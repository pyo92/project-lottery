package com.example.projectlottery

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.containers.GenericContainer
import spock.lang.Specification

@ContextConfiguration //autowired
@SpringBootTest
abstract class IntegrationContainerBaseTest extends Specification {

    static final GenericContainer REDIS_CONTAINER

    static {
        REDIS_CONTAINER = new GenericContainer<>("redis:7")
            .withExposedPorts(6379)

        REDIS_CONTAINER.start()

        System.setProperty("spring.data.redis.host", REDIS_CONTAINER.getHost())
        System.setProperty("spring.data.redis.port", REDIS_CONTAINER.getMappedPort(6379).toString())
    }
}
