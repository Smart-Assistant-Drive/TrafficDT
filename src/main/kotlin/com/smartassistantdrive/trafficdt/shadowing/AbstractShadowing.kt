package org.example.com.smartassistantdrive.trafficdt.shadowing

import it.wldt.adapter.physical.PhysicalAssetDescription
import it.wldt.core.model.ShadowingFunction
import it.wldt.core.state.DigitalTwinStateAction
import it.wldt.core.state.DigitalTwinStateEvent
import it.wldt.core.state.DigitalTwinStateProperty
import org.slf4j.Logger

abstract class AbstractShadowing(id: String?) : ShadowingFunction(id) {

	override fun onDigitalTwinBound(adaptersPhysicalAssetDescriptionMap: MutableMap<String, PhysicalAssetDescription>?) {
		try{
			// Start DT State Change Transaction
			this.digitalTwinStateManager.startStateTransaction()
			// Iterate over all the received PAD from connected Physical Adapters
			if (adaptersPhysicalAssetDescriptionMap != null) {
				adaptersPhysicalAssetDescriptionMap.values.forEach { pad ->
					pad.properties.forEach {
						getLogger().info("Creo la proprietà: " + it.key + " - " + it.initialValue)
						// Create and write the property on the DT's State
						this.digitalTwinStateManager.createProperty(DigitalTwinStateProperty(it.key, it.initialValue))
						//Start observing the variation of the physical property in order to receive notifications
						//Without this call the Shadowing Function will not receive any notifications or callback about
						//incoming physical property of the target type and with the target key
						this.observePhysicalAssetProperty(it)
					}
					pad.events.forEach { event ->
						// create the action related to the action defined
						getLogger().info("Creo l'evento: " + event.key + " - " + event.type)
						val dtStateEvent = DigitalTwinStateEvent(event.key, event.type)
						this.digitalTwinStateManager.registerEvent(dtStateEvent)
						this.observePhysicalAssetEvent(event)
					}
					pad.actions.forEach { action ->
						getLogger().info("Creo l'azione: " + action.key + " - " + action.type)
						val dtStateAction = DigitalTwinStateAction(action.key, action.type, action.contentType)
						this.digitalTwinStateManager.enableAction(dtStateAction)
						getLogger().info("${getLogger().name} -> onDigitalTwinBound() -> Action Enabled:" + action.key)
					}
				}
			} else
				throw NullPointerException("adaptersPhysicalAssetDescriptionMap is null")

			// Commit DT State Change Transaction to apply the changes on the DT State and notify about the change
			this.digitalTwinStateManager.commitStateTransaction();

			//Start observation to receive all incoming Digital Action through active Digital Adapter
			//Without this call the Shadowing Function will not receive any notifications or callback about
			//incoming request to execute an exposed DT's Action
			observeDigitalActionEvents();

			//Notify the DT Core that the Bounding phase has been correctly completed and the DT has evaluated its
			//internal status according to what is available and declared through the Physical Adapters
			notifyShadowingSync();

		} catch (e: Exception) {
			e.printStackTrace()
		}
	}

	fun addStringProperty(key: String, link: String) {
		// NEW from 0.3.0 -> Start DT State Change Transaction
		digitalTwinStateManager.startStateTransaction()

		//Create and write the property on the DT's State
		digitalTwinStateManager.createProperty(
			DigitalTwinStateProperty<String>(
				"DT-$key",
				link
			)
		)

		// NEW from 0.3.0 -> Commit DT State Change Transaction to apply the changes on the DT State and notify about the change
		digitalTwinStateManager.commitStateTransaction()
	}

	fun deleteProperty(key: String) {
		// NEW from 0.3.0 -> Start DT State Change Transaction
		digitalTwinStateManager.startStateTransaction()

		//Create and write the property on the DT's State
		digitalTwinStateManager.deleteProperty("DT-$key")

		// NEW from 0.3.0 -> Commit DT State Change Transaction to apply the changes on the DT State and notify about the change
		digitalTwinStateManager.commitStateTransaction()
	}

	abstract fun getLogger(): Logger
}
