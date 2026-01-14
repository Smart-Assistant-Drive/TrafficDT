package com.smartassistantdrive.trafficdt.com.smartassistantdrive.trafficdt.domainLayer

import com.smartassistantdrive.trafficdt.businessLayer.DistanceFromNext
import com.smartassistantdrive.trafficdt.domainLayer.Car
import com.smartassistantdrive.trafficdt.domainLayer.CarUpdate
import com.smartassistantdrive.trafficdt.domainLayer.CarVirtualPosition
import com.smartassistantdrive.trafficdt.utils.UtilsFunctions.Companion.calculateDistance
import org.slf4j.LoggerFactory
import java.util.Optional
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import kotlin.math.pow
import kotlin.math.sqrt

class LanesMonitor(
    numBlocks: Int, numLanes: Int
) {
    private val logger = LoggerFactory.getLogger("LanesMonitor")

    val lanes: ArrayList<ArrayList<ArrayList<Car>>> = ArrayList()
    val accessMap: HashMap<String, CarVirtualPosition> = HashMap<String, CarVirtualPosition>()
    private val lock = ReentrantReadWriteLock()

    init {
        // initialize the lanes arrays
        for(c in 0..numBlocks) {
            lanes.add(ArrayList())
            for(i in 0..numLanes) {
                lanes[c].add(ArrayList())
            }
        }
    }

    /**
     * Ritorna la lista unificata delle auto per una specifica corsia in modo sicuro.
     */
    fun getDistances(): List<DistanceFromNext> {
        val listDistances = ArrayList<DistanceFromNext>()
        lock.read {
            try {
                val numeroCorsie = this.lanes[0].size //TODO da rendere generico per il numero di corsie

                for (c in 0 until numeroCorsie) {
                    // Uniamo tutte le auto della corsia 'i' presenti in tutti i blocchi
                    val corsiaUnificata = this.lanes.flatMap { blocco -> blocco[c] }
                    if(corsiaUnificata.isNotEmpty())
                        listDistances.add(DistanceFromNext(corsiaUnificata[corsiaUnificata.size - 1].id, "", -1.0, -1.0))
                    for (i in 0..<(corsiaUnificata.size - 1)) {
                        println(corsiaUnificata)
                        val car1 = corsiaUnificata[i]
                        val car2 = corsiaUnificata[i + 1]
                        println(car1.position.toString())
                        println(car2.position.toString())
                        val distance = calculateDistance(car1.position, car2.position)
                        listDistances.add(DistanceFromNext(car1.id, car2.id, distance.toDouble(), car2.speed.toDouble()))
                    }
                }
                return listDistances
            } catch (e: Exception) {
                logger.error("ERROR UPDATE DISTANCES: ${e.message}")
                return ArrayList()
            }
        }
    }

    fun getCar(carId: String): Optional<Car> {
        lock.read {
            val virtualPosition = this.accessMap[carId]
            if (virtualPosition != null)
                return Optional.of(this.lanes[virtualPosition.indexBlock][virtualPosition.indexLane][virtualPosition.indexPosition])
            else
                return Optional.empty()
        }
    }

    fun containsCar(carId: String): Boolean {
        lock.read {
            return this.accessMap.containsKey(carId)
        }
    }

    fun carExited(carId: String) {
        lock.write {
            if(this.accessMap.containsKey(carId)) {
                val carVirtualPosition: CarVirtualPosition = this.accessMap[carId]!!
                logger.info("CAR TO REMOVE: ${this.lanes[carVirtualPosition.indexBlock][carVirtualPosition.indexLane]}")
                this.lanes[carVirtualPosition.indexBlock][carVirtualPosition.indexLane].removeAt(carVirtualPosition.indexPosition)
                this.accessMap.remove(carId)
                logger.info("CAR REMOVED: ${this.lanes[carVirtualPosition.indexBlock][carVirtualPosition.indexLane]}")
            }
        }
    }

    /**
     * Aggiunge un'auto a una corsia specifica in un blocco specifico.
     */
    fun updateCar(carUpdate: CarUpdate) {
        println("AGGIORNO AUTO NEL MONITOR ${carUpdate.idCar}")
        println(this.lanes[carUpdate.indexP][carUpdate.indexLane])
        lock.write {
            val car: Car = Car(
                carUpdate.idCar,
                carUpdate.state,
                "",
                carUpdate.currentSpeed,
                carUpdate.position,
                carUpdate.indexP,
                carUpdate.indexLane,
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
            // calculateDistance(it.dPoint, it.position) // WRONG
            this.lanes[carUpdate.indexP][carUpdate.indexLane].sortedBy {
                sqrt(
                    it.dPoint.first.toDouble().pow(2.0).toFloat() + it.dPoint.second.toDouble().pow(2.0)
                )
            }.toCollection(tempArray)

            this.lanes[carUpdate.indexP][carUpdate.indexLane] = tempArray

            for(carToUpdate in this.lanes[carUpdate.indexP][carUpdate.indexLane]) {
                val indexCar = this.lanes[carUpdate.indexP][carUpdate.indexLane].indexOf(carToUpdate)
                this.accessMap[carToUpdate.id] = CarVirtualPosition(indexCar, carToUpdate.indexLane, carToUpdate.indexP)
            }

            println("FINE UPDATE")
            println(this.lanes[carUpdate.indexP][carUpdate.indexLane])
        }
    }
}