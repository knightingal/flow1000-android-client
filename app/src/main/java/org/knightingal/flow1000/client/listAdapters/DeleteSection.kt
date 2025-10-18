package org.knightingal.flow1000.client.listAdapters

import org.knightingal.flow1000.client.task.ConcurrencyJsonApiTask
import org.knightingal.flow1000.client.util.EnvArgs.Companion.SERVER_IP
import org.knightingal.flow1000.client.util.EnvArgs.Companion.SERVER_PORT

fun postDeleteSection(sectionId: Long) {
    val completeUrl = "http://${SERVER_IP}:${SERVER_PORT}" +
            "/local1000/deleteSection?id=" + sectionId
    ConcurrencyJsonApiTask.startPost(completeUrl, "") {}
}
