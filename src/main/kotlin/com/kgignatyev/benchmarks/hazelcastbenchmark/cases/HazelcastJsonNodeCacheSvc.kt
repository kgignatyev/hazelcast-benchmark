package com.kgignatyev.benchmarks.hazelcastbenchmark.cases

import com.hazelcast.core.HazelcastJsonValue
import com.kgignatyev.benchmarks.hazelcastbenchmark.*
import org.springframework.stereotype.Component

@Component
class HazelcastJsonNodeCacheSvc(val cacheSvc: CacheSvc, val tdl: TestDataLoader, ):Benchmark<HazelcastJsonValue> {


    override fun run(c: SearchCriteria): List<HazelcastJsonValue> {
        val predicate = cacheSvc.buildHzLikePredicate<HazelcastJsonValue>(c)
        return cacheSvc.searchCache(cacheSvc.jsonNodeMap, predicate)
    }

    override fun loadTestData() {
        var counter = 0
        tdl.loadJsonData {
            counter++
            val json = HazelcastJsonValue(it)
            cacheSvc.jsonNodeMap.put(counter.toString(), json)
        }
    }

    override fun name(): String {
        return "hazelcast json node cache"
    }


}
