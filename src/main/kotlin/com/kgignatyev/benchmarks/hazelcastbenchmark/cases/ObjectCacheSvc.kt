package com.kgignatyev.benchmarks.hazelcastbenchmark.cases

import com.kgignatyev.benchmarks.hazelcastbenchmark.*
import org.springframework.stereotype.Component


@Component
class ObjectCacheSvc( cacheSvc: CacheSvc, tdl: TestDataLoader):BaseObjectCacheSvc(cacheSvc.allInMemoryCache,cacheSvc,tdl, cacheSvc::buildPredicate){

    override fun name(): String {
        return "object cache"
    }
}
