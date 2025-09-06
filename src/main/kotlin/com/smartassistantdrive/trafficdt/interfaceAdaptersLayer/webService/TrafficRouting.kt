package org.example.com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.webService

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json
import org.example.com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.digitalAdapter.CustomHttpDigitalAdapter

class TrafficRouting {
	companion object {
		lateinit var adapterSingleton : CustomHttpDigitalAdapter

		fun execute(aggregateHTTPDigitalAdapter: CustomHttpDigitalAdapter) {
			adapterSingleton = aggregateHTTPDigitalAdapter
			val array = ArrayList<String>(0)
			io.ktor.server.netty.EngineMain.main(array.toTypedArray())
		}

		fun execute() {
			val array = ArrayList<String>(0)
			io.ktor.server.netty.EngineMain.main(array.toTypedArray())
		}
	}
}

fun Application.configureRouting() {
	install(ContentNegotiation) {
		json(Json {
			prettyPrint = true
			isLenient = true
		})
	}
	routing {
		get("/hello") {
			call.respond(TrafficRouting.adapterSingleton.filter(2))
		}
	}
}

fun Application.module() {
	configureRouting()
}