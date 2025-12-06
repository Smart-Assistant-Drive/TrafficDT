package com.smartassistantdrive.trafficdt.utils

class EnvironmentVariable(key: String, private val defaultValue: String) {

	companion object {
		const val HTTP_PORT = "HTTP_PORT"
		const val MQTT_PORT = "MQTT_PORT"

		const val BASE_PORT_AGGREGATE = "BASE_PORT_AGGREGATE"
		const val AGGREGATE_DIGITAL_ADAPTER = "SEMAPHORE_DIGITAL_ADAPTER"
		const val MQTT_BROKER_HOST = "MQTT_BROKER_HOST"

		const val BASE_HOST = "BASE_HOST"

		val httpPort = EnvironmentVariable(HTTP_PORT, "8082")
		val httpAggregatePort = EnvironmentVariable(BASE_PORT_AGGREGATE, "8079")
		val digitalAggregatePort = EnvironmentVariable(AGGREGATE_DIGITAL_ADAPTER, "8089")
	}

	private val value: String? = System.getenv(key)

	fun getEnvValue(): String {
		if (value == null) {
			return defaultValue
		}
		return value
	}
}