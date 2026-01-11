package com.smartassistantdrive.trafficdt.businessLayer

import kotlinx.serialization.Serializable

@Serializable
data class TrafficDtInfo(var link: String, var roadId: String, var direction: Int, var numLanes: Int, var numBlocks: Int, var idDt: String)
