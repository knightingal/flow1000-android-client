package com.example.jianming.myapplication

class SectionConfig(var name: String, var encryped: Boolean, var baseUrl: String,) ;

val SECTION_CONFIG_MAP: Map<String, SectionConfig> = initAlbumConfig();

fun initAlbumConfig(): Map<String, SectionConfig> {
    return mapOf(
        "flow1000" to SectionConfig("flow1000", true, "encrypted"),
        "ship" to SectionConfig("ship", true, "encrypted"),
        "1803" to SectionConfig("1803", false, "1803"),
        "1804" to SectionConfig("1804", false, "1804"),
        "1805" to SectionConfig("1805", false, "1805"),
    );
}

fun getAlbumConfig(name: String?) : SectionConfig {

    return SECTION_CONFIG_MAP.getOrDefault(name, SectionConfig("flow1000", true, "encrypted"))
}