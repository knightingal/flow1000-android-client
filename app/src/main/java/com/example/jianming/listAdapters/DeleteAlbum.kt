package com.example.jianming.listAdapters

import SERVER_IP
import SERVER_PORT
import com.example.jianming.Tasks.ConcurrencyJsonApiTask

fun postDeleteAlbum(sectionId: Long) {
    val completeUrl = "http://${SERVER_IP}:${SERVER_PORT}" +
            "/local1000/deleteSection?id=" + sectionId
    ConcurrencyJsonApiTask.startPost(completeUrl, "") {}
}
