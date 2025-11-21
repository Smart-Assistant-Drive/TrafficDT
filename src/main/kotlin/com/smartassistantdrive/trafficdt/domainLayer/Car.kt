package org.example.com.smartassistantdrive.trafficdt.domainLayer

import kotlinx.serialization.Serializable

@Serializable
data class Car(
	val id: String,
	val state: CarState,
	val waitingSemaphoreId: String,
	val speed: Float,
	val position: Pair<Float, Float>,
	val indexP: Int,
	val indexLane: Int,
	val dPoint: Pair<Float, Float>
)
