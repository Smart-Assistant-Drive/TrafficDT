package org.example.com.smartassistantdrive.trafficdt.businessLayer

import org.example.com.smartassistantdrive.trafficdt.domainLayer.Coordinate

data class StopCarAction(
	val idCar: String,
	val position: Pair<Coordinate, Coordinate>,
	val isFirstOfQueue: Boolean
)
