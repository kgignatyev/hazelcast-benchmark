package com.kgignatyev.benchmarks.hazelcastbenchmark.cases

import com.hazelcast.map.IMap
import com.hazelcast.query.Predicate
import com.kgignatyev.benchmarks.hazelcastbenchmark.*
import org.springframework.stereotype.Component

abstract class BaseObjectCacheSvc(val imap: IMap<String, BMCompany>,  val cacheSvc: CacheSvc, val tdl: TestDataLoader,
 val predicateBuilder:(SearchCriteria)-> Predicate<String, BMCompany>
):Benchmark<BMCompany> {


    override fun run(c: SearchCriteria): List<BMCompany> {
        val predicate = predicateBuilder(c)
        return cacheSvc.searchCache(imap, predicate)
    }

    override fun loadTestData() {
        tdl.loadObjectData{
            imap.put(it.id, it)
        }
    }


}
