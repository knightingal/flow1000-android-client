Map<String, String> albumMap = {
  "1000": "source",
  "ship": "source",
  "1803": "1803",
  "1804": "1804",
  "1805": "1805",
  "1806": "1806",
  "1807": "1807",
};

class SectionDetail {
  final String dirName;
  // final int picPage;
  final List<ImgDetail> pics;
  final String album;
  final String title;
  final String timeStampe;
  late String rootPath;

  SectionDetail({
    required this.dirName,
    // required this.picPage,
    required this.pics,
    required this.album,
    required this.title,
    required this.timeStampe,
  });

  factory SectionDetail.fromJson(
    Map<String, dynamic> json,
    List<Map<String, dynamic>> picJson,
  ) {
    final String dirName = json["name"];
    return SectionDetail(
      dirName: dirName,
      // picPage: json["picPage"],
      pics: picJson.map((e) => ImgDetail.fromJson(e)).toList(),
      album: json["album"],
      title: json["name"],
      timeStampe: json["mtime"],
    );
  }
}

class ImgDetail {
  final String name;
  final int width;
  final int height;
  double realHeight = 0;
  double realWidth = 0;

  String toUrl(SectionDetail sectionDetail) =>
      "${sectionDetail.rootPath}/${sectionDetail.dirName}/$name";

  ImgDetail({required this.name, required this.width, required this.height});

  factory ImgDetail.fromJson(Map<String, dynamic> json) {
    return ImgDetail(
      name: json["name"],
      width: json["width"],
      height: json["height"],
    );
  }
}

class AlbumInfo {
  final int index;
  final String dirName;
  final String cover;
  final int coverWidth;
  final int coverHeight;
  final String album;
  final String clientStatus;
  final String title;
  final String timeStampe;
  double realWidth = 0;
  double realHeight = 0;
  double frameWidth = 0;
  double frameHeight = 0;
  double cardWidth = 0;
  double cardHeight = 0;

  String toCoverUrl() {
    // return "http://192.168.2.12:3002/linux1000/encrypted/$name/$cover";
    return "http://192.168.2.12:3002/linux1000/${albumMap[album]}/$dirName/${cover.replaceAll(".bin", "")}";
  }

  AlbumInfo({
    required this.index,
    required this.dirName,
    required this.cover,
    required this.coverWidth,
    required this.coverHeight,
    required this.album,
    required this.clientStatus,
    required this.title,
    required this.timeStampe,
  });

  factory AlbumInfo.fromJson(Map<String, dynamic> json) {
    final String dirName = json["name"];

    return AlbumInfo(
      index: json["index"],
      dirName: dirName,
      cover: json["cover"],
      coverWidth: json["coverWidth"],
      coverHeight: json["coverHeight"],
      album: json["album"],
      clientStatus: json["clientStatus"],
      title: json["title"],
      timeStampe: json["mtime"],
    );
  }
}
