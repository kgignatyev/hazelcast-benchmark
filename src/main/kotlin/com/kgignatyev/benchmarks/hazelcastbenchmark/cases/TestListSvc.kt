package com.kgignatyev.benchmarks.hazelcastbenchmark.cases

import com.kgignatyev.benchmarks.hazelcastbenchmark.BMCompany
import com.kgignatyev.benchmarks.hazelcastbenchmark.Benchmark
import com.kgignatyev.benchmarks.hazelcastbenchmark.SearchCriteria
import com.kgignatyev.benchmarks.hazelcastbenchmark.TestDataLoader
import org.springframework.stereotype.Service


@Service
class TestListSvc(val tdl: TestDataLoader) :Benchmark<BMCompany> {

    val list = mutableListOf<BMCompany>()

    override fun run(c: SearchCriteria): List<BMCompany> {
        val nameCriteria = c.searchBy[0].value
        val employeeNameCriteria = c.searchBy[1].value
        return list.filter { org -> org.name.contains(nameCriteria) && (org.employees.find { it.fullName.contains(employeeNameCriteria)  }!= null) }
            .sortedBy{ c.sortBy.first.field }
    }

    override fun loadTestData() {
        list.clear()
        tdl.loadObjectData { list.add(it) }
    }

    override fun name(): String {
        return "simple list"
    }
}
