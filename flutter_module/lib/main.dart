import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_module/album_index.dart';
import 'package:flutter_module/db.dart';
import 'package:flutter_module/section_content.dart';
import 'package:go_router/go_router.dart';

final DB db = DB();

void main() {
  db.init();
  runApp(const MyApp());
}

final _router = GoRouter(
  routes: [
    GoRoute(
      path: '/',
      builder: (context, state) => MyHomePage(title: 'Flutter Demo Home Page'),
    ),
    GoRoute(
      path: '/section_page/:sectionId',
      builder: (context, state) {
        var sectionId = state.pathParameters["sectionId"];
        return SectionContentPage(albumIndex: int.parse(sectionId!));
      },
    ),
    // GoRoute(
    //   path: '/about_page',
    //   builder: (context, state) => const AboutPage(),
    // ),
  ],
);

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp.router(
      routerConfig: _router,

      title: 'Flutter Demo',
      theme: ThemeData(primarySwatch: Colors.blue),
    );
  }
}

class MyHomePage extends StatelessWidget {
  final String title;
  static const platform = MethodChannel('flutter/startWeb');

  const MyHomePage({super.key, required this.title});

  @override
  Widget build(BuildContext context) {
    // This method is rerun every time setState is called, for instance as done
    // by the _incrementCounter method above.
    //
    // The Flutter framework has been optimized to make rerunning build methods
    // fast, so that you can just rebuild anything that needs updating rather
    // than having to individually change instances of widgets.
    return DefaultTabController(
      length: 6,
      child: Scaffold(
        appBar: AppBar(
          // TRY THIS: Try changing the color here to a specific color (to
          // Colors.amber, perhaps?) and trigger a hot reload to see the AppBar
          // change color while the other colors stay the same.
          backgroundColor: Theme.of(context).colorScheme.inversePrimary,
          // Here we take the value from the MyHomePage object that was created by
          // the App.build method, and use it to set our appbar title.
          title: Text(title),
          bottom: const TabBar(
            tabs: [
              // Tab(text: "single"),
              Tab(text: "main"),
              Tab(text: "1803"),
              Tab(text: "1804"),
              Tab(text: "1805"),
              Tab(text: "1806"),
              Tab(text: "1807"),
            ],
          ),
        ),
        body: TabBarView(
          children: [
            // SinglePage(),
            AlbumIndexPage(album: "1000"),
            AlbumIndexPage(album: "1803"),
            AlbumIndexPage(album: "1804"),
            AlbumIndexPage(album: "1805"),
            AlbumIndexPage(album: "1806"),
            AlbumIndexPage(album: "1807"),
          ],
        ),
        // body: AlbumGridPage(),
        // body: AlbumContentPage(albumIndex: 5,),
        // body: EncriptImageWidget(),
        // body: ImageEx.network("http://192.168.2.12:3002/linux1000/encrypted/20151209003040BB-29 USS NORTH DAKOTA/011739.jpg.bin"),
        floatingActionButton: FloatingActionButton(
          onPressed: () {
            // platform.invokeMethod("aboutPage");
            // popupCoverDialog(context);
            Navigator.push(
              context,
              MaterialPageRoute(
                builder: (context) => const SectionContentPage(albumIndex: 56),
              ),
            );
          },
          tooltip: 'Increment',
          child: const Icon(Icons.add),
        ), // This trailing comma makes auto-formatting nicer for build methods.
      ),
    );
  }
}
