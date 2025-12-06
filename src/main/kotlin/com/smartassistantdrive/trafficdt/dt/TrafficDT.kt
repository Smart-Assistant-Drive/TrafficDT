package com.smartassistantdrive.trafficdt.dt

import it.wldt.adapter.http.digital.adapter.HttpDigitalAdapter
import it.wldt.adapter.http.digital.adapter.HttpDigitalAdapterConfiguration
import it.wldt.core.engine.DigitalTwin
import com.smartassistantdrive.trafficdt.businessLayer.TrafficDtInfo
import com.smartassistantdrive.trafficdt.dt.property.TrafficInitialProperties
import com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.digitalAdapter.CarsMqttDigitalAdapter
import com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.digitalAdapter.MqttTrafficDigitalAdapter
import com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.digitalAdapter.configuration.EndPointConfiguration
import com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.physicalAdapter.MqttTrafficPhysicalAdapter
import com.smartassistantdrive.trafficdt.shadowing.TrafficShadowingFunction
import com.smartassistantdrive.trafficdt.utils.EnvironmentVariable
import com.smartassistantdrive.trafficdt.utils.EnvironmentVariable.Companion.BASE_HOST
import com.smartassistantdrive.trafficdt.utils.EnvironmentVariable.Companion.MQTT_BROKER_HOST
import com.smartassistantdrive.trafficdt.utils.EnvironmentVariable.Companion.MQTT_PORT
import com.smartassistantdrive.trafficdt.utils.EnvironmentVariable.Companion.httpPort

class TrafficDT(idDT: String, idIndex: Int, roadId: String, direction: Int, numLanes: Int, numBlocks: Int) {
	private var digitalTwin: DigitalTwin

	init {
		val mqttPort = EnvironmentVariable(MQTT_PORT, "1883")
		val host = EnvironmentVariable(BASE_HOST, "127.0.0.1")
		val mqttHost = EnvironmentVariable(MQTT_BROKER_HOST, "127.0.0.1")
		val httpDTPort = httpPort.getEnvValue().toInt() + idIndex
		val id = "semaphore-dt-$idDT"
		val shadowing = TrafficShadowingFunction("traffic-shadowing-$idDT", TrafficDtInfo("${host.getEnvValue()}:$httpDTPort", roadId, direction, numLanes, numBlocks))
		digitalTwin = DigitalTwin(id, shadowing)

		val mqttPhysicalAdapter = MqttTrafficPhysicalAdapter(mqttHost.getEnvValue(), mqttPort.getEnvValue().toInt(), idDT, TrafficInitialProperties(roadId, direction)).build("test-mqtt-pa-$idDT")

		val httpDigitalAdapterConfiguration = HttpDigitalAdapterConfiguration("my-http-adapter-$idDT", host.getEnvValue(), httpDTPort)
		val httpDigitalAdapter = HttpDigitalAdapter(httpDigitalAdapterConfiguration, digitalTwin)

		val carsDigitalAdapter = CarsMqttDigitalAdapter("cars-digital-adapter", EndPointConfiguration(mqttHost.getEnvValue(), mqttPort.getEnvValue().toInt()))

		val mqttDigitalAdapter = MqttTrafficDigitalAdapter(mqttHost.getEnvValue(), mqttPort.getEnvValue().toInt(), idDT).build("test-mqtt-da-$idDT")

		digitalTwin.addPhysicalAdapter(mqttPhysicalAdapter)
		digitalTwin.addDigitalAdapter(httpDigitalAdapter)
		digitalTwin.addDigitalAdapter(carsDigitalAdapter)
		digitalTwin.addDigitalAdapter(mqttDigitalAdapter)
	}

	fun getDigitalTwin(): DigitalTwin {
		return digitalTwin
	}
}