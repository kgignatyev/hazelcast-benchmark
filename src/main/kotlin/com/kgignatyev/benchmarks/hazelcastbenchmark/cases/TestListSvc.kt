package com.kgignatyev.benchmarks.hazelcastbenchmark.cases

import com.kgignatyev.benchmarks.hazelcastbenchmark.vo.BMCompany
import com.kgignatyev.benchmarks.hazelcastbenchmark.Benchmark
import com.kgignatyev.benchmarks.hazelcastbenchmark.SearchCriteria
import com.kgignatyev.benchmarks.hazelcastbenchmark.TestDataLoader
import org.springframework.stereotype.Service


@Service
class TestListSvc(val tdl: TestDataLoader) : Benchmark<BMCompany> {

    var useSort: Boolean = true
    val list = mutableListOf<BMCompany>()

    override fun run(c: SearchCriteria): List<BMCompany> {
        val nameCriteria = c.searchBy[0].value
        val employeeNameCriteria = c.searchBy[1].value
        val filteredList = list.filter { org ->
            org.name.contains(nameCriteria) && (org.employees.find {
                it.fullName.contains(employeeNameCriteria)
            } != null)
        }
        return if (useSort) {
            filteredList
                .sortedBy { c.sortBy.first.field }
        } else {
            filteredList
        }

    }

    override fun loadTestData() {
        list.clear()
        tdl.loadObjectData { list.add(it) }
    }

    override fun name(): String {
        return "simple list" + if (useSort) "" else " no sort"
    }

    override fun size(): Int {
        return list.size
    }
}
