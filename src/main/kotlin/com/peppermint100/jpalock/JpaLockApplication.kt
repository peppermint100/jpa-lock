package com.peppermint100.jpalock

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class JpaLockApplication

fun main(args: Array<String>) {
    runApplication<JpaLockApplication>(*args)
}
