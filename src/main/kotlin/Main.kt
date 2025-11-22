package org.example

import YamlRoadReader
import it.wldt.core.engine.DigitalTwinEngine
import org.example.com.smartassistantdrive.trafficdt.dt.AggregateTrafficDt
import org.example.com.smartassistantdrive.trafficdt.dt.TrafficDT
import org.example.com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.webService.TrafficRouting

fun main() {
	println("Hello World!")
	// TrafficRouting.execute()
    val listRoads = YamlRoadReader().readYamlFile("src/main/resources/configuration.yml")
	val digitalTwinEngine = DigitalTwinEngine()
	digitalTwinEngine.addDigitalTwin(AggregateTrafficDt("aggregate-traffic").getDigitalTwin(), true)
    listRoads.forEachIndexed { index, road ->
        println("Creo DT per ${road.roadId}")
        digitalTwinEngine.addDigitalTwin(TrafficDT("trafficdt-$index", index, road.roadId, road.direction, road.numLanes, road.numBlocks).getDigitalTwin())
    }

	// Start all the DTs registered on the engine
	digitalTwinEngine.startAll()
}