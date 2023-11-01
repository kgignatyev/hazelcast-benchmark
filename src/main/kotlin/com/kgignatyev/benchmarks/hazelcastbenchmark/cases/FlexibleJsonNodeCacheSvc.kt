package com.kgignatyev.benchmarks.hazelcastbenchmark.cases

import com.fasterxml.jackson.databind.node.ObjectNode
import com.hazelcast.query.Predicate
import com.hazelcast.query.Predicates
import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.JsonPath
import com.kgignatyev.benchmarks.hazelcastbenchmark.*
import com.kgignatyev.benchmarks.hazelcastbenchmark.BenchmarkConfig.Companion.jsonProvider
import org.springframework.stereotype.Component
import java.io.Externalizable
import java.io.ObjectInput
import java.io.ObjectOutput



class FlexibleJsonNode(var json:String):Externalizable{
    constructor():this(""   )

    val jsonNode: Any by lazy  { jsonProvider.parse(json)  }
    override fun writeExternal(out: ObjectOutput) {
        out.writeUTF(json)
    }

    override fun readExternal(oin: ObjectInput) {
        json = oin.readUTF()
    }
}

@Component
class FlexibleJsonPredicatesSvc(val cacheSvc: CacheSvc, val tdl: TestDataLoader, ): Benchmark<FlexibleJsonNode> {

    val imap = cacheSvc.flexibleJsonNodeMap


    override fun run(c: SearchCriteria): List<FlexibleJsonNode> {

        val orgNameCriteria = c.searchBy[0].value
        val employeeNameCriteria = c.searchBy[1].value
        val predicate =  Predicates.and<String,FlexibleJsonNode>(
            JsonPathContainsPredicate("$.name", orgNameCriteria),
            JsonPathContainsPredicate("$.employees[*].fullName", employeeNameCriteria),
        )
        return cacheSvc.searchCache(imap, predicate)
    }

    override fun loadTestData() {
        var counter = 0
        tdl.loadJsonData {
            counter++
            imap.put(counter.toString(),  FlexibleJsonNode(it))
        }
    }

    override fun name(): String {
        return "flexible json predicates"
    }

    override fun size(): Int {
        return imap.size
    }
}


abstract class JsonPathPredicate(var jsonPath:String): Predicate<String, FlexibleJsonNode>, Externalizable {

    constructor():this("")

    val jsonPathCompiled by lazy {JsonPath.compile(jsonPath)}

    override fun apply(mapEntry: MutableMap.MutableEntry<String, FlexibleJsonNode>): Boolean {
        val v:Any = jsonPathCompiled.read( mapEntry.value.jsonNode )
        return applyToValue(v)
    }

    abstract fun applyToValue(v: Any): Boolean

    override fun writeExternal(out: ObjectOutput) {
        out.writeUTF(jsonPath)
    }

    override fun readExternal(oin: ObjectInput) {
        jsonPath = oin.readUTF()
    }


}

class JsonPathContainsPredicate(jsonPath: String, var partialString:String): JsonPathPredicate(jsonPath) {

    constructor():this("", "")
    override fun applyToValue(v: Any): Boolean {
        if (v is List<*>) {
            return v.find { it.toString().contains(partialString) } != null
        }
        return v.toString().contains(partialString)
    }

    override fun writeExternal(out: ObjectOutput) {
        super.writeExternal(out)
        out.writeUTF(partialString)
    }

    override fun readExternal(oin: ObjectInput) {
        super.readExternal(oin)
        partialString = oin.readUTF()
    }
}
