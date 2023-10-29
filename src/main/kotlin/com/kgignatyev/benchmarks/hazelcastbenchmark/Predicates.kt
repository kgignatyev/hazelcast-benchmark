package com.kgignatyev.benchmarks.hazelcastbenchmark

import com.hazelcast.query.Predicate


class ContainsPredicate(val field: String, val value: String) : Predicate<String,BMCompany> {
    override fun apply(mapEntry: MutableMap.MutableEntry<String, BMCompany>?): Boolean {
        return mapEntry?.value?.let {
            when (field) {
                "name" -> it.name.contains(value)
                "industry" -> it.industry.contains(value)
                "url" -> it.url.contains(value)
                "catchPhrase" -> it.catchPhrase.contains(value)
                else -> false
            }
        } ?: false
    }

}