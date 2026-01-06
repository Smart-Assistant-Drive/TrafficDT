package com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.physicalAdapter

import it.wldt.adapter.mqtt.physical.MqttPhysicalAdapter
import it.wldt.adapter.mqtt.physical.MqttPhysicalAdapterConfiguration
import com.smartassistantdrive.trafficdt.businessLayer.ChangeLaneAction
import com.smartassistantdrive.trafficdt.businessLayer.ChangeLaneRequest
import com.smartassistantdrive.trafficdt.businessLayer.RestartCarAction
import com.smartassistantdrive.trafficdt.businessLayer.StopCarAction
import com.smartassistantdrive.trafficdt.dt.property.TrafficInitialProperties
import com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.conversion.toJson
import com.smartassistantdrive.trafficdt.utils.UtilsFunctions
import com.smartassistantdrive.trafficdt.utils.UtilsFunctions.Companion.stringToJsonObjectGson

class MqttTrafficPhysicalAdapter(host: String, port: Int, idDT: String, trafficInitialProperties: TrafficInitialProperties) {
	val builder = MqttPhysicalAdapterConfiguration.builder(host, port)

	val baseTopic = "$baseTopicPrefix-$idDT"

	companion object {
        val baseTopicPrefix = "trafficdt-physical"

		/* MACRO VALUES */
		val SECURITY_DISTANCE_VALUE: Double = 10.0

		/* PROPERTY */
		val POSITION_AX = "positionAX"
		val POSITION_AY = "positionAY"
		val POSITION_BX = "positionBX"
		val POSITION_BY = "positionBY"
		val ROAD_ID = "roadId"
		val SECURITY_DISTANCE = "securityDistance"
		val DIRECTION = "direction"

		/* EVENTS */
		val DIGITALTWIN_STARTED = "digitalTwinStarted"
		val DIGITALTWIN_SHUTDOWN = "digitalTwinShutdown"
		val CHANGE_LANE_REQUEST = "changeLaneRequest"
		val FIRST_CAR_RESTART = "firstCarRestart"
		val STOP_REQUEST_FOR_SEMAPHORE = "stopRequestForSemaphore"
		val CAR_ENTERED_ON_ROAD = "carEntered"
		val CAR_ENTERED_ON_ROAD_ACTION = "carEnteredAction"
		val CAR_EXITED_ON_ROAD = "carExited"
		val CAR_UPDATE = "carUpdate"

		/* ACTIONS */
		val CHANGE_LANE_ACTION = "changeLaneAction"
		val STOP_CAR_ACTION = "stopCarAction"
		val RESTART_CAR_ACTION = "restartCarAction"
	}

	init {
		/* PROPERTY */
		builder.addPhysicalAssetPropertyAndTopic(POSITION_AX, 0.0, "$baseTopic/$POSITION_AX") {
			it.toDouble()
		}
		builder.addPhysicalAssetPropertyAndTopic(SECURITY_DISTANCE, SECURITY_DISTANCE_VALUE, "$baseTopic/$SECURITY_DISTANCE") {
			it.toDouble()
		}
		builder.addPhysicalAssetPropertyAndTopic(POSITION_AY, 0.0, "$baseTopic/$POSITION_AY") {
			it.toDouble()
		}
		builder.addPhysicalAssetPropertyAndTopic(POSITION_BX, 0.0, "$baseTopic/$POSITION_BX") {
			it.toDouble()
		}
		builder.addPhysicalAssetPropertyAndTopic(POSITION_BY, 0.0, "$baseTopic/$POSITION_BY") {
			it.toDouble()
		}
		builder.addPhysicalAssetPropertyAndTopic(ROAD_ID, trafficInitialProperties.roadId, "$baseTopic/$ROAD_ID") { it }
		builder.addPhysicalAssetPropertyAndTopic(DIRECTION, trafficInitialProperties.direction, "$baseTopic/$DIRECTION") { it }

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
            println("CAR ENTERED EVENT")
            println(it)
            try {
                val json = stringToJsonObjectGson(it)
                UtilsFunctions.jsonToCarUpdateModel(json!!)
            } catch (e: Exception) {
                println(e.message)
            }
		}

		builder.addPhysicalAssetEventAndTopic(CAR_UPDATE, "text/plain", "$baseTopic/$CAR_UPDATE") {
			// info of car needed
			val json = stringToJsonObjectGson(it)
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

		builder.addPhysicalAssetEventAndTopic(DIGITALTWIN_STARTED, "text/plain", "$baseTopic/$DIGITALTWIN_STARTED") { it }

		builder.addPhysicalAssetEventAndTopic(DIGITALTWIN_SHUTDOWN, "text/plain", "$baseTopic/$DIGITALTWIN_SHUTDOWN") { it }

		/* ACTIONS */
		builder.addPhysicalAssetActionAndTopic<ChangeLaneAction>(CHANGE_LANE_ACTION, "car.changeLane", "text/plain", "$baseTopic/$CHANGE_LANE_ACTION") {
            it.toJson().toString()
		}

        builder.addPhysicalAssetActionAndTopic<ChangeLaneAction>(CAR_ENTERED_ON_ROAD_ACTION, "car.carEntered", "text/plain", "$baseTopic/$CAR_ENTERED_ON_ROAD_ACTION") {
            it.toJson().toString()
        }
	}

	fun build(id: String): MqttPhysicalAdapter {
		return MqttPhysicalAdapter(id, builder.build())
	}
}