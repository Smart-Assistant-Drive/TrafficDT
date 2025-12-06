package com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.physicalAdapter

import it.wldt.adapter.mqtt.physical.MqttPhysicalAdapter
import it.wldt.adapter.mqtt.physical.MqttPhysicalAdapterConfiguration
import com.smartassistantdrive.trafficdt.businessLayer.TrafficDtInfo
import com.smartassistantdrive.trafficdt.utils.UtilsFunctions

class MqttAggregatePhysicalAdapter(host: String, port: Int) {

	val builder = MqttPhysicalAdapterConfiguration.builder(host, port)

	companion object {
		val TRAFFIC_DT_ACTIVATED = "trafficDtActivated"
		val TRAFFIC_DT_SHUTDOWN = "trafficDtShutdown"
		val BASE_TOPIC_AGGREGATE = "aggregate-traffic"
		val CREATE_TRAFFIC_DT = "createTrafficDt"

        public fun getTrafficInfo(content: String): TrafficDtInfo {
            val json = UtilsFunctions.stringToJsonObjectGson(content)
            if(json != null) {
                return UtilsFunctions.getTrafficDTInfo(json)
            } else {
                throw IllegalArgumentException()
            }
        }
	}

	init {
		builder.addPhysicalAssetEventAndTopic(TRAFFIC_DT_ACTIVATED, "text/plain", "$BASE_TOPIC_AGGREGATE/$TRAFFIC_DT_ACTIVATED") {
			println("CONTENT: $it")
			getTrafficInfo(it)
		}
		builder.addPhysicalAssetEventAndTopic(TRAFFIC_DT_SHUTDOWN, "text/plain", "$BASE_TOPIC_AGGREGATE/$TRAFFIC_DT_SHUTDOWN") {
			getTrafficInfo(it)
		}
        builder.addPhysicalAssetActionAndTopic<String>(
            CREATE_TRAFFIC_DT,
            "text/plain",
            "create",
            "aggregatesemaphores/$CREATE_TRAFFIC_DT"
        ) {
            // payload: id as string
            it
        }
	}

	fun build(id: String): MqttPhysicalAdapter {
		return MqttPhysicalAdapter(id, builder.build())
	}

}