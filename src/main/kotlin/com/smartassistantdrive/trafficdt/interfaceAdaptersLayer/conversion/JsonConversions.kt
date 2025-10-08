package org.example.com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.conversion

import com.google.gson.JsonObject
import org.example.com.smartassistantdrive.trafficdt.businessLayer.ChangeLaneAction
import org.example.com.smartassistantdrive.trafficdt.businessLayer.DistanceFromNext
import org.example.com.smartassistantdrive.trafficdt.businessLayer.TrafficDtInfo

fun ChangeLaneAction.toJson(): JsonObject {
	val json = JsonObject()
	json.addProperty("idCar", this.idCar)
	json.addProperty("lane", this.lane)
	json.addProperty("canChange", this.canChange)
	return json
}

fun DistanceFromNext.toJson(): JsonObject {
	val json = JsonObject()
	json.addProperty("idCar", this.idCar)
	json.addProperty("idNextCar", this.idNextCar)
	json.addProperty("distance", this.distance)
	json.addProperty("speed", this.velocityOfNext)
	return json
}

fun TrafficDtInfo.toJson(): JsonObject {
	val json = JsonObject()
	json.addProperty("link", this.link)
	json.addProperty("roadId", this.roadId)
	json.addProperty("direction", this.direction)
	json.addProperty("numLanes", this.numLanes)
	return json
}