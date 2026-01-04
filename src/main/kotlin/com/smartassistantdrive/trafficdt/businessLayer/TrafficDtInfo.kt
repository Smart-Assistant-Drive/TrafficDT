package com.smartassistantdrive.trafficdt.businessLayer

import kotlinx.serialization.Serializable

@Serializable
data class TrafficDtInfo(var link: String, val roadId: String, val direction: Int, val numLanes: Int, val numBlocks: Int)
