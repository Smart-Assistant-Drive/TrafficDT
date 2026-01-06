package com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.digitalAdapter

import it.wldt.adapter.digital.DigitalAdapter
import it.wldt.core.state.DigitalTwinState
import it.wldt.core.state.DigitalTwinStateChange
import it.wldt.core.state.DigitalTwinStateEvent
import it.wldt.core.state.DigitalTwinStateEventNotification
import it.wldt.exception.EventBusException
import java.util.ArrayList
import java.util.stream.Collectors
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import com.smartassistantdrive.trafficdt.businessLayer.DistanceFromNext
import com.smartassistantdrive.trafficdt.domainLayer.CarUpdate
import com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.conversion.toJson
import com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.digitalAdapter.configuration.EndPointConfiguration
import com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.physicalAdapter.MqttTrafficPhysicalAdapter.Companion.CAR_ENTERED_ON_ROAD
import com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.physicalAdapter.MqttTrafficPhysicalAdapter.Companion.CAR_UPDATE

/**
 * Class that represents a MQTT Adapter for specific traffic dt -> car communication
 */
class CarsMqttDigitalAdapter(id: String, mqttConfiguration: EndPointConfiguration): DigitalAdapter<EndPointConfiguration>(id, mqttConfiguration) {

	val baseTopic = "trafficdt-digital-$id"

	companion object {
		val DISTANCE_FROM_NEXT = "distanceFromNext"
	}

	private fun publishUpdate(topic: String, value: String) {
		val qos = 2
		val broker: String = "tcp://${configuration.host}:${configuration.port}"
		println("BROKER: $broker")

		val persistence = MemoryPersistence()

		try {
			println("CREO IL CLIENT")
			val sampleClient = MqttClient(broker, "cars-mqtt-adapter", persistence)
			val connOpts = MqttConnectOptions()
			connOpts.isCleanSession = true
			println("Connecting to broker: $broker")
			sampleClient.connect(connOpts)
			println("Connected")
			println("Publishing message: $value")
			val message = MqttMessage(value.toByteArray())
			message.qos = qos
			sampleClient.publish(topic, message)
			println("Message published")
			sampleClient.disconnect()
			println("Disconnected")
		} catch (me: MqttException) {
			println("reason " + me.reasonCode)
			println("msg " + me.message)
			println("loc " + me.localizedMessage)
			println("cause " + me.cause)
			println("excep $me")
		}
	}

	override fun onStateUpdate(p0: DigitalTwinState?, p1: DigitalTwinState?, p2: ArrayList<DigitalTwinStateChange>?) {

	}

	override fun onEventNotificationReceived(eventNotification: DigitalTwinStateEventNotification<*>?) {
		println("DIGITAL ADAPTER: EVENTO RICEVUTO")
		if (eventNotification != null) {
			println("DIGITAL ADAPTER: EVENTO RICEVUTO: ${eventNotification.digitalEventKey}")
		}
		if(eventNotification != null) {
			val eventKey = eventNotification.digitalEventKey
			when(eventKey) {
				DISTANCE_FROM_NEXT -> {
					val body: DistanceFromNext = eventNotification.body as DistanceFromNext
					this.publishUpdate("$baseTopic/cars/${body.idCar}/$DISTANCE_FROM_NEXT", body.toJson().toString())
				}
                CAR_UPDATE -> {
                    try {
                        val body: CarUpdate = eventNotification.body as CarUpdate
                        this.publishUpdate("$baseTopic/cars/$CAR_UPDATE", body.toJson().toString())
                    } catch (me: MqttException) {
                        println("reason " + me.reasonCode)
                    }
                }
                CAR_ENTERED_ON_ROAD -> {
                    val body: CarUpdate = eventNotification.body as CarUpdate
                    this.publishUpdate("${configuration.physicalBaseTopic}/$CAR_ENTERED_ON_ROAD", body.toJson().toString())
                }
			}
		}
	}

	override fun onAdapterStart() {
		println("STARTED")
	}

	override fun onAdapterStop() {

	}

	override fun onDigitalTwinSync(currentDigitalTwinState: DigitalTwinState?) {
		println("[DemoDigitalAdapter] -> onDigitalTwinSync(): $currentDigitalTwinState")

		try {
			digitalTwinState!!.eventList
				.map { eventList: List<DigitalTwinStateEvent> ->
					eventList.stream()
						.map { obj: DigitalTwinStateEvent -> obj.key }
						.collect(Collectors.toList())
				}
				.ifPresent { eventKeys: List<String>? ->
					try {
						this.observeDigitalTwinEventsNotifications(eventKeys)
					} catch (e: EventBusException) {
						e.printStackTrace()
					}
				}
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}

	override fun onDigitalTwinUnSync(p0: DigitalTwinState?) {

	}

	override fun onDigitalTwinCreate() {

	}

	override fun onDigitalTwinStart() {

	}

	override fun onDigitalTwinStop() {

	}

	override fun onDigitalTwinDestroy() {

	}
}