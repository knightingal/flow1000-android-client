import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter_module/main.dart';
import 'package:flutter_module/scroll.dart';
import 'package:path_provider/path_provider.dart';

import 'struct/album_info.dart';
import 'struct/slot.dart';

class SectionContentPage extends StatefulWidget {
  const SectionContentPage({super.key, required this.albumIndex});

  final int albumIndex;

  @override
  State<StatefulWidget> createState() {
    return SectionContentPageState();
  }
}

class SectionContentPageState extends State<SectionContentPage> {
  late double width;
  Future<SectionDetail> fetchAlbumIndex() async {
    List<Map<String, Object?>> imgRow = await db.queryPicInfoBySectionId(
      widget.albumIndex,
    );
    List<Map<String, Object?>> sectionRow = await db
        .querySectionInfoBySectionId(widget.albumIndex);

    return SectionDetail.fromJson(sectionRow[0], imgRow);
  }

  SectionDetail? albumInfoList;
  SlotGroup slotGroup = SlotGroup.fromCount(1);

  @override
  void initState() {
    super.initState();

    Future<SectionDetail> futureAlbumInfoList = fetchAlbumIndex();
    Future<Directory?> futureDirectory = getExternalStorageDirectory();

    Future.wait([futureAlbumInfoList, futureDirectory]).then((onValue) {
      SectionDetail sectionDetail = onValue[0] as SectionDetail;

      Directory? directory = onValue[1] as Directory?;
      sectionDetail.rootPath = directory is Directory
          ? "${directory.path}${Platform.pathSeparator}Download"
          : "unknown";

      for (int i = 0; i < sectionDetail.pics.length; i++) {
        ImgDetail albumInfo = sectionDetail.pics[i];
        double coverHeight;
        double coverWidth;
        if (slotGroup.slots.length == 1 && width > albumInfo.width) {
          coverWidth = albumInfo.width.toDouble();
          coverHeight = albumInfo.height.toDouble();
        } else {
          coverWidth = width / slotGroup.slots.length;
          coverHeight = albumInfo.height * (coverWidth / albumInfo.width);
        }

        albumInfo.realHeight = coverHeight;
        albumInfo.realWidth = coverWidth;

        slotGroup.insertSlotItem(SlotItem(i, albumInfo.realHeight));
      }
      setState(() {
        albumInfoList = sectionDetail;
      });

      return null;
    });
  }

  void subscribeAlbum() async {
    // nothing to do
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
          return Image.file(
            File(albumInfoList!.pics[index].toUrl(albumInfoList!)),
            key: Key("content-$index"),
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
