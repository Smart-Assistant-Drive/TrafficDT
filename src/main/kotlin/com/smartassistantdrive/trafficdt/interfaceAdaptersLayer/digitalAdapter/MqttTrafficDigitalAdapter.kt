package org.example.com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.digitalAdapter

import it.wldt.adapter.mqtt.digital.MqttDigitalAdapter
import it.wldt.adapter.mqtt.digital.MqttDigitalAdapterConfiguration
import it.wldt.adapter.mqtt.digital.topic.MqttQosLevel
import org.example.com.smartassistantdrive.trafficdt.businessLayer.ChangeLaneAction
import org.example.com.smartassistantdrive.trafficdt.businessLayer.DistanceFromNext
import org.example.com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.conversion.toJson

class MqttTrafficDigitalAdapter(host: String, port: Int, idDT: String) {

	val builder = MqttDigitalAdapterConfiguration.builder(host, port)
	val baseTopic = "trafficdt-digital-$idDT"

	companion object {
		/* EVENTS */
		val CHANGE_LANE_DIGITAL_ACTION = "changeLaneDigitalAction"
	}

	init {
		this.builder.addEventNotificationTopic<ChangeLaneAction>(CHANGE_LANE_DIGITAL_ACTION, "$baseTopic/digital/$CHANGE_LANE_DIGITAL_ACTION", MqttQosLevel.MQTT_QOS_0) {
			it.toJson().toString()
		}
	}

	fun build(id: String): MqttDigitalAdapter {
		return MqttDigitalAdapter(id, builder.build())
	}
}