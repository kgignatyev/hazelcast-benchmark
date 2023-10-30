package com.kgignatyev.benchmarks.hazelcastbenchmark

import com.fasterxml.jackson.databind.ObjectMapper
import net.datafaker.Faker
import org.springframework.stereotype.Service
import java.io.FileWriter


@Service
class TestDataGeneratorSvc(val om: ObjectMapper) {

    fun generateTestData(numEntries: Int) {
        println("Generating $numEntries entries")
        val df = Faker()
         FileWriter( BenchmarkConfig.testDataFileName).use { fw->
             (1..numEntries).forEach {

                 val c = df.company()
                 val bmc = BMCompany()
                 bmc.id = it.toString()
                 bmc.name = c.name()
                 bmc.catchPhrase = c.catchPhrase()
                 bmc.url = c.url()
                 bmc.industry = c.industry()
                    (1..10).forEach {
                        val e = df.name()
                        val bme = BMEmployee()
                        bme.fullName = e.fullName()
                        bme.title = e.title()
                        bmc.employees.add(bme)
                    }

                 fw.write(om.writeValueAsString(bmc))
                 fw.write("\n")
             }
         }
        println("Done")
    }
}
