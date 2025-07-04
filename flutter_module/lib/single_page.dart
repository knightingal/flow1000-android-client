import 'package:flutter/material.dart';

class SinglePage extends StatelessWidget {
  const SinglePage({super.key});

  @override
  Widget build(BuildContext context) {
    double width = MediaQuery.of(context).size.width;
    return Container(
      padding: EdgeInsets.all(10),

      child: Center(
        child: Image.network(
          "http://192.168.2.12:3002/linux1000/source/20160321000141BB-39_USS_ARIZONA/0-0138026.jpg",
          width: width / 2,
          height: width / 2,
          fit: BoxFit.scaleDown,
        ),
      ),
    );
  }
}
