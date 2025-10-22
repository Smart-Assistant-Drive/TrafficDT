package org.example.com.smartassistantdrive.trafficdt.dt.property

import it.wldt.adapter.physical.PhysicalAssetProperty
import org.example.com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.physicalAdapter.MqttTrafficPhysicalAdapter

class TrafficInitialProperties(val roadId: String, val direction: Int): InternalProperties() {

	init {
		this.addProperty(PhysicalAssetProperty(MqttTrafficPhysicalAdapter.ROAD_ID, roadId))
		this.addProperty(PhysicalAssetProperty(MqttTrafficPhysicalAdapter.DIRECTION, direction))
	}

}