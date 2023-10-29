package com.kgignatyev.benchmarks.hazelcastbenchmark

import com.kgignatyev.benchmarks.hazelcastbenchmark.cases.ObjectCacheSvc
import com.kgignatyev.benchmarks.hazelcastbenchmark.cases.TestListSvc
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.ConfigurableApplicationContext
import kotlin.system.exitProcess
import kotlin.system.measureNanoTime

@SpringBootApplication
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
		println("enter command: exit, test-list, test-object-cache")
		cmd = readln()
		when(cmd){
			"exit" -> proceed = false
			"test-list" -> {
				val svc = cfg.getBean(TestListSvc::class.java)
				runBenchmark(svc)
			}
			"test-object-cache" -> {
				val svc = cfg.getBean(ObjectCacheSvc::class.java)
				runBenchmark(svc)
			}
			else -> println("unknown command")
		}
	}
}

fun runBenchmark(svc: Benchmark<BMCompany>) {
	val c = createSearchCriteria()
	val testName = svc.name()
	val loadTime = measureNanoTime { svc.loadTestData() }
	var numResults = 0
	(1..20).forEach {
		val runTime = measureNanoTime { numResults = svc.run(c).size }
		reportResults(it, testName, loadTime, runTime, numResults)
	}

}

fun reportResults(i: Int, testName: String, loadTime: Long, runTime: Long, numResults: Int) {
	val lt = loadTime.toString().padStart(20)
	val rt = runTime.toString().padStart(20)
	val tn = testName.padEnd(20)
	val runId = i.toString().padStart(3)
	println( "$tn $runId $lt ns $rt ns found: $numResults" )
}

fun createSearchCriteria(): SearchCriteria {
	return  SearchCriteria(
		listOf(
			PropertyCriteria("name", "rr", "contains"),
		)
	)

}
