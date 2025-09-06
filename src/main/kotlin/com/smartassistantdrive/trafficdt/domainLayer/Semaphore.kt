package org.example.com.smartassistantdrive.trafficdt.domainLayer

import kotlinx.serialization.Serializable

@Serializable
data class Semaphore(
	val id: String,
	val stoppingPoint: Pair<Coordinate, Coordinate>
)
