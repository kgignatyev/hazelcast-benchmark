package com.kgignatyev.benchmarks.hazelcastbenchmark

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.jayway.jsonpath.spi.json.JacksonJsonProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


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
    }
}
