package org.knightingal.flow1000.client.listAdapters

import SERVER_IP
import SERVER_PORT
import org.knightingal.flow1000.client.Tasks.ConcurrencyJsonApiTask

fun postDeleteSection(sectionId: Long) {
    val completeUrl = "http://${SERVER_IP}:${SERVER_PORT}" +
            "/local1000/deleteSection?id=" + sectionId
    ConcurrencyJsonApiTask.startPost(completeUrl, "") {}
}
