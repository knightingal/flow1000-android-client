import 'dart:async';
import 'dart:typed_data';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';

import 'package:http/http.dart' as http;

import '../encrypt_lib.dart' as encrypt_lib;

class EncriptImageWidget extends StatefulWidget {
  const EncriptImageWidget({super.key, this.src, this.width, this.height});

  final String? src;

  final double? width;
  final double? height;
  

  @override
  State<StatefulWidget> createState() {
    return EncriptImageState();
  }
}

class EncriptImageState extends State<EncriptImageWidget> {
  late Future<Uint8List> decriptedContentFuture;

  Future<Uint8List> fetchImage() async {
    final key = encrypt_lib.Key.fromUtf8("password");
    final iv = encrypt_lib.IV.fromUtf8("");
    final encrypter = encrypt_lib.Encrypter(
        encrypt_lib.AES(key, mode: encrypt_lib.AESMode.cfb, padding: null), iv: iv);
    String src = switch (widget.src) {
      null => "http://192.168.2.12:3002/linux1000/encrypted/20151209003040BB-29 USS NORTH DAKOTA/011739.jpg.bin",
      var s => s,
    };
    
    final reps2Future =
        // http.get(Uri.parse("http://192.168.2.12:8000/dev/aes-image"));
        http.get(Uri.parse(src));
    return reps2Future.then((resp2) {
      if (resp2.statusCode == 200) {
        Uint8List bytes = resp2.bodyBytes;
        Uint8List header = bytes.sublist(0, 1024);
        Uint8List tail = bytes.sublist(1024);
        Uint8List headerDP = encrypter.decryptUint8List(header);
        Uint8List tailDP = encrypter.decryptUint8List(tail);
        bytes.setAll(0, headerDP);
        bytes.setAll(1024, tailDP);
        
        return bytes;
      } else {
        return Uint8List.fromList([]);
      }
    });
  }

  @override
  void initState() {
    super.initState();
    decriptedContentFuture = fetchImage();
  }

  @override
  Widget build(BuildContext context) {
    Widget body = FutureBuilder<Uint8List>(
        future: decriptedContentFuture,
        builder: (context, snapshot) {
          if (snapshot.hasData && snapshot.data!.isNotEmpty) {
            return Image.memory(snapshot.data!, width: widget.width, height: widget.height,);
          } else {
            return const Text("");
          }
        });

    return body;
  }
}

