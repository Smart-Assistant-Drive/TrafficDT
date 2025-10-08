package org.example.com.smartassistantdrive.trafficdt.dt

import it.wldt.adapter.http.digital.adapter.HttpDigitalAdapter
import it.wldt.adapter.http.digital.adapter.HttpDigitalAdapterConfiguration
import it.wldt.core.engine.DigitalTwin
import org.example.com.smartassistantdrive.trafficdt.businessLayer.TrafficDtInfo
import org.example.com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.digitalAdapter.CarsMqttDigitalAdapter
import org.example.com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.digitalAdapter.CustomHttpDigitalAdapter
import org.example.com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.digitalAdapter.MqttTrafficDigitalAdapter
import org.example.com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.digitalAdapter.configuration.EndPointConfiguration
import org.example.com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.physicalAdapter.MqttTrafficPhysicalAdapter
import org.example.com.smartassistantdrive.trafficdt.shadowing.TrafficShadowingFunction
import org.example.com.smartassistantdrive.trafficdt.utils.EnvironmentVariable
import org.example.com.smartassistantdrive.trafficdt.utils.EnvironmentVariable.Companion.BASE_HOST
import org.example.com.smartassistantdrive.trafficdt.utils.EnvironmentVariable.Companion.MQTT_BROKER_HOST
import org.example.com.smartassistantdrive.trafficdt.utils.EnvironmentVariable.Companion.MQTT_PORT
import org.example.com.smartassistantdrive.trafficdt.utils.EnvironmentVariable.Companion.httpPort

class TrafficDT(idDT: String, idIndex: Int, roadId: String, direction: Int, numLanes: Int) {
// TODO set roadId and direction
	private var digitalTwin: DigitalTwin

	init {
		val mqttPort = EnvironmentVariable(MQTT_PORT, "1883")
		val host = EnvironmentVariable(BASE_HOST, "127.0.0.1")
		val mqttHost = EnvironmentVariable(MQTT_BROKER_HOST, "127.0.0.1")
		val httpDTPort = httpPort.getEnvValue().toInt() + idIndex
		val id = "semaphore-dt-$idDT"
		val shadowing = TrafficShadowingFunction("traffic-shadowing-$idDT", TrafficDtInfo("${host.getEnvValue()}:$httpDTPort", roadId, direction, numLanes))
		digitalTwin = DigitalTwin(id, shadowing)

		val mqttPhysicalAdapter = MqttTrafficPhysicalAdapter(mqttHost.getEnvValue(), mqttPort.getEnvValue().toInt(), idDT).build("test-mqtt-pa-$idDT")

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