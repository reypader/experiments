package com.rmpader.experiments

import com.rmpader.eventsourcing.coordination.kubernetes.KubernetesAggregateCoordinator
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.slf4j.MDCContext
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import java.util.*

private val logger = LoggerFactory.getLogger("Main")

fun main() {
    val nodeId = System.getenv("POD_NAME")
    MDC.put("node_id", nodeId)
    val aggregateId = UUID.randomUUID().toString()
    logger.info("==========[$nodeId] JOINING==========")
    runBlocking(MDCContext()) {
        val coordinator =
            KubernetesAggregateCoordinator
                .builder()
                .labelSelector("app=kubernetes-informer-demo")
                .nodeId(nodeId)
                .namespace("default")
                .build()

        try {
            coordinator.start()

            repeat(100) { iteration ->
                delay(1000)

                logger.info("=== Iteration ${iteration + 1} ===")

                val location = coordinator.locateAggregate(aggregateId)
                logger.info("Aggregate '$aggregateId' location: $location")
            }
        } catch (e: Exception) {
            logger.error("Error running coordinator: ${e.message}", e)
        } finally {
            logger.info("==========[$nodeId] LEAVING==========")

            logger.info("Stopping coordinator...")
            coordinator.stop()
            logger.info("Coordinator stopped")
        }
    }
}
