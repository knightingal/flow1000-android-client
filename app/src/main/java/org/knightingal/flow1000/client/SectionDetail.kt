package org.knightingal.flow1000.client

data class SectionDetail(var id: Int?, var dirName: String, var picPage:Long  , var pics: List<ImgDetail>, var album: String) {
    companion object {
        data class ImgDetail(var name: String, var width: Int, var height: Int)
    }
}