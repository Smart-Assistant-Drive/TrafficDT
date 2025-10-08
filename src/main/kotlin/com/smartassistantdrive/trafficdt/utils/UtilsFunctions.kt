package org.example.com.smartassistantdrive.trafficdt.utils

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import kotlin.math.pow
import kotlin.math.sqrt
import org.example.com.smartassistantdrive.trafficdt.businessLayer.TrafficDtInfo
import org.example.com.smartassistantdrive.trafficdt.domainLayer.CarState
import org.example.com.smartassistantdrive.trafficdt.domainLayer.CarUpdate

class UtilsFunctions {
	companion object {
		fun stringToJsonObjectGson(jsonString: String): JsonObject? {
			val gson = Gson()
			return try {
				gson.fromJson(jsonString, JsonObject::class.java)
			} catch (e: JsonParseException) {
				println("Errore durante la conversione della stringa in JsonObject: ${e.message}")
				null
			}
		}

		fun jsonToCarUpdateModel(json: JsonObject): CarUpdate {
			val idCar = json["idCar"].asString
			val speed = json["currentSpeed"].asFloat
			val state = json["state"].asString
			val lane = json["lane"].asInt
			val positionX = json["positionX"].asFloat
			val positionY = json["positionY"].asFloat
			val indexP = json["lane"].asInt
			val dPoint = json["dPoint"].asFloat
			return CarUpdate(
				idCar,
				speed,
				CarState.valueOf(state),
				lane,
				Pair(positionX, positionY),
				indexP,
				dPoint
			)
		}

		fun calculateDistance(position1: Pair<Float, Float>, position2: Pair<Float, Float>): Float {
			return sqrt(
				(
						position2.first - position1.first
					).toDouble()
					.pow(2.0)
					+
					(
						position2.second - position1.second
					).toDouble()
					.pow(2.0)
			).toFloat()
		}

		fun getCurrentTimestamp(): Long {
			return System.currentTimeMillis()
		}

		fun getTrafficDTInfo(json: JsonObject): TrafficDtInfo {
			return TrafficDtInfo(
				json["link"].asString,
				json["roadId"].asString,
				json["direction"].asInt,
				json["numLanes"].asInt
			)
		}
	}
}
