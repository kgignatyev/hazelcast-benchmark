package com.kgignatyev.benchmarks.hazelcastbenchmark.cases

import com.kgignatyev.benchmarks.hazelcastbenchmark.CacheSvc
import com.kgignatyev.benchmarks.hazelcastbenchmark.TestDataLoader
import org.springframework.stereotype.Component


@Component
class ObjectCacheCustomPredicateSvc(cacheSvc: CacheSvc, tdl: TestDataLoader):BaseObjectCacheSvc(cacheSvc.allInMemoryCache,cacheSvc,tdl, cacheSvc::buildCustomPredicate){

    override fun name(): String {
        return "object cache w custom predicate"
    }

}

@Component
class ObjectCacheEmptyPredicateSvc(cacheSvc: CacheSvc, tdl: TestDataLoader):BaseObjectCacheSvc(cacheSvc.allInMemoryCache,cacheSvc,tdl, cacheSvc::buildEmptyPredicate){

    override fun name(): String {
        return "object cache w empty predicate"
    }

}
