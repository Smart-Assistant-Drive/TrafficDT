package org.example.com.smartassistantdrive.trafficdt.dt

import it.wldt.adapter.http.digital.adapter.HttpDigitalAdapter
import it.wldt.adapter.http.digital.adapter.HttpDigitalAdapterConfiguration
import it.wldt.core.engine.DigitalTwin
import org.example.com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.digitalAdapter.CarsMqttDigitalAdapter
import org.example.com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.digitalAdapter.CustomHttpDigitalAdapter
import org.example.com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.digitalAdapter.configuration.EndPointConfiguration
import org.example.com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.physicalAdapter.MqttTrafficPhysicalAdapter
import org.example.com.smartassistantdrive.trafficdt.shadowing.TrafficShadowingFunction
import org.example.com.smartassistantdrive.trafficdt.utils.EnvironmentVariable
import org.example.com.smartassistantdrive.trafficdt.utils.EnvironmentVariable.Companion.BASE_HOST
import org.example.com.smartassistantdrive.trafficdt.utils.EnvironmentVariable.Companion.MQTT_BROKER_HOST
import org.example.com.smartassistantdrive.trafficdt.utils.EnvironmentVariable.Companion.MQTT_PORT
import org.example.com.smartassistantdrive.trafficdt.utils.EnvironmentVariable.Companion.httpPort

class TrafficDT(idDT: String, idIndex: Int) {

	private var digitalTwin: DigitalTwin

	init {
		val mqttPort = EnvironmentVariable(MQTT_PORT, "1883")
		val host = EnvironmentVariable(BASE_HOST, "127.0.0.1")
		val mqttHost = EnvironmentVariable(MQTT_BROKER_HOST, "127.0.0.1")

		val id = "test-semaphore-dt-$idDT"
		val shadowing = TrafficShadowingFunction("traffic-shadowing-$idDT", 2)
		digitalTwin = DigitalTwin(id, shadowing)

		val mqttPhysicalAdapter = MqttTrafficPhysicalAdapter(mqttHost.getEnvValue(), mqttPort.getEnvValue().toInt(), idDT).build("test-mqtt-pa-$idDT")

		val httpDigitalAdapterConfiguration = HttpDigitalAdapterConfiguration("my-http-adapter-$idDT", host.getEnvValue(), (httpPort.getEnvValue().toInt() + idIndex))
		val httpDigitalAdapter = HttpDigitalAdapter(httpDigitalAdapterConfiguration, digitalTwin)

		val carsDigitalAdapter = CarsMqttDigitalAdapter("cars-digital-adapter", EndPointConfiguration(mqttHost.getEnvValue(), mqttPort.getEnvValue().toInt()))

		val customHttpDigitalAdapter = CustomHttpDigitalAdapter("custom-http-da-$idDT", EndPointConfiguration("127.0.0.1", 8089), shadowing::filterCar)

		digitalTwin.addPhysicalAdapter(mqttPhysicalAdapter)
		digitalTwin.addDigitalAdapter(httpDigitalAdapter)
		digitalTwin.addDigitalAdapter(carsDigitalAdapter)
		digitalTwin.addDigitalAdapter(customHttpDigitalAdapter)
	}

	fun getDigitalTwin(): DigitalTwin {
		return digitalTwin
	}
}