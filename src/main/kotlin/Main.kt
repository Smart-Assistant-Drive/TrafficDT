package com.smartassistantdrive.trafficdt

import YamlRoadReader
import com.smartassistantdrive.trafficdt.dt.AggregateTrafficDt
import com.smartassistantdrive.trafficdt.dt.TrafficDT
import it.wldt.core.engine.DigitalTwinEngine

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            println("Hello World!")
            // TrafficRouting.execute()
            val listRoads = YamlRoadReader().readYamlFile("src/main/resources/configuration.yml")
            val digitalTwinEngine = DigitalTwinEngine()
            digitalTwinEngine.addDigitalTwin(AggregateTrafficDt("aggregate-traffic", digitalTwinEngine).getDigitalTwin(), true)
            listRoads.forEachIndexed { index, road ->
                println("Creo DT per ${road.roadId}")
                digitalTwinEngine.addDigitalTwin(
                    TrafficDT(
                        "trafficdt-$index",
                        index,
                        road.roadId,
                        road.direction,
                        road.numLanes,
                        road.numBlocks
                    ).getDigitalTwin(), true)
            }

            // Start all the DTs registered on the engine
            //digitalTwinEngine.startAll()
        }
    }
}
