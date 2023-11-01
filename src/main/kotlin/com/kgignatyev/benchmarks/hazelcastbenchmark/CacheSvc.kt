package com.kgignatyev.benchmarks.hazelcastbenchmark

import com.fasterxml.jackson.databind.ObjectMapper
import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.HazelcastJsonValue
import com.hazelcast.map.IMap
import com.hazelcast.query.Predicate
import com.hazelcast.query.Predicates
import com.kgignatyev.benchmarks.hazelcastbenchmark.HzConfig.Companion.allInMemoryCacheName
import com.kgignatyev.benchmarks.hazelcastbenchmark.HzConfig.Companion.customJsonNodeMapName
import com.kgignatyev.benchmarks.hazelcastbenchmark.HzConfig.Companion.flexibleJsonNodeMapName
import com.kgignatyev.benchmarks.hazelcastbenchmark.HzConfig.Companion.jsonNodeMapName
import com.kgignatyev.benchmarks.hazelcastbenchmark.cases.CustomJsonNode
import com.kgignatyev.benchmarks.hazelcastbenchmark.cases.FlexibleJsonNode
import com.kgignatyev.benchmarks.hazelcastbenchmark.vo.BMCompany
import com.kgignatyev.benchmarks.hazelcastbenchmark.hz.BMCompanyContainsPredicate
import com.kgignatyev.benchmarks.hazelcastbenchmark.hz.StingArrayContainsPredicate
import jakarta.annotation.PostConstruct
import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component


@Component
class CacheSvc(val hz: HazelcastInstance, val om: ObjectMapper) : ApplicationListener<ApplicationEvent> {
    override fun onApplicationEvent(event: ApplicationEvent) {
        println("event: $event")
    }

    lateinit var allInMemoryCache: IMap<String, BMCompany>
    lateinit var jsonNodeMap: IMap<String, HazelcastJsonValue>
    lateinit var customJsonNodeMap: IMap<String, CustomJsonNode>
    lateinit var flexibleJsonNodeMap: IMap<String, FlexibleJsonNode>

    @PostConstruct
    fun init() {
        allInMemoryCache = getMap(allInMemoryCacheName)
        jsonNodeMap = getMap(jsonNodeMapName)
        customJsonNodeMap = getMap(customJsonNodeMapName)
        flexibleJsonNodeMap = getMap(flexibleJsonNodeMapName)
    }

    fun <T> getMap(name: String): IMap<String, T> {
        val map: IMap<String, T> = hz.getMap(name)
        val cfg = hz.config.mapConfigs[name]
        if (cfg != null) {
            println("map config: " + om.writer().withDefaultPrettyPrinter().writeValueAsString(cfg))
        }
        return map
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

    fun <T> buildHzLikePredicate(c: SearchCriteria): Predicate<String, T> {
        val orgNameCriteria = c.searchBy[0].value
        val employeeNameCriteria = c.searchBy[1].value
        return Predicates.and(
            Predicates.like<String, T>("employeeNames", "%${employeeNameCriteria}%"),
            Predicates.like<String, T>("name", "%${orgNameCriteria}%")
        )
    }

    fun buildCustomPredicate(c: SearchCriteria): Predicate<String, BMCompany> {
        val orgNameCriteria = c.searchBy[0].value
        val employeeNameCriteria = c.searchBy[1].value
        return Predicates.and(
            BMCompanyContainsPredicate("name", orgNameCriteria),
            StingArrayContainsPredicate("employeeNames", employeeNameCriteria ),
        )
    }

}

class IDComparator<T> : Comparator<MutableMap.MutableEntry<String, T>>, java.io.Serializable {
    override fun compare(o1: MutableMap.MutableEntry<String, T>, o2: MutableMap.MutableEntry<String, T>): Int {
        return o1.key.compareTo(o2.key)
    }

}
