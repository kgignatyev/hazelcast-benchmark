package com.kgignatyev.benchmarks.hazelcastbenchmark


interface Benchmark<T> {
    fun run( c:SearchCriteria ):List<T>
    fun loadTestData()
    fun name():String
}

data class SortCriteria(val field: String, val order: String)
data class PropertyCriteria(val field: String, val value: String, val op:String = "eq" )
data class SearchCriteria(
    val searchBy:List<PropertyCriteria>,
    val sortBy:List<SortCriteria> = listOf(SortCriteria("name", "asc")),
)
