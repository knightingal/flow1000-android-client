package com.example.jianming.myapplication

class AlbumConfig(var name: String, var encryped: Boolean, var baseUrl: String,) ;

val ALBUM_CONFIG_MAP: Map<String, AlbumConfig> = initAlbumConfig();

fun initAlbumConfig(): Map<String, AlbumConfig> {
    return mapOf(
        "flow1000" to AlbumConfig("flow1000", true, "encrypted"),
        "ship" to AlbumConfig("ship", true, "encrypted"),
        "1803" to AlbumConfig("1803", false, "1803"),
        "1804" to AlbumConfig("1804", false, "1804"),
        "1805" to AlbumConfig("1805", false, "1805"),
    );
}

fun getAlbumConfig(name: String?) : AlbumConfig {

    return ALBUM_CONFIG_MAP.getOrDefault(name, AlbumConfig("flow1000", true, "encrypted"))
}