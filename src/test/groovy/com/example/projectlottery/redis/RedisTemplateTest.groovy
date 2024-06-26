package com.example.projectlottery.redis

import com.example.projectlottery.IntegrationContainerBaseTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate

class RedisTemplateTest extends IntegrationContainerBaseTest {

    @Autowired
    private RedisTemplate redisTemplate

    def "RedisTemplate - String operations"() {
        given:
        def valueOperations = redisTemplate.opsForValue()
        def key = "key"
        def value = "gp"

        when:
        valueOperations.set(key, value)

        then:
        def result = valueOperations.get(key)
        result == value
    }

    def "RedisTemplate - Set operations"() {
        given:
        def setOperations = redisTemplate.opsForSet()
        def key = "setKey"

        when:
        setOperations.add(key, "gp", "gp", "gp2", "gp3", "gp4")

        then:
        def size = setOperations.size(key)
        size == 4
    }

    def "RedisTemplate - Hash operations"() {
        given:
        def hashOperations = redisTemplate.opsForHash()
        def key = "hashKey"

        when:
        hashOperations.put(key, "subKey", "value")

        then:
        def result = hashOperations.get(key, "subKey")
        result == "value"

        def entries = hashOperations.entries(key)
        entries.keySet().contains("subKey")
        entries.values().contains("value")

        def size = hashOperations.size(key)
        size == entries.size()
    }
}
