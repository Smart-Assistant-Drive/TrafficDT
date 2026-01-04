package com.smartassistantdrive.trafficdt.shadowing

import it.wldt.adapter.digital.event.DigitalActionWldtEvent
import it.wldt.adapter.physical.PhysicalAssetDescription
import it.wldt.adapter.physical.event.PhysicalAssetEventWldtEvent
import it.wldt.adapter.physical.event.PhysicalAssetPropertyWldtEvent
import it.wldt.adapter.physical.event.PhysicalAssetRelationshipInstanceCreatedWldtEvent
import it.wldt.adapter.physical.event.PhysicalAssetRelationshipInstanceDeletedWldtEvent
import it.wldt.core.engine.DigitalTwinEngine
import kotlin.jvm.optionals.getOrNull
import com.smartassistantdrive.trafficdt.businessLayer.TrafficDtInfo
import com.smartassistantdrive.trafficdt.dt.TrafficDT
import com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.physicalAdapter.MqttAggregatePhysicalAdapter
import com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.physicalAdapter.MqttAggregatePhysicalAdapter.Companion.CREATE_TRAFFIC_DT
import com.smartassistantdrive.trafficdt.utils.EnvironmentVariable
import com.smartassistantdrive.trafficdt.utils.EnvironmentVariable.Companion.BASE_HOST
import com.smartassistantdrive.trafficdt.utils.EnvironmentVariable.Companion.httpPort
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class AggregateTrafficShadowingFunction(id: String?, private val dtEngine: DigitalTwinEngine) : AbstractShadowing(id) {

	private val LOGGER_NAME = "AggregateTrafficShadowingFunction"
	private val logger = LoggerFactory.getLogger(LOGGER_NAME)
	val trafficDigitalTwinsActive: ArrayList<TrafficDtInfo> = ArrayList()

	fun getTrafficDigitalTwinByRoadId(roadId: String, direction: Int): TrafficDtInfo? {
		logger.info("CURRENT DTs: " + trafficDigitalTwinsActive.toString())
		return trafficDigitalTwinsActive.stream().filter {
			it.roadId == roadId && it.direction == direction
		}.findFirst().getOrNull()
	}

    fun getAllRegisteredTrafficManagers(): List<TrafficDtInfo> {
        return trafficDigitalTwinsActive
    }

	override fun getLogger(): Logger = logger

	override fun onCreate() {
		
	}

	override fun onDigitalTwinBound(adaptersPhysicalAssetDescriptionMap: MutableMap<String, PhysicalAssetDescription>?) {
		super.onDigitalTwinBound(adaptersPhysicalAssetDescriptionMap)
	}

	override fun onStart() {
		
	}

	override fun onStop() {
		
	}

	override fun onDigitalTwinUnBound(p0: MutableMap<String, PhysicalAssetDescription>?, p1: String?) {
		
	}

	override fun onPhysicalAdapterBidingUpdate(p0: String?, p1: PhysicalAssetDescription?) {
		
	}

	override fun onPhysicalAssetPropertyVariation(p0: PhysicalAssetPropertyWldtEvent<*>?) {
		
	}

	override fun onPhysicalAssetEventNotification(physicalAssetEventWldtEvent: PhysicalAssetEventWldtEvent<*>?) {
		if (physicalAssetEventWldtEvent != null) {
			val eventKey = physicalAssetEventWldtEvent.physicalEventKey
			logger.info("Event notified... $eventKey")
			when (eventKey) {
				MqttAggregatePhysicalAdapter.TRAFFIC_DT_ACTIVATED -> {
					val dtInfo = physicalAssetEventWldtEvent.body as TrafficDtInfo
					this.trafficDigitalTwinsActive.add(dtInfo)
					logger.info("Activated new dt... ${trafficDigitalTwinsActive.toString()}")
				}
				MqttAggregatePhysicalAdapter.TRAFFIC_DT_SHUTDOWN -> {
					val dtInfo = physicalAssetEventWldtEvent.body as TrafficDtInfo
					this.trafficDigitalTwinsActive.removeIf {
						it.roadId == dtInfo.roadId && it.direction == dtInfo.direction
					}
				}
			}

		}
	}

	override fun onPhysicalAssetRelationshipEstablished(p0: PhysicalAssetRelationshipInstanceCreatedWldtEvent<*>?) {
		
	}

	override fun onPhysicalAssetRelationshipDeleted(p0: PhysicalAssetRelationshipInstanceDeletedWldtEvent<*>?) {
		
	}

	override fun onDigitalActionEvent(digitalActionWldtEvent: DigitalActionWldtEvent<*>?) {
        try {
            if (digitalActionWldtEvent != null) {
                logger.info("Actions triggered... " + digitalActionWldtEvent.actionKey)
                logger.info("Body... " + digitalActionWldtEvent.body)
                val body = digitalActionWldtEvent.body as String
                when (digitalActionWldtEvent.actionKey) {
                    CREATE_TRAFFIC_DT -> {
                        val trafficDtInfo = MqttAggregatePhysicalAdapter.getTrafficInfo(body)
                        val baseTrafficHost = EnvironmentVariable(BASE_HOST, "127.0.0.1")
                        val idIndex = trafficDigitalTwinsActive.size - 1
                        val httpDTPort = httpPort.getEnvValue().toInt() + idIndex
                        trafficDtInfo.link = "${baseTrafficHost.getEnvValue()}:$httpDTPort"
                        this.trafficDigitalTwinsActive.add(trafficDtInfo)
                        this.createNewSemaphore(trafficDtInfo, idIndex)
                        logger.info("Created new dt... $trafficDigitalTwinsActive")
                    }
                }
            } else {
                throw NullPointerException("Digital Action Wldt Event received is null")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
	}

    private fun createNewSemaphore(trafficDtInfo: TrafficDtInfo, idIndex: Int) {
        this.dtEngine.addDigitalTwin(TrafficDT("trafficdt-${trafficDtInfo.roadId}-${trafficDtInfo.direction}", idIndex, trafficDtInfo.roadId, trafficDtInfo.direction, trafficDtInfo.numLanes, trafficDtInfo.numBlocks).getDigitalTwin(), true)
    }

}