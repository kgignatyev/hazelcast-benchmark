package com.kgignatyev.benchmarks.hazelcastbenchmark

import com.hazelcast.config.Config
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class HzConfig {

    @Bean
    fun hazelcastConfig(): Config {
        val config = Config.loadFromClasspath(this::class.java.classLoader, "base-hazelcast.yaml")
        return config
    }

}
