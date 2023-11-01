package com.kgignatyev.benchmarks.hazelcastbenchmark

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.jayway.jsonpath.spi.json.JacksonJsonProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File
import java.time.LocalDateTime


@Configuration
class BenchmarkConfig {




    @Bean
    fun om(): ObjectMapper {
        return objMapper
    }


    companion object{
        val testDataFileName = "data/companies.json"
        val objMapper = jacksonObjectMapper()

        val jsonProvider = JacksonJsonProvider( objMapper)
        val resultsDir = File("results").apply {
            mkdirs()
        }
        val resultsFile = File(resultsDir,"results-${LocalDateTime.now()}.csv").apply {
            appendText("test name, N, time ms, time ns, N results\n")
        }
        val sortResultsFile = File(resultsDir,"list-sort-results-${LocalDateTime.now()}.csv").apply {
            appendText("N, time ms, time ns, N results\n")
        }
    }
}
