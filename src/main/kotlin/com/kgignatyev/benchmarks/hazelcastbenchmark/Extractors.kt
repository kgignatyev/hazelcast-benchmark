package com.kgignatyev.benchmarks.hazelcastbenchmark

import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.hazelcast.query.extractor.ValueCollector
import com.hazelcast.query.extractor.ValueExtractor
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
