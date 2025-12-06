package com.smartassistantdrive.trafficdt.dt

import it.wldt.adapter.http.digital.adapter.HttpDigitalAdapter
import it.wldt.adapter.http.digital.adapter.HttpDigitalAdapterConfiguration
import it.wldt.core.engine.DigitalTwin
import it.wldt.core.engine.DigitalTwinEngine
import com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.digitalAdapter.CustomHttpDigitalAdapter
import com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.digitalAdapter.configuration.EndPointConfiguration
import com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.physicalAdapter.MqttAggregatePhysicalAdapter
import com.smartassistantdrive.trafficdt.shadowing.AggregateTrafficShadowingFunction
import com.smartassistantdrive.trafficdt.utils.EnvironmentVariable
import com.smartassistantdrive.trafficdt.utils.EnvironmentVariable.Companion.digitalAggregatePort
import com.smartassistantdrive.trafficdt.utils.EnvironmentVariable.Companion.httpAggregatePort

class AggregateTrafficDt(idDT: String, dtEngine: DigitalTwinEngine) {

	private var digitalTwin: DigitalTwin

	init {
		val mqttPort = EnvironmentVariable(EnvironmentVariable.MQTT_PORT, "1883")
		val host = EnvironmentVariable(EnvironmentVariable.BASE_HOST, "127.0.0.1")
		val mqttHost = EnvironmentVariable(EnvironmentVariable.MQTT_BROKER_HOST, "127.0.0.1")

		val id = "$idDT-dt"
		val shadowing = AggregateTrafficShadowingFunction("$idDT-shadowing", dtEngine)
		digitalTwin = DigitalTwin(id, shadowing)

		val mqttPhysicalAdapter = MqttAggregatePhysicalAdapter(mqttHost.getEnvValue(), mqttPort.getEnvValue().toInt()).build("mqtt-pa-$idDT")

        val httpDigitalAdapterConfiguration = HttpDigitalAdapterConfiguration("my-http-adapter-$idDT", host.getEnvValue(), httpAggregatePort.getEnvValue().toInt())
        val httpDigitalAdapter = HttpDigitalAdapter(httpDigitalAdapterConfiguration, digitalTwin)

        val customHttpDigitalAdapter = CustomHttpDigitalAdapter(
            "custom-http-da-$idDT",
            EndPointConfiguration(
                host.getEnvValue(),
                5102//digitalAggregatePort.getEnvValue().toInt()
            ),
            shadowing::getTrafficDigitalTwinByRoadId,
            shadowing::getAllRegisteredTrafficManagers
        )

		digitalTwin.addPhysicalAdapter(mqttPhysicalAdapter)
		digitalTwin.addDigitalAdapter(customHttpDigitalAdapter)
		digitalTwin.addDigitalAdapter(httpDigitalAdapter)
	}

	fun getDigitalTwin(): DigitalTwin {
		return digitalTwin
	}

}