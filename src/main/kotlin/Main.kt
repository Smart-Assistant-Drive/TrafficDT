package org.example

import it.wldt.core.engine.DigitalTwinEngine
import org.example.com.smartassistantdrive.trafficdt.dt.TrafficDT
import org.example.com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.webService.TrafficRouting

fun main() {
	println("Hello World!")
	// TrafficRouting.execute()

	val digitalTwinEngine = DigitalTwinEngine()
	digitalTwinEngine.addDigitalTwin(TrafficDT("trafficdt", 0).getDigitalTwin())
	// Start all the DTs registered on the engine
	digitalTwinEngine.startAll()
}