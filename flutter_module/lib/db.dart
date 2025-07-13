import 'package:flutter/material.dart';
import 'package:path/path.dart';
import 'package:sqflite/sqflite.dart';

class DB {
  late final Future<Database> database;

  void init() {
    WidgetsFlutterBinding.ensureInitialized();
    database = getDatabasesPath().then(
      (basePath) =>
          openDatabase(join(basePath, 'database-flow1000'), readOnly: true),
    );
  }

  Future<List<Map<String, Object?>>> queryDb() {
    return database.then((db) {
      return db.query(
        "PicSectionBean",
        columns: ["id", "name"],
        where: "id=?",
        whereArgs: [1],
      );
    });
  }

  Future<List<Map<String, Object?>>> querySectionInfoByAlbum(String album) {
    return database.then((db) {
      return db.query(
        "PicSectionBean",
        columns: [
          "id",
          "name",
          "album",
          "mtime",
          "coverWidth",
          "coverHeight",
          "cover",
          "clientStatus",
        ],
        where: "album=? and clientStatus=?",
        whereArgs: [album, 'LOCAL'],
      );
    });
  }

  Future<List<Map<String, Object?>>> querySectionInfoBySectionId(
    int sectionId,
  ) {
    return database.then((db) {
      return db.query(
        "PicSectionBean",
        columns: ["id", "name", "album", "mtime"],
        where: "id=?",
        whereArgs: [sectionId],
      );
    });
  }

  Future<List<Map<String, Object?>>> queryPicInfoBySectionId(int sectionId) {
    return database.then((db) {
      return db.query(
        "PicInfoBean",
        columns: ["index", "name", "width", "height"],
        where: "sectionIndex=?",
        whereArgs: [sectionId],
      );
    });
  }
}
