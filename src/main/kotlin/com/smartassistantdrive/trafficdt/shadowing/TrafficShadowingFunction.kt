package com.smartassistantdrive.trafficdt.shadowing

import it.wldt.adapter.digital.event.DigitalActionWldtEvent
import it.wldt.adapter.physical.PhysicalAssetDescription
import it.wldt.adapter.physical.PhysicalAssetEvent
import it.wldt.adapter.physical.PhysicalAssetProperty
import it.wldt.adapter.physical.event.PhysicalAssetEventWldtEvent
import it.wldt.adapter.physical.event.PhysicalAssetPropertyWldtEvent
import it.wldt.adapter.physical.event.PhysicalAssetRelationshipInstanceCreatedWldtEvent
import it.wldt.adapter.physical.event.PhysicalAssetRelationshipInstanceDeletedWldtEvent
import it.wldt.core.state.DigitalTwinStateEventNotification
import it.wldt.core.state.DigitalTwinStateProperty
import java.util.Optional
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import com.smartassistantdrive.trafficdt.businessLayer.ChangeLaneAction
import com.smartassistantdrive.trafficdt.businessLayer.ChangeLaneRequest
import com.smartassistantdrive.trafficdt.businessLayer.DistanceFromNext
import com.smartassistantdrive.trafficdt.businessLayer.TrafficDtInfo
import com.smartassistantdrive.trafficdt.domainLayer.Car
import com.smartassistantdrive.trafficdt.domainLayer.CarUpdate
import com.smartassistantdrive.trafficdt.domainLayer.CarVirtualPosition
import com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.digitalAdapter.CarsMqttDigitalAdapter
import com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.digitalAdapter.MqttTrafficDigitalAdapter.Companion.CHANGE_LANE_DIGITAL_ACTION
import com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.physicalAdapter.MqttTrafficPhysicalAdapter
import com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.physicalAdapter.MqttTrafficPhysicalAdapter.Companion.DIGITALTWIN_SHUTDOWN
import com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.physicalAdapter.MqttTrafficPhysicalAdapter.Companion.DIGITALTWIN_STARTED
import com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.physicalAdapter.MqttTrafficPhysicalAdapter.Companion.SECURITY_DISTANCE
import com.smartassistantdrive.trafficdt.utils.UtilsFunctions.Companion.calculateDistance
import com.smartassistantdrive.trafficdt.utils.UtilsFunctions.Companion.getCurrentTimestamp
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class TrafficShadowingFunction(id: String?, val trafficDtInfo: TrafficDtInfo) : AbstractShadowing(id) {

	private val LOGGER_NAME = "TrafficShadowingFunction"
	private val logger = LoggerFactory.getLogger(LOGGER_NAME)

	val lanes: ArrayList<ArrayList<ArrayList<Car>>> = ArrayList()
	val accessMap: HashMap<String, CarVirtualPosition> = HashMap<String, CarVirtualPosition>()

	var executorService: ScheduledExecutorService = Executors.newScheduledThreadPool(1)

	val cars: ArrayList<Car> = ArrayList() /** for testing only **/

	var task: Runnable = Runnable {
			updateCarsDistances()
		}

	override fun getLogger(): Logger = logger

	override fun onCreate() {

	}

	override fun onStart() {

	}

	override fun onStop() {
		this.digitalTwinStateManager.notifyDigitalTwinStateEvent(
			DigitalTwinStateEventNotification<TrafficDtInfo>(DIGITALTWIN_SHUTDOWN, trafficDtInfo, getCurrentTimestamp())
		)
	}

	override fun onDigitalTwinBound(adaptersPhysicalAssetDescriptionMap: MutableMap<String, PhysicalAssetDescription>?) {
		val pad = PhysicalAssetDescription()
		pad.properties.add(PhysicalAssetProperty("numLanes", trafficDtInfo.numLanes))
		pad.events.add(PhysicalAssetEvent(CarsMqttDigitalAdapter.DISTANCE_FROM_NEXT, "text/plain"))
		pad.events.add(PhysicalAssetEvent(CHANGE_LANE_DIGITAL_ACTION, "text/plain"))
		adaptersPhysicalAssetDescriptionMap!!["immutableProperties"] = pad
		super.onDigitalTwinBound(adaptersPhysicalAssetDescriptionMap)

		// initialize the lanes arrays
		for(c in 0..trafficDtInfo.numBlocks) {
			lanes.add(ArrayList())
			for(i in 0..<trafficDtInfo.numLanes) {
				lanes[c].add(ArrayList())
			}
		}
//		executorService.scheduleAtFixedRate(task, 0,  1, TimeUnit.SECONDS)

		// TODO remove this test code
		this.digitalTwinStateManager.notifyDigitalTwinStateEvent(
			DigitalTwinStateEventNotification<TrafficDtInfo>(DIGITALTWIN_STARTED, trafficDtInfo, getCurrentTimestamp())
		)
	}

	override fun onDigitalTwinUnBound(p0: MutableMap<String, PhysicalAssetDescription>?, p1: String?) {

	}

	override fun onPhysicalAdapterBidingUpdate(p0: String?, p1: PhysicalAssetDescription?) {

	}

	override fun onPhysicalAssetPropertyVariation(physicalAssetPropertyWldtEvent: PhysicalAssetPropertyWldtEvent<*>?) {
		if (physicalAssetPropertyWldtEvent != null) {
			logger.info("Property variation detected... " + physicalAssetPropertyWldtEvent.physicalPropertyId)
			try {
				this.digitalTwinStateManager.startStateTransaction()
				this.digitalTwinStateManager.updateProperty(
					DigitalTwinStateProperty(
						physicalAssetPropertyWldtEvent.physicalPropertyId,
						physicalAssetPropertyWldtEvent.body
					)
				)
				this.digitalTwinStateManager.commitStateTransaction()
			} catch (e: Exception) {
				e.printStackTrace()
			}
		}
	}

	override fun onPhysicalAssetEventNotification(physicalAssetEventWldtEvent: PhysicalAssetEventWldtEvent<*>?) {
		if (physicalAssetEventWldtEvent != null) {
			val eventKey = physicalAssetEventWldtEvent.physicalEventKey
			logger.info("Event notified... $eventKey")
			when(eventKey) {
				MqttTrafficPhysicalAdapter.CHANGE_LANE_REQUEST -> {
					val securityDistanceProperty = digitalTwinStateManager.digitalTwinState.getProperty(SECURITY_DISTANCE).get()
					val distanceValue = securityDistanceProperty.value as Double
					// manage change lane requests, calculate the distance between the cars that are behind and next to the current car.
					var canChange = true
					val request: ChangeLaneRequest = physicalAssetEventWldtEvent.body as ChangeLaneRequest
					val car = getCar(request.idCar)
					if(car.isPresent) {
						val position = car.get().position
						val carsList = lanes[car.get().indexP][request.destinationLane]
						carsList.forEach {
							val position1 = it.position
							val distance = calculateDistance(position1, position)
							if(distance < distanceValue) {
								canChange = false
							}
						}

						this.digitalTwinStateManager.notifyDigitalTwinStateEvent(
							DigitalTwinStateEventNotification(
								CHANGE_LANE_DIGITAL_ACTION,
								ChangeLaneAction(request.idCar, request.destinationLane, canChange),
								getCurrentTimestamp()
							)
						)
					} else {
						// silently ignoring
					}
				}

				MqttTrafficPhysicalAdapter.FIRST_CAR_RESTART -> {
					// manage first car restart (stopped on a semaphore)
				}

				MqttTrafficPhysicalAdapter.STOP_REQUEST_FOR_SEMAPHORE -> {
					// manage first car restart (stopped on a semaphore)
				}

				MqttTrafficPhysicalAdapter.CAR_UPDATE -> {
					// update the car state
					val carUpdate: CarUpdate = physicalAssetEventWldtEvent.body as CarUpdate
					if(this.accessMap.containsKey(carUpdate.idCar)) {
						// update the car
						updateCar(carUpdate)
					}
				}

				MqttTrafficPhysicalAdapter.CAR_ENTERED_ON_ROAD -> {
					// manage car added on road using lanes
					val carUpdate: CarUpdate = physicalAssetEventWldtEvent.body as CarUpdate

					if(!this.accessMap.containsKey(carUpdate.idCar)) {
						// update the car
						val indexPositionInLane = this.lanes[carUpdate.indexP][carUpdate.indexLane].size
						this.accessMap[carUpdate.idCar] = CarVirtualPosition(indexPositionInLane, carUpdate.indexLane, carUpdate.indexP)
						this.lanes[carUpdate.indexP][carUpdate.indexLane].add(
							Car(
								carUpdate.idCar,
								carUpdate.state,
								"",
								carUpdate.currentSpeed,
								carUpdate.position,
								carUpdate.indexP,
								carUpdate.indexLane,
								carUpdate.dPoint
							)
						)
					}
				}

				MqttTrafficPhysicalAdapter.CAR_EXITED_ON_ROAD -> {
					val carId: String = physicalAssetEventWldtEvent.body as String
					if(this.accessMap.containsKey(carId)) {
						val carVirtualPosition: CarVirtualPosition = this.accessMap[carId]!!
						this.lanes[carVirtualPosition.indexBlock][carVirtualPosition.indexLane].removeAt(carVirtualPosition.indexPosition)
					}
				}
			}
		}
	}

	override fun onPhysicalAssetRelationshipEstablished(p0: PhysicalAssetRelationshipInstanceCreatedWldtEvent<*>?) {

	}

	override fun onPhysicalAssetRelationshipDeleted(p0: PhysicalAssetRelationshipInstanceDeletedWldtEvent<*>?) {

	}

	override fun onDigitalActionEvent(p0: DigitalActionWldtEvent<*>?) {

	}

	fun updateCarsDistances() {
		this.lanes.forEach { blocks ->
			blocks.forEach {
				for (i in 0..<(it.size - 1)) {
					val car1 = it[i]
					val car2 = it[i + 1]
					val distance = calculateDistance(car1.position, car2.position)
					this.digitalTwinStateManager.notifyDigitalTwinStateEvent(
						DigitalTwinStateEventNotification(
							CarsMqttDigitalAdapter.DISTANCE_FROM_NEXT,
							DistanceFromNext(car1.id, car2.id, distance.toDouble(), car2.speed.toDouble()),
							getCurrentTimestamp()
						)
					)
				}
			}
		}
	}

	fun getCar(carId: String): Optional<Car> {
		val virtualPosition = this.accessMap[carId]
		if(virtualPosition != null)
			return Optional.of(this.lanes[virtualPosition.indexBlock][virtualPosition.indexLane][virtualPosition.indexPosition])
		else
			return Optional.empty()
	}

	fun updateCar(carUpdate: CarUpdate) {
		val car: Car = Car(
			carUpdate.idCar,
			carUpdate.state,
			"",
			carUpdate.currentSpeed,
			carUpdate.position,
			carUpdate.indexLane,
			carUpdate.indexP,
			carUpdate.dPoint
		)

		val carVirtualPosition = this.accessMap[carUpdate.idCar]
		if (carVirtualPosition != null) {
			this.lanes[carVirtualPosition.indexBlock][carVirtualPosition.indexLane].removeAt(carVirtualPosition.indexPosition)
		}

		this.lanes[carUpdate.indexP][carUpdate.indexLane].add(
			car
		)

		val tempArray: ArrayList<Car> = ArrayList()
		this.lanes[carUpdate.indexP][carUpdate.indexLane].sortedBy { calculateDistance(it.dPoint, it.position) }.toCollection(tempArray)

		this.lanes[carUpdate.indexP][carUpdate.indexLane] = tempArray

		val indexCar = this.lanes[carUpdate.indexP][carUpdate.indexLane].indexOf(car)

		this.accessMap[carUpdate.idCar] = CarVirtualPosition(indexCar, carUpdate.indexLane, carUpdate.indexP)
	}
}