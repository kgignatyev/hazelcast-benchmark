package com.kgignatyev.benchmarks.hazelcastbenchmark

import com.hazelcast.nio.ObjectDataInput
import com.hazelcast.nio.ObjectDataOutput
import com.hazelcast.nio.serialization.StreamSerializer


class BMCompanySerializer : StreamSerializer<BMCompany> {

    override fun write(out: ObjectDataOutput, o: BMCompany) {
        out.writeString(BenchmarkConfig.objMapper.writeValueAsString(o))
    }

    override fun getTypeId(): Int {
        return 9000
    }

    override fun read(odi: ObjectDataInput): BMCompany {
        val data = odi.readString()
        return BenchmarkConfig.objMapper.readValue(data, BMCompany::class.java)
    }

}
