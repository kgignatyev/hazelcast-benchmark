package com.kgignatyev.benchmarks.hazelcastbenchmark.cases

import com.kgignatyev.benchmarks.hazelcastbenchmark.*
import org.springframework.stereotype.Component


@Component
class ObjectCacheSvc(val cacheSvc: CacheSvc,val tdl: TestDataLoader):Benchmark<BMCompany> {


    override fun run(c: SearchCriteria): List<BMCompany> {
        val predicate = cacheSvc.buildPredicate(c)
        return cacheSvc.searchCache(cacheSvc.allInMemoryCache, predicate)
    }

    override fun loadTestData() {
        tdl.loadObjectData{
            cacheSvc.allInMemoryCache.put(it.id, it)
        }
    }

    override fun name(): String {
        return "object cache"
    }
}
