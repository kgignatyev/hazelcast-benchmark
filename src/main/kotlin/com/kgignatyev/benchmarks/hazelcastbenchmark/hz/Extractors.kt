package com.kgignatyev.benchmarks.hazelcastbenchmark.hz

import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.hazelcast.query.extractor.ValueCollector
import com.hazelcast.query.extractor.ValueExtractor
import com.hazelcast.query.extractor.ValueReader
import com.kgignatyev.benchmarks.hazelcastbenchmark.vo.BMCompany
import com.kgignatyev.benchmarks.hazelcastbenchmark.cases.CustomJsonNode


/**
 * note: this does not work without custom serializer, see bug
 * https://github.com/hazelcast/hazelcast/issues/25851
 */
class EmployeeNamesExtractorForObject: ValueExtractor<BMCompany, String> {
    override fun extract(target: BMCompany?, argument: String?, collector: ValueCollector<Any>) {
        if( target == null ) return
        target.employees.forEach {
            collector.addObject( it.fullName )
        }
    }
}

class EmployeeNamesExtractorForCustomJsonNode: ValueExtractor<CustomJsonNode, String> {
    override fun extract(target: CustomJsonNode?, argument: String?, collector: ValueCollector<Any>) {
        if( target == null ) return
        val employees = target.jsonNode.get("employees") as ArrayNode
        employees.forEach {
            collector.addObject( (it as ObjectNode).get("fullName").asText() )
        }
    }
}

/**
 * Hazelcast refuses to use extractor for HazelcastJsonValue
 */
class EmployeeNamesExtractorForHzJsonNode: ValueExtractor<Any, String> {
    override fun extract(target: Any?, argument: String?, collector: ValueCollector<Any>) {
        if( target == null ) return
        val r = (target as ValueReader )
//        println(r)
//        val employees = BenchmarkConfig.objMapper.readTree( target.value).get("employees") as ArrayNode
//        employees.forEach {
//            collector.addObject( (it as ObjectNode).get("fullName").asText() )
//        }
    }
}
