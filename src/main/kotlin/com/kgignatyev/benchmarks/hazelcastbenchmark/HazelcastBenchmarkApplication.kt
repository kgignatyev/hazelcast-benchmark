package com.kgignatyev.benchmarks.hazelcastbenchmark

import com.kgignatyev.benchmarks.hazelcastbenchmark.cases.ObjectCacheCustomPredicateSvc
import com.kgignatyev.benchmarks.hazelcastbenchmark.cases.ObjectCacheSvc
import com.kgignatyev.benchmarks.hazelcastbenchmark.cases.TestListSvc
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
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
		println("enter command: exit, test-list, test-object-cache, test-object-cache-w-custom-predicate")
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
			"test-object-cache-w-custom-predicate" -> {
				val svc = cfg.getBean(ObjectCacheCustomPredicateSvc::class.java)
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
	val tn = testName.padEnd(35)
	val lt = loadTime.toString().padStart(20)
	println( "$tn load time: $lt ns" )
	(1..20).forEach {
		val runTime = measureNanoTime { numResults = svc.run(c).size }
		reportResults(it, tn, runTime, numResults)
	}

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
		)
	)

}
