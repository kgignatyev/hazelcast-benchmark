package com.kgignatyev.benchmarks.hazelcastbenchmark

import com.fasterxml.jackson.databind.ObjectMapper
import com.kgignatyev.benchmarks.hazelcastbenchmark.vo.BMCompany
import org.springframework.stereotype.Component
import java.io.FileReader

@Component
class TestDataLoader( val om:ObjectMapper) {

    fun convertToBMC( json:String): BMCompany {
        return om.readValue(json, BMCompany::class.java)
    }

    fun loadObjectData( callback:(BMCompany)->Unit) {
        loadJsonData{
            callback(convertToBMC(it))
        }
    }

    fun loadJsonData( callback:(String)->Unit) {
        var count = 0
        FileReader( BenchmarkConfig.testDataFileName ).useLines { lines ->
            lines.forEach {
                count++
                callback(it)
            }
        }
        println("Loaded $count entries")
    }
}
