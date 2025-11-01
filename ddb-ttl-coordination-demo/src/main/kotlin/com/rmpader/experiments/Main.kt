package com.rmpader.experiments

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import com.rmpader.eventsourcing.coordination.AggregateCoordinator.AggregateLocation
import com.rmpader.eventsourcing.coordination.ddb.DynamoDbAggregateCoordinator
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.slf4j.MDCContext
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

private val logger = LoggerFactory.getLogger("Main")

fun main() {
    val nodeId = System.getenv("POD_NAME") ?: "local-node-${UUID.randomUUID()}"
    MDC.put("node_id", nodeId)

    val aggregateId = UUID.randomUUID().toString()
    val runDurationMinutes = System.getenv("RUN_DURATION_MINUTES")?.toIntOrNull() ?: 10
    val checkIntervalMillis = System.getenv("CHECK_INTERVAL_MILLIS")?.toLongOrNull() ?: 1000L

    logger.info("========== [$nodeId] JOINING ==========")
    logger.info("Monitoring aggregate: $aggregateId")
    logger.info("Run duration: $runDurationMinutes minutes")
    logger.info("Check interval: $checkIntervalMillis milliseconds")

    runBlocking(MDCContext()) {
        val coordinator =
            DynamoDbAggregateCoordinator
                .builder(nodeId, DynamoDbClient.fromEnvironment())
                .build()

        try {
            coordinator.start()

            val runDuration = runDurationMinutes.minutes
            val checkInterval = checkIntervalMillis.milliseconds
            val startTime = System.currentTimeMillis()
            var previousLocation: AggregateLocation? = null
            var checkCount = 0

            while (System.currentTimeMillis() - startTime < runDuration.inWholeMilliseconds) {
                delay(checkInterval)
                checkCount++
                logger.info("locating aggregate $aggregateId")
                val currentLocation = coordinator.locateAggregate(aggregateId)

                // Only log if location changed
                if (currentLocation != previousLocation) {
                    logger.info(
                        "Aggregate location changed: $previousLocation -> $currentLocation " +
                                "(after $checkCount checks)",
                    )
                    previousLocation = currentLocation
                    checkCount = 0
                }
            }

            logger.info("Run duration elapsed. Total runtime: ${runDuration.inWholeMinutes} minutes")
        } catch (e: Exception) {
            logger.error("Error running coordinator: ${e.message}", e)
        } finally {
            logger.info("========== [$nodeId] LEAVING ==========")
            coordinator.stop()
            logger.info("Coordinator stopped")
        }
    }
}
