package ru.leonsabr.rest.disk.utils

import java.time.Duration
import java.time.LocalDateTime

fun waitForCondition(
        timeout: Duration = Duration.ofSeconds(10),
        pollInterval: Duration = Duration.ofMillis(500),
        condition: () -> Boolean
): Boolean {
    val timeLimit = LocalDateTime.now() + timeout

    do {
        if (condition()) return true
        Thread.sleep(pollInterval.toMillis())
    } while (LocalDateTime.now() < timeLimit)

    return false
}
