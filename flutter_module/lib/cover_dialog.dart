import 'package:flutter/material.dart';

class CoverDialog extends StatelessWidget {
  const CoverDialog({super.key});

  @override
  Widget build(BuildContext context) {
    return SizedBox.shrink();
  }
}

void popupCoverDialog(BuildContext context) {
  showDialog<String>(
    context: context,
    builder:
        (BuildContext context) => Dialog(
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadiusGeometry.circular(8.0),
          ),
          child: Padding(
            padding: const EdgeInsets.all(8.0),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              mainAxisAlignment: MainAxisAlignment.center,
              children: <Widget>[
                const Text('This is a typical dialog.'),
                const SizedBox(height: 15),
                TextButton(
                  onPressed: () {
                    Navigator.pop(context);
                  },
                  child: const Text('Close'),
                ),
              ],
            ),
          ),
        ),
  );
}
