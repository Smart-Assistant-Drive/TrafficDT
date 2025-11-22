package org.example.com.smartassistantdrive.trafficdt.useCase

import it.wldt.core.state.DigitalTwinStateEventNotification
import java.util.Optional
import org.example.com.smartassistantdrive.trafficdt.businessLayer.ChangeLaneAction
import org.example.com.smartassistantdrive.trafficdt.businessLayer.ChangeLaneRequest
import org.example.com.smartassistantdrive.trafficdt.businessLayer.DistanceFromNext
import org.example.com.smartassistantdrive.trafficdt.businessLayer.TrafficDtInfo
import org.example.com.smartassistantdrive.trafficdt.domainLayer.Car
import org.example.com.smartassistantdrive.trafficdt.domainLayer.CarUpdate
import org.example.com.smartassistantdrive.trafficdt.domainLayer.CarVirtualPosition
import org.example.com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.digitalAdapter.CarsMqttDigitalAdapter
import org.example.com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.digitalAdapter.MqttTrafficDigitalAdapter
import org.example.com.smartassistantdrive.trafficdt.utils.UtilsFunctions

/*TODO refactor shadowing using this class*/
class TrafficManagerUseCase(val trafficDtInfo: TrafficDtInfo) {
	val lanes: ArrayList<ArrayList<ArrayList<Car>>> = ArrayList()
	val accessMap: HashMap<String, CarVirtualPosition> = HashMap<String, CarVirtualPosition>()

	init {
		// initialize the lanes arrays
		for(c in 0..trafficDtInfo.numBlocks) {
			lanes.add(ArrayList())
			for(i in 0..<trafficDtInfo.numLanes) {
				lanes[c].add(ArrayList())
			}
		}
	}

	fun changeLane(car: Car, distanceValue: Double, destinationLane: Int): Boolean {
		// manage change lane requests, calculate the distance between the cars that are behind and next to the current car.
		var canChange = true
		val position = car.position
		val carsList = lanes[car.indexP][destinationLane]
		carsList.forEach {
			val position1 = it.position
			val distance = UtilsFunctions.calculateDistance(position1, position)
			if(distance < distanceValue) {
				canChange = false
			}
		}
		return canChange
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
		this.lanes[carUpdate.indexP][carUpdate.indexLane].sortedBy { UtilsFunctions.calculateDistance(it.dPoint, it.position) }.toCollection(tempArray)

		this.lanes[carUpdate.indexP][carUpdate.indexLane] = tempArray

		val indexCar = this.lanes[carUpdate.indexP][carUpdate.indexLane].indexOf(car)

		this.accessMap[carUpdate.idCar] = CarVirtualPosition(indexCar, carUpdate.indexLane, carUpdate.indexP)
	}
}