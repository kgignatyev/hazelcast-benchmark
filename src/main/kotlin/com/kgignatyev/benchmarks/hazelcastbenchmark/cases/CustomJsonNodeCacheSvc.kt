package com.kgignatyev.benchmarks.hazelcastbenchmark.cases

import com.fasterxml.jackson.databind.node.ObjectNode
import com.hazelcast.core.HazelcastJsonValue
import com.hazelcast.query.Predicate
import com.hazelcast.query.Predicates
import com.kgignatyev.benchmarks.hazelcastbenchmark.*
import com.kgignatyev.benchmarks.hazelcastbenchmark.BenchmarkConfig.Companion.objMapper
import org.springframework.stereotype.Component
import java.io.Externalizable
import java.io.ObjectInput
import java.io.ObjectOutput


class CustomJsonNode(var json:String):Externalizable{

    constructor():this(""   )

    val jsonNode:ObjectNode by lazy  { objMapper.readTree(json) as ObjectNode }
    override fun writeExternal(out: ObjectOutput) {
        out.writeUTF(json)
    }

    override fun readExternal(`in`: ObjectInput) {
        json = `in`.readUTF()
    }
}

@Component
class CustomJsonNodeCacheSvc(val cacheSvc: CacheSvc, val tdl: TestDataLoader, ):Benchmark<CustomJsonNode> {


    override fun run(c: SearchCriteria): List<CustomJsonNode> {

        val orgNameCriteria = c.searchBy[0].value
        val employeeNameCriteria = c.searchBy[1].value
        val predicate =  Predicates.and<String,CustomJsonNode>(
            CustomJsonContainsPredicate("name", orgNameCriteria),
            StingArrayContainsPredicate("employeeNames", employeeNameCriteria),
        )
        return cacheSvc.searchCache(cacheSvc.customJsonNodeMap, predicate)
    }

    override fun loadTestData() {
        var counter = 0
        tdl.loadJsonData {
            counter++
            val json = CustomJsonNode(it)
            cacheSvc.customJsonNodeMap.put(counter.toString(), json)
        }
    }

    override fun name(): String {
        return "custom json node cache"
    }


}


class CustomJsonContainsPredicate(val fieldName:String, val value:String): Predicate<String, CustomJsonNode> {
    override fun apply(entry: MutableMap.MutableEntry<String, CustomJsonNode>?): Boolean {
        if (entry == null) return false
        val node = entry.value.jsonNode
        val fieldValue = node.get(fieldName).asText()
        return fieldValue.contains(value)
    }
}
