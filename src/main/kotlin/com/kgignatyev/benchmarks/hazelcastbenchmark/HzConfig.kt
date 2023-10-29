package com.kgignatyev.benchmarks.hazelcastbenchmark

import com.hazelcast.config.CacheDeserializedValues
import com.hazelcast.config.Config
import com.hazelcast.config.InMemoryFormat
import com.hazelcast.config.MapConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class HzConfig {

    companion object {
        const val allInMemoryCacheName = "all-in-memory"
        const val deserializedOnDemandCacheName = "deserialized-on-demand"
        const val jsonNodeMapName = "json-node-map"
        const val customJsonNodeMapName = "custom-json-node-map"
    }


    @Bean
    fun hazelcastConfig(): Config {
        val config = Config.loadFromClasspath(this::class.java.classLoader, "base-hazelcast.yaml")
        config.addMapConfig(MapConfig().apply {
            name = deserializedOnDemandCacheName
            backupCount = 0
            inMemoryFormat = InMemoryFormat.OBJECT
            cacheDeserializedValues = CacheDeserializedValues.ALWAYS
        })
        config.addMapConfig( MapConfig().apply {
            name = deserializedOnDemandCacheName
            backupCount = 0
            inMemoryFormat = InMemoryFormat.BINARY
            cacheDeserializedValues = CacheDeserializedValues.NEVER
        })

        config.addMapConfig(MapConfig().apply {
            name = jsonNodeMapName
            backupCount = 0
        })

        config.addMapConfig( MapConfig().apply {
            name = customJsonNodeMapName
            backupCount = 0
            inMemoryFormat = InMemoryFormat.OBJECT
            cacheDeserializedValues = CacheDeserializedValues.ALWAYS
        })

        return config
    }


}
