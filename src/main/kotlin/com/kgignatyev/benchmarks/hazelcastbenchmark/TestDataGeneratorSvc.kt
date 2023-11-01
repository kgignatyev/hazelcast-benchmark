package com.kgignatyev.benchmarks.hazelcastbenchmark

import com.fasterxml.jackson.databind.ObjectMapper
import com.kgignatyev.benchmarks.hazelcastbenchmark.vo.BMCompany
import com.kgignatyev.benchmarks.hazelcastbenchmark.vo.BMEmployee
import net.datafaker.Faker
import org.springframework.stereotype.Service
import java.io.FileWriter


@Service
class TestDataGeneratorSvc(val om: ObjectMapper) {

    fun generateTestData(numEntries: Int, df: Faker = Faker(), entryCallback: (BMCompany) -> Unit) {
        (1..numEntries).forEach {
            val c = df.company()
            val bmc = BMCompany()
            bmc.id = it.toString()
            bmc.name = c.name()
            bmc.catchPhrase = c.catchPhrase()
            bmc.url = c.url()
            bmc.industry = c.industry()
            (1..10).forEach { _ ->
                val e = df.name()
                val bme = BMEmployee()
                bme.fullName = e.fullName()
                bme.title = e.title()
                bmc.employees.add(bme)
            }

            entryCallback(bmc)
        }
    }

    fun generateTestDataFile(numEntries: Int) {
        println("Generating $numEntries entries")
        FileWriter(BenchmarkConfig.testDataFileName).use { fw ->
            generateTestData(numEntries) { bmc ->
                fw.write(om.writeValueAsString(bmc))
                fw.write("\n")
            }
        }
        println("Done")
    }
}
