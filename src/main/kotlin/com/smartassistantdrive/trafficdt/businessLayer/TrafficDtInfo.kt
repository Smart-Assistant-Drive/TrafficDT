package com.smartassistantdrive.trafficdt.businessLayer

import kotlinx.serialization.Serializable

@Serializable
data class TrafficDtInfo(val link: String, val roadId: String, val direction: Int, val numLanes: Int, val numBlocks: Int)
