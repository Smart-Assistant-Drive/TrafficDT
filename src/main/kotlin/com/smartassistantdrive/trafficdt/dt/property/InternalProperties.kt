package org.example.com.smartassistantdrive.trafficdt.dt.property

import it.wldt.adapter.physical.PhysicalAssetProperty

open class InternalProperties {
	val properties: ArrayList<PhysicalAssetProperty<*>> = ArrayList()

	fun addProperty(value: PhysicalAssetProperty<*>) {
		properties.add(value)
	}
}