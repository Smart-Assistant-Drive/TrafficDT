package org.example.com.smartassistantdrive.trafficdt.domainLayer

data class CarUpdate(
	val idCar: String,
	val currentSpeed: Float,
	val state: CarState,
	val indexLane: Int,
	val position: Pair<Float, Float>,
	val indexP: Int,
	val dPoint: Pair<Float, Float>
)
