package com.kgignatyev.benchmarks.hazelcastbenchmark

import com.kgignatyev.benchmarks.hazelcastbenchmark.BenchmarkConfig.Companion.resultsFile
import com.kgignatyev.benchmarks.hazelcastbenchmark.BenchmarkConfig.Companion.sortResultsFile
import com.kgignatyev.benchmarks.hazelcastbenchmark.cases.*
import com.kgignatyev.benchmarks.hazelcastbenchmark.vo.BMCompany
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.ConfigurableApplicationContext
import kotlin.system.exitProcess
import kotlin.system.measureNanoTime

@SpringBootApplication( exclude = [org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration::class] )

class HazelcastBenchmarkApplication

fun main(args: Array<String>) {
	try {
		val cfg = SpringApplication.run(HazelcastBenchmarkApplication::class.java, *args)
		runUserCommands( cfg )
	}catch (e: Exception) {
		e.printStackTrace()
	}
	exitProcess(0)
}

fun runUserCommands(cfg: ConfigurableApplicationContext) {
	var cmd = ""
	var proceed = true
	while (proceed){
		println("""
			enter command: 
			  exit 
			  generate-data N, 
			  test-list 
			  test-list-full
			  test-object-cache
			  test-object-cache-w-custom-predicate
			  test-object-cache-w-empty-predicate
			  test-default-json-node-cache 
			  test-custom-json-node-cache
			  test-flexible-json-node-cache			  
			  """.trimIndent())
		val parts = readln().trim().split(" ")
		cmd = parts[0]
		when(cmd){
			"exit" -> proceed = false
			"generate-data" -> {
				val svc = cfg.getBean(TestDataGeneratorSvc::class.java)
				svc.generateTestDataFile(parts[1].toInt())
			}
			"test-list" -> {
				val svc = cfg.getBean(TestListSvc::class.java)
				runBenchmark(svc)
			}
			"test-list-full" -> {
				runFullListTest(cfg)
			}
			"test-object-cache" -> {
				val svc = cfg.getBean(ObjectCacheSvc::class.java)
				runBenchmark(svc)
			}
			"test-object-cache-w-custom-predicate" -> {
				val svc = cfg.getBean(ObjectCacheCustomPredicateSvc::class.java)
				runBenchmark(svc)
			}
			"test-object-cache-w-empty-predicate" -> {
				val svc = cfg.getBean(ObjectCacheEmptyPredicateSvc::class.java)
				runBenchmark(svc)
			}
			"test-default-json-node-cache" -> {
				val svc = cfg.getBean(HazelcastJsonNodeCacheSvc::class.java)
				runBenchmark(svc)
			}
			"test-custom-json-node-cache" -> {
				val svc = cfg.getBean(CustomJsonNodeCacheSvc::class.java)
				runBenchmark(svc)
			}
			"test-flexible-json-node-cache" -> {
				val svc = cfg.getBean(FlexibleJsonPredicatesSvc::class.java)
				runBenchmark(svc)
			}
			else -> println("unknown command")
		}
	}
}

fun runFullListTest(cfg: ConfigurableApplicationContext) {
	val listSvc = cfg.getBean(TestListSvc::class.java)
	println("This test run scenarios with variable number of entries")
	val testDataGeneratorSvc = cfg.getBean(TestDataGeneratorSvc::class.java)
	val numEntries = listOf( 10000, 100000, 500000,1000000 )
	numEntries.forEach { numEntries->
		listSvc.list.clear()
		println("Generating test data for $numEntries entries")
		testDataGeneratorSvc.generateTestData(numEntries){bmc->
			listSvc.list.add(bmc)
		}
		println("Loaded $numEntries entries")
		listSvc.useSort = true
		runBenchmark(listSvc, true )
		listSvc.useSort = false
		runBenchmark(listSvc, true )
	}

	numEntries.forEach {
		testListSorting( it, listSvc.list )
	}
	listSvc.list.clear()
}

fun testListSorting(numEntriesToSort: Int, list: MutableList<BMCompany>) {
	val numOfTries = 5
	(1..numOfTries).forEach { n ->
		val listToSort = list.take(numEntriesToSort)
		val sortTime = measureNanoTime {
			listToSort.sortedBy { it.name }
		}
		println("Sorting $numEntriesToSort entries took $sortTime ns or ${sortTime / 1000000} ms")
		if( n == numOfTries) {
			sortResultsFile.appendText("$numEntriesToSort, ${sortTime / 1000000}, $sortTime\n")
		}
	}
}

fun <V> runBenchmark(svc: Benchmark<V>, skipLoad:Boolean = false) {
	val c = createSearchCriteria()
	val testName = svc.name()
	val tn = testName.padEnd(35)
	if( !skipLoad ) {
		val loadTime = measureNanoTime { svc.loadTestData() }
		val lt = loadTime.toString().padStart(20)
		println("$tn load time: $lt ns")
	}
	val dataSetSize = svc.size()
	var numResults = 0
	var totalTime = 0L
	var runCount = 0
	(1..20).forEach {
		val runTime = measureNanoTime { numResults = svc.run(c).size }
		reportResults(it, tn, runTime, numResults)
		if( it > 2){//let's ignore a couple of warmups, as CustomJson is severely affected
			totalTime+= runTime
			runCount++
		}
	}
	val avgTime = totalTime/runCount
	println("$tn avg time: ${avgTime/1000000} ms or $avgTime ns for $dataSetSize entries")
	resultsFile.appendText("$tn,  $dataSetSize, ${avgTime/1000000}, $avgTime, $numResults\n")

}

fun reportResults(i: Int, testName: String, runTime: Long, numResults: Int) {

	val rt = runTime.toString().padStart(20)
	val rtMs = (runTime/1000000).toString().padStart(5)
	val runId = i.toString().padStart(3)
	println( "$testName $runId $rt ns or ${rtMs} ms found: $numResults" )
}

fun createSearchCriteria(): SearchCriteria {
	return  SearchCriteria(
		listOf(
			PropertyCriteria("name", "rr", "contains"),
			PropertyCriteria("employeeNames", "ok", "contains"),
		)
	)
}
