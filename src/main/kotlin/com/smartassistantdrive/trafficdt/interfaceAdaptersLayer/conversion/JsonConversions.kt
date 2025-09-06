package org.example.com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.conversion

import com.google.gson.JsonObject
import org.example.com.smartassistantdrive.trafficdt.businessLayer.ChangeLaneAction
import org.example.com.smartassistantdrive.trafficdt.businessLayer.DistanceFromNext

fun ChangeLaneAction.toJson() {
	val json = JsonObject()
	json.addProperty("idCar", this.idCar)
	json.addProperty("lane", this.lane)
	json.addProperty("canChange", this.canChange)
}

fun DistanceFromNext.toJson() {
	val json = JsonObject()
	json.addProperty("idCar", this.idCar)
	json.addProperty("idNextCar", this.idNextCar)
	json.addProperty("distance", this.distance)
}