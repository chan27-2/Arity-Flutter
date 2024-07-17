import 'package:flutter/material.dart';

class HistorySection extends StatelessWidget {
  final List<String> history;
  final Function(String) onHistoryItemSelected;

  const HistorySection({
    super.key,
    required this.history,
    required this.onHistoryItemSelected,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Padding(
          padding: EdgeInsets.all(8.0),
          child: Text('History', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
        ),
        SizedBox(
          height: 100,
          child: ListView.builder(
            itemCount: history.length,
            itemBuilder: (context, index) {
              return ListTile(
                title: Text(history[index]),
                onTap: () => onHistoryItemSelected(history[index]),
              );
            },
          ),
        ),
      ],
    );
  }
}
