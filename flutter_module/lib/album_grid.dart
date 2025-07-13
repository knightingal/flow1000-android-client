import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;

import 'config.dart';
import 'struct/album_info.dart';

class AlbumGridPage extends StatefulWidget {
  const AlbumGridPage({super.key});

  @override
  State<StatefulWidget> createState() {
    return AlbumGridPageState();
  }
}

class AlbumGridPageState extends State<AlbumGridPage> {
  Future<List<AlbumInfo>> fetchAlbumIndex() async {
    final response = await http.get(Uri.parse(albumIndexUrl()));
    if (response.statusCode == 200) {
      List<dynamic> jsonArray = jsonDecode(response.body);
      List<AlbumInfo> albumInfoList = jsonArray
          .map((e) => AlbumInfo.fromJson(e, ""))
          .toList();
      return albumInfoList;
    } else {
      throw Exception("Failed to load album");
    }
  }

  late Future<List<AlbumInfo>> futureAblumList;

  @override
  void initState() {
    super.initState();
    futureAblumList = fetchAlbumIndex();
  }

  @override
  Widget build(BuildContext context) {
    Widget body;
    body = FutureBuilder<List<AlbumInfo>>(
      future: futureAblumList,
      builder: (context, snapshot) {
        if (snapshot.hasData && snapshot.data!.isNotEmpty) {
          return LayoutBuilder(
            builder: (context, constraints) {
              return GridView.builder(
                itemCount: snapshot.data!.length,
                gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                  childAspectRatio: 4 / 3,
                  crossAxisCount: 4,
                ),
                itemBuilder: (context, index) {
                  // return Text(snapshot.data![index].name);
                  return Image.network(snapshot.data![index].toCoverUrl());
                },
              );
            },
          );
        } else {
          return const Text("");
        }
      },
    );

    return body;
  }
}
