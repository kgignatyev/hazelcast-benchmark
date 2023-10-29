package com.kgignatyev.benchmarks.hazelcastbenchmark

import com.fasterxml.jackson.databind.ObjectMapper
import com.hazelcast.core.HazelcastInstance
import com.hazelcast.map.IMap
import com.hazelcast.query.Predicate
import com.hazelcast.query.Predicates
import com.kgignatyev.benchmarks.hazelcastbenchmark.HzConfig.Companion.allInMemoryCacheName
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component


@Component
class CacheSvc( val hz: HazelcastInstance, val om:ObjectMapper ) : ApplicationListener<ApplicationEvent> {
    override fun onApplicationEvent(event: ApplicationEvent) {
        println("event: $event")
    }

    lateinit var allInMemoryCache: IMap<String, BMCompany>

    @PostConstruct
    fun init() {
        allInMemoryCache = hz.getMap(allInMemoryCacheName)
        val mapConf = hz.config.mapConfigs[allInMemoryCacheName]
        val cfg = om.writer().withDefaultPrettyPrinter().writeValueAsString(mapConf)
        println("mapConf: $cfg")
    }


    fun <T : Any> searchCache(
        map: IMap<String, T>,
        predicate: Predicate<String, T>?
    ): List<T> {
        val comparator = IDComparator<T>()
        val pagingPredicate = Predicates.pagingPredicate(
                predicate,
                comparator, 1000000
            )

        val results: Collection<T> = map.values(pagingPredicate)

        return results.toList()
    }

    fun buildPredicate(c: SearchCriteria): Predicate<String, BMCompany> {
        return Predicates.like( "name", "%${c.searchBy.first.value}%")
    }

    fun buildCustomPredicate(c: SearchCriteria): Predicate<String, BMCompany> {
        return ContainsPredicate( "name",c.searchBy.first.value )
    }

}

class IDComparator<T>:Comparator<MutableMap.MutableEntry<String, T>>,java.io.Serializable {
    override fun compare(o1: MutableMap.MutableEntry<String, T>, o2: MutableMap.MutableEntry<String, T>): Int {
        return o1.key.compareTo( o2.key)
    }

}
