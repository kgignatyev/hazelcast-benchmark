package com.kgignatyev.benchmarks.hazelcastbenchmark

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import java.io.FileReader

@Component
class TestDataLoader( val om:ObjectMapper) {

    fun convertToBMC( json:String):BMCompany {
        return om.readValue(json, BMCompany::class.java)
    }

    fun loadObjectData( callback:(BMCompany)->Unit) {
        loadJsonData{
            callback(convertToBMC(it))
        }
    }

    fun loadJsonData( callback:(String)->Unit) {
        FileReader( BenchmarkConfig.testDataFileName ).useLines { lines ->
            lines.forEach {
                callback(it)
            }
        }
    }
}
