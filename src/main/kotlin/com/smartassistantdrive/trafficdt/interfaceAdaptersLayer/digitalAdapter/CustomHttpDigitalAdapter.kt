package org.example.com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.digitalAdapter

import it.wldt.adapter.digital.DigitalAdapter
import it.wldt.core.state.DigitalTwinState
import it.wldt.core.state.DigitalTwinStateChange
import it.wldt.core.state.DigitalTwinStateEventNotification
import java.util.ArrayList
import org.example.com.smartassistantdrive.trafficdt.domainLayer.Car
import org.example.com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.digitalAdapter.configuration.EndPointConfiguration
import org.example.com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.webService.TrafficRouting

typealias CarFilter = (Int) -> Car

class CustomHttpDigitalAdapter(id: String, configuration: EndPointConfiguration, val filter: CarFilter): DigitalAdapter<EndPointConfiguration>(id, configuration)  {

	override fun onStateUpdate(p0: DigitalTwinState?, p1: DigitalTwinState?, p2: ArrayList<DigitalTwinStateChange>?) {
		
	}

	override fun onEventNotificationReceived(p0: DigitalTwinStateEventNotification<*>?) {
		
	}

	override fun onAdapterStart() {
		TrafficRouting.execute(this)
	}

	override fun onAdapterStop() {
		
	}

	override fun onDigitalTwinSync(p0: DigitalTwinState?) {
		
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