package org.example.com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.physicalAdapter

import it.wldt.adapter.mqtt.physical.MqttPhysicalAdapter
import it.wldt.adapter.mqtt.physical.MqttPhysicalAdapterConfiguration
import org.example.com.smartassistantdrive.trafficdt.businessLayer.ChangeLaneAction
import org.example.com.smartassistantdrive.trafficdt.businessLayer.ChangeLaneRequest
import org.example.com.smartassistantdrive.trafficdt.businessLayer.RestartCarAction
import org.example.com.smartassistantdrive.trafficdt.businessLayer.StopCarAction
import org.example.com.smartassistantdrive.trafficdt.utils.UtilsFunctions
import org.example.com.smartassistantdrive.trafficdt.utils.UtilsFunctions.Companion.stringToJsonObjectGson

class MqttTrafficPhysicalAdapter(host: String, port: Int, idDT: String) {

	val builder = MqttPhysicalAdapterConfiguration.builder(host, port)
	val baseTopic = "trafficdt-physical-$idDT"

	companion object {
		/* MACRO VALUES */
		val SECURITY_DISTANCE_VALUE: Double = 10.0

		/* PROPERTY */
		val POSITION_AX = "positionAX"
		val POSITION_AY = "positionAY"
		val POSITION_BX = "positionBX"
		val POSITION_BY = "positionBY"
		val ROAD_ID = "roadId"
		val SECURITY_DISTANCE = "securityDistance"

		/* EVENTS */
		val CHANGE_LANE_REQUEST = "changeLaneRequest"
		val FIRST_CAR_RESTART = "firstCarRestart"
		val STOP_REQUEST_FOR_SEMAPHORE = "stopRequestForSemaphore"
		val CAR_ENTERED_ON_ROAD = "carEntered"
		val CAR_EXITED_ON_ROAD = "carExited"
		val CAR_UPDATE = "carUpdate"

		/* ACTIONS */
		val CHANGE_LANE_ACTION = "changeLaneAction"
		val STOP_CAR_ACTION = "stopCarAction"
		val RESTART_CAR_ACTION = "restartCarAction"
	}

	init {
		/* PROPERTY */
		builder.addPhysicalAssetPropertyAndTopic(POSITION_AX, 0, "$baseTopic/$POSITION_AX") {
			it.toDouble()
		}
		builder.addPhysicalAssetPropertyAndTopic(SECURITY_DISTANCE, SECURITY_DISTANCE_VALUE, "$baseTopic/$SECURITY_DISTANCE") {
			it.toDouble()
		}
		builder.addPhysicalAssetPropertyAndTopic(POSITION_AY, 0, "$baseTopic/$POSITION_AY") {
			it.toDouble()
		}
		builder.addPhysicalAssetPropertyAndTopic(POSITION_BX, 0, "$baseTopic/$POSITION_BX") {
			it.toDouble()
		}
		builder.addPhysicalAssetPropertyAndTopic(POSITION_BY, 0, "$baseTopic/$POSITION_BY") {
			it.toDouble()
		}
		builder.addPhysicalAssetPropertyAndTopic(ROAD_ID, "", "$baseTopic/$ROAD_ID") { it }

		/* EVENTS */
		builder.addPhysicalAssetEventAndTopic(CHANGE_LANE_REQUEST, "text/plain", "$baseTopic/$CHANGE_LANE_REQUEST") {
			// Change lane request object
			val json = stringToJsonObjectGson(it)
			if(json != null) {
				val idCar: String = json["id"].asString
				val destinationLane: Int = json["destinationLane"].asInt
				ChangeLaneRequest(idCar, destinationLane)
			} else {
				throw IllegalArgumentException()
			}
		}

		builder.addPhysicalAssetEventAndTopic(FIRST_CAR_RESTART, "text/plain", "$baseTopic/$FIRST_CAR_RESTART") {
			// Id car needed
			it
		}

		builder.addPhysicalAssetEventAndTopic(STOP_REQUEST_FOR_SEMAPHORE, "text/plain", "$baseTopic/$STOP_REQUEST_FOR_SEMAPHORE") {
			// Id car needed
			it
		}

		builder.addPhysicalAssetEventAndTopic(CAR_ENTERED_ON_ROAD, "text/plain", "$baseTopic/$CAR_ENTERED_ON_ROAD") {
			// info of car needed
			val json = UtilsFunctions.stringToJsonObjectGson(it)
			if(json != null) {
				UtilsFunctions.jsonToCarUpdateModel(json)
			} else {
				throw IllegalArgumentException()
			}
		}

		builder.addPhysicalAssetEventAndTopic(CAR_UPDATE, "text/plain", "$baseTopic/$CAR_UPDATE") {
			// info of car needed
			val json = UtilsFunctions.stringToJsonObjectGson(it)
			if(json != null) {
				UtilsFunctions.jsonToCarUpdateModel(json)
			} else {
				throw IllegalArgumentException()
			}
		}

		builder.addPhysicalAssetEventAndTopic(CAR_EXITED_ON_ROAD, "text/plain", "$baseTopic/$CAR_EXITED_ON_ROAD") {
			// Id car needed
			it
		}

		/* ACTIONS */
		builder.addPhysicalAssetActionAndTopic<ChangeLaneAction>(CHANGE_LANE_ACTION, "car.changeLane", "text/plain", "$baseTopic/$CHANGE_LANE_ACTION") {
			it.toString()
		}

		builder.addPhysicalAssetActionAndTopic<StopCarAction>(STOP_CAR_ACTION, "car.stop", "text/plain", "$baseTopic/$STOP_CAR_ACTION") {
			it.toString()
		}

		builder.addPhysicalAssetActionAndTopic<RestartCarAction>(RESTART_CAR_ACTION, "car.restart", "text/plain", "$baseTopic/$RESTART_CAR_ACTION") {
			it.toString()
		}
	}

	fun build(id: String): MqttPhysicalAdapter {
		return MqttPhysicalAdapter(id, builder.build())
	}
}