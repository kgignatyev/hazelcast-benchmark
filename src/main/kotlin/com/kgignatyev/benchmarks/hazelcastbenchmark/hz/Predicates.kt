package com.kgignatyev.benchmarks.hazelcastbenchmark.hz

import com.hazelcast.query.Predicate
import com.hazelcast.query.impl.Extractable
import com.hazelcast.query.impl.getters.MultiResult
import com.kgignatyev.benchmarks.hazelcastbenchmark.vo.BMCompany


class BMCompanyContainsPredicate(val field: String, val value: String) : Predicate<String, BMCompany> {
    override fun apply(mapEntry: MutableMap.MutableEntry<String, BMCompany>?): Boolean {
        val r = mapEntry?.value?.let {
            when (field) {
                "name" -> it.name.contains(value)
                "industry" -> it.industry.contains(value)
                "url" -> it.url.contains(value)
                "catchPhrase" -> it.catchPhrase.contains(value)
                else -> false
            }
        } ?: false
//        println("apply $field $value $r")
        return r
    }

}

abstract class CustomPredicate<T:Any>( val propertyName: String):Predicate<String, T> {
    private fun readAttributeValue(entry: Map.Entry<*, *>): Any? {
        val extractable = entry as Extractable
        return extractable.getAttributeValue(propertyName)
    }

    override fun apply(entry: Map.Entry<String, T>?): Boolean {
        if (entry == null) {
            return false
        }
        val v = readAttributeValue(entry) ?: return false
        return applyForSingleAttributeValue(v)
    }
    abstract fun applyForSingleAttributeValue(v: Any?): Boolean
}

abstract class ListPredicate<T:Any>( propertyName: String, val v: Any) : CustomPredicate<T>(propertyName) {
    override fun applyForSingleAttributeValue(vals:Any?): Boolean {
        if (vals == null) {
            return false
        }
        val entryValues = when (vals) {
            is MultiResult<*> -> vals.results.toSet()  as Set<Any>
            else -> setOf(vals)
        }

        return applyPredicate(entryValues)
    }
    abstract fun applyPredicate(entryValues: Set<Any>?): Boolean
}

class StingArrayContainsPredicate( field: String,  partialValue: String) : ListPredicate<String>(field, partialValue) {
    override fun applyPredicate(entryValues: Set<Any>?): Boolean {
        if (entryValues == null) {
            return false
        }
        return entryValues.find{  ev -> ev.toString().contains(v as String) } != null
    }
}
