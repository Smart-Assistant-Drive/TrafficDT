package com.smartassistantdrive.trafficdt.businessLayer

data class ChangeLaneAction(
	val idCar: String,
	val lane: Int,
	val canChange: Boolean
)
