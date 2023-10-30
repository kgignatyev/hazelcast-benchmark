package com.kgignatyev.benchmarks.hazelcastbenchmark

import com.kgignatyev.benchmarks.hazelcastbenchmark.cases.*
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
			  exit, 
			  generate-data N, 
			  test-list, 
			  test-object-cache
			  test-object-cache-w-custom-predicate,
			  test-default-json-node-cache, 
			  test-custom-json-node-cache""".trimIndent())
		val parts = readln().trim().split(" ")
		cmd = parts[0]
		when(cmd){
			"exit" -> proceed = false
			"generate-data" -> {
				val svc = cfg.getBean(TestDataGeneratorSvc::class.java)
				svc.generateTestData(parts[1].toInt())
			}
			"test-list" -> {
				val svc = cfg.getBean(TestListSvc::class.java)
				runBenchmark(svc)
			}
			"test-object-cache" -> {
				val svc = cfg.getBean(ObjectCacheSvc::class.java)
				runBenchmark(svc)
			}
			"test-object-cache-w-custom-predicate" -> {
				val svc = cfg.getBean(ObjectCacheCustomPredicateSvc::class.java)
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
			else -> println("unknown command")
		}
	}
}

fun <V> runBenchmark(svc: Benchmark<V>) {
	val c = createSearchCriteria()
	val testName = svc.name()
	val loadTime = measureNanoTime { svc.loadTestData() }
	var numResults = 0
	val tn = testName.padEnd(35)
	val lt = loadTime.toString().padStart(20)
	println( "$tn load time: $lt ns" )
	var totalTime = 0L
	var runCount = 0
	(1..20).forEach {
		val runTime = measureNanoTime { numResults = svc.run(c).size }
		reportResults(it, tn, runTime, numResults)
		if( it > 2){//lets ignore couple warm ups, as CustomJson is severely affected
			totalTime+= runTime
			runCount++
		}
	}
	val avgTime = totalTime/runCount
	println("$tn avg time: $avgTime ns or ${avgTime/1000000} ms")

}

fun reportResults(i: Int, testName: String, runTime: Long, numResults: Int) {

	val rt = runTime.toString().padStart(20)
	val runId = i.toString().padStart(3)
	println( "$testName $runId $rt ns found: $numResults" )
}

fun createSearchCriteria(): SearchCriteria {
	return  SearchCriteria(
		listOf(
			PropertyCriteria("name", "rr", "contains"),
			PropertyCriteria("employeeNames", "ok", "contains"),
		)
	)

}
