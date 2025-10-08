package org.example.com.smartassistantdrive.trafficdt.dt

import it.wldt.core.engine.DigitalTwin
import org.example.com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.digitalAdapter.CustomHttpDigitalAdapter
import org.example.com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.digitalAdapter.configuration.EndPointConfiguration
import org.example.com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.physicalAdapter.MqttAggregatePhysicalAdapter
import org.example.com.smartassistantdrive.trafficdt.shadowing.AggregateTrafficShadowingFunction
import org.example.com.smartassistantdrive.trafficdt.utils.EnvironmentVariable

class AggregateTrafficDt(idDT: String) {

	private var digitalTwin: DigitalTwin

	init {
		val mqttPort = EnvironmentVariable(EnvironmentVariable.MQTT_PORT, "1883")
		val host = EnvironmentVariable(EnvironmentVariable.BASE_HOST, "127.0.0.1")
		val mqttHost = EnvironmentVariable(EnvironmentVariable.MQTT_BROKER_HOST, "127.0.0.1")

		val id = "$idDT-dt"
		val shadowing = AggregateTrafficShadowingFunction("$idDT-shadowing")
		digitalTwin = DigitalTwin(id, shadowing)

		val mqttPhysicalAdapter = MqttAggregatePhysicalAdapter(mqttHost.getEnvValue(), mqttPort.getEnvValue().toInt()).build("mqtt-pa-$idDT")

		val customHttpDigitalAdapter = CustomHttpDigitalAdapter("custom-http-da-$idDT", EndPointConfiguration(host.getEnvValue(), 8089), shadowing::getTrafficDigitalTwinByRoadId)

		digitalTwin.addPhysicalAdapter(mqttPhysicalAdapter)
		digitalTwin.addDigitalAdapter(customHttpDigitalAdapter)
	}

	fun getDigitalTwin(): DigitalTwin {
		return digitalTwin
	}

}