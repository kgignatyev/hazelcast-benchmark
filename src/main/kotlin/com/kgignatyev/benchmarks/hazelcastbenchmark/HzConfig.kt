package com.kgignatyev.benchmarks.hazelcastbenchmark

import com.hazelcast.config.*
import com.kgignatyev.benchmarks.hazelcastbenchmark.vo.BMCompany
import com.kgignatyev.benchmarks.hazelcastbenchmark.hz.BMCompanySerializer
import com.kgignatyev.benchmarks.hazelcastbenchmark.hz.EmployeeNamesExtractorForCustomJsonNode
import com.kgignatyev.benchmarks.hazelcastbenchmark.hz.EmployeeNamesExtractorForObject
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class HzConfig {

    companion object {
        const val allInMemoryCacheName = "all-in-memory"
        const val deserializedOnDemandCacheName = "deserialized-on-demand"
        const val jsonNodeMapName = "default-json-node-map"
        const val customJsonNodeMapName = "custom-json-node-map"
        const val flexibleJsonNodeMapName = "flexible-json-node-map"
    }


    @Bean
    fun hazelcastConfig(): Config {
        val config = Config.loadFromClasspath(this::class.java.classLoader, "base-hazelcast.yaml")
        val serializationConfig = config.serializationConfig
        serializationConfig.addSerializerConfig(SerializerConfig().apply {
            className = BMCompanySerializer::class.java.name
            typeClass = BMCompany::class.java
            typeClassName = BMCompany::class.java.name
            implementation = BMCompanySerializer()
        } )


        val employeeNameAttrConfigForObjects = AttributeConfig("employeeNames", EmployeeNamesExtractorForObject::class.java.canonicalName)

        config.addMapConfig(MapConfig().apply {
            name = allInMemoryCacheName
            backupCount = 0
            inMemoryFormat = InMemoryFormat.OBJECT
            cacheDeserializedValues = CacheDeserializedValues.ALWAYS
            attributeConfigs.add(employeeNameAttrConfigForObjects)

        })
//        config.addMapConfig( MapConfig().apply {
//            name = deserializedOnDemandCacheName
//            backupCount = 0
//            inMemoryFormat = InMemoryFormat.BINARY
//            cacheDeserializedValues = CacheDeserializedValues.NEVER
//            attributeConfigs.apply {
//                add(employeeNameAttrConfigForObjects)
//            }
//        })

        //we can't use this because HazelcastJsonValue is rejected by extractors (internal Hazelcast bug? feature?)
//        val employeeNamesExtractorForDefaultJsonNode =  AttributeConfig("employeeNames",EmployeeNamesExtractorForHzJsonNode::class.java.canonicalName)
        config.addMapConfig(MapConfig().apply {
            name = jsonNodeMapName
            backupCount = 0
            attributeConfigs.apply {
//                add(employeeNamesExtractorForDefaultJsonNode)
            }
        })

        val employeeNameAttrConfigForCustomJsonNode = AttributeConfig("employeeNames", EmployeeNamesExtractorForCustomJsonNode::class.java.canonicalName)

        config.addMapConfig( MapConfig().apply {
            name = customJsonNodeMapName
            backupCount = 0
            inMemoryFormat = InMemoryFormat.OBJECT
            cacheDeserializedValues = CacheDeserializedValues.ALWAYS
            attributeConfigs.apply {
                add(employeeNameAttrConfigForCustomJsonNode)
            }
        })

        config.addMapConfig( MapConfig().apply {
            name = flexibleJsonNodeMapName
            backupCount = 0
            inMemoryFormat = InMemoryFormat.OBJECT
            cacheDeserializedValues = CacheDeserializedValues.ALWAYS
//            attributeConfigs.apply {
//                add(employeeNameAttrConfigForCustomJsonNode)
//            }
        })

        return config
    }


}
