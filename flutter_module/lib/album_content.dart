import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter_module/scroll.dart';
import 'package:http/http.dart' as http;

import 'config.dart';
import 'struct/album_info.dart';
import 'struct/slot.dart';

class AlbumContentPage extends StatefulWidget {
  const AlbumContentPage({super.key, required this.albumIndex});

  final int albumIndex;

  @override
  State<StatefulWidget> createState() {
    return AlbumContentPageState();
  }
}

class AlbumContentPageState extends State<AlbumContentPage> {
  late double width;
  // Future<SectionDetail> fetchAlbumIndex() async {
  // final response = await http.get(
  //   Uri.parse(albumContentUrl(widget.albumIndex)),
  // );
  // if (response.statusCode == 200) {
  //   dynamic jsonArray = jsonDecode(response.body);
  //   SectionDetail albumInfoList = SectionDetail.fromJson(jsonArray);
  //   return albumInfoList;
  // } else {
  // throw Exception("Failed to load album");
  // }
  // }

  SectionDetail? albumInfoList;
  SlotGroup slotGroup = SlotGroup.fromCount(1);

  @override
  void initState() {
    super.initState();
    // fetchAlbumIndex().then((albumInfoList) {
    //   for (int i = 0; i < albumInfoList.pics.length; i++) {
    //     ImgDetail albumInfo = albumInfoList.pics[i];
    //     double coverHeight;
    //     double coverWidth;
    //     if (slotGroup.slots.length == 1 && width > albumInfo.width) {
    //       coverWidth = albumInfo.width.toDouble();
    //       coverHeight = albumInfo.height.toDouble();
    //     } else {
    //       coverWidth = width / slotGroup.slots.length;
    //       coverHeight = albumInfo.height * (coverWidth / albumInfo.width);
    //     }

    //     albumInfo.realHeight = coverHeight;
    //     albumInfo.realWidth = coverWidth;

    //     slotGroup.insertSlotItem(SlotItem(i, albumInfo.realHeight));
    //   }
    //   setState(() {
    //     this.albumInfoList = albumInfoList;
    //   });
    // });
  }

  void subscribeAlbum() async {
    final url = subscribeAlbumUrl(widget.albumIndex);
    final response = await http.post(Uri.parse(url));
    if (response.statusCode != 200) {
      throw Exception("Failed to subscribe album");
    }
  }

  final FocusNode _buttonFocusNode = FocusNode(debugLabel: 'Menu Button');

  @override
  Widget build(BuildContext context) {
    width = MediaQuery.of(context).size.width;
    AppBar? appBar;
    Widget body;
    if (albumInfoList == null || albumInfoList!.pics.isEmpty) {
      body = Text("AlbumIndexPage");
    } else {
      appBar = AppBar(
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
        title: Text(albumInfoList!.title),
        actions: <Widget>[
          MenuAnchor(
            childFocusNode: _buttonFocusNode,
            menuChildren: <Widget>[
              MenuItemButton(
                child: Text('Detail'),
                onPressed: () {
                  showDialog(
                    context: context,
                    builder: (BuildContext context) {
                      return Dialog(
                        child: Padding(
                          padding: const EdgeInsets.all(8.0),
                          child: Text(albumInfoList!.dirName),
                        ),
                      );
                    },
                  );
                },
              ),
              MenuItemButton(
                child: Text('Subscribe'),
                onPressed: () {
                  subscribeAlbum();
                },
              ),
            ],
            builder:
                (
                  BuildContext context,
                  MenuController controller,
                  Widget? child,
                ) {
                  return IconButton(
                    focusNode: _buttonFocusNode,
                    icon: const Icon(Icons.menu),
                    onPressed: () {
                      if (controller.isOpen) {
                        controller.close();
                      } else {
                        controller.open();
                      }
                    },
                  );
                },
          ),
        ],
      );
      body = CustomScrollViewWrap(
        slots: slotGroup,
        builder: (BuildContext context, int index) {
          return Image.network(
            key: Key("content-$index"),
            albumInfoList!.pics[index].toUrl(albumInfoList!),
            width: albumInfoList!.pics[index].realWidth,
            height: albumInfoList!.pics[index].realHeight,
          );
        },
        totalLength: albumInfoList!.pics.length,
      );
    }
    return Scaffold(body: body, appBar: appBar);
  }
}
