String apiHost() => "http://192.168.2.12:8000";
String albumIndexUrl({String? album}) =>
    "${apiHost()}/local1000/picIndexAjax?album=${album ?? ""}";
String albumContentUrl(int index) =>
    "${apiHost()}/local1000/picDetailAjax?id=$index";

String subscribeAlbumUrl(int index) =>
    "${apiHost()}/local1000/downloadSection?id=$index";
