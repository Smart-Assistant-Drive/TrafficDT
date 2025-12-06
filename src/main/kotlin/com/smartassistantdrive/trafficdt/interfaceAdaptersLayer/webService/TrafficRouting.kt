package com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.webService

import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json
import com.smartassistantdrive.trafficdt.interfaceAdaptersLayer.digitalAdapter.CustomHttpDigitalAdapter
import io.ktor.server.netty.EngineMain

class TrafficRouting {
	companion object {
		lateinit var adapterSingleton : CustomHttpDigitalAdapter

		fun execute(aggregateHTTPDigitalAdapter: CustomHttpDigitalAdapter) {
			adapterSingleton = aggregateHTTPDigitalAdapter
			val array = ArrayList<String>(0)
			EngineMain.main(array.toTypedArray())
		}

		fun execute() {
			val array = ArrayList<String>(0)
			EngineMain.main(array.toTypedArray())
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
		get("/getAllTrafficManagers") {
            val result = TrafficRouting.adapterSingleton.dtGetter()
            call.respond(result)
		}
		get("/getByRoadId") {
			val roadId = call.request.queryParameters["roadId"].orEmpty()
			val direction = call.request.queryParameters["direction"].orEmpty()
			if(roadId.isEmpty() || direction.isEmpty()) {
				call.respond(HttpStatusCode.BadRequest, "L'ID della risorsa deve essere un numero intero.")
			}
			val result = TrafficRouting.adapterSingleton.filter(roadId, direction.toInt())
			if(result == null) {
				call.respond(HttpStatusCode.NotFound, "Risorsa con roadId pari a $roadId non trovata.")
			} else {
				call.respond(result)
			}
		}
	}
}

fun Application.module() {
	configureRouting()
}