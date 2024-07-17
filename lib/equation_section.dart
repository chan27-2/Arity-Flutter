import 'package:flutter/material.dart';
import 'package:flutter_math_fork/flutter_math.dart';

class EquationSection extends StatelessWidget {
  final String equation;
  final TextEditingController equationController;
  final FocusNode focusNode;

  const EquationSection({
    super.key,
    required this.equation,
    required this.equationController,
    required this.focusNode,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Padding(
          padding: EdgeInsets.all(8.0),
          child: Text('Equation', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
        ),
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 8.0),
          child: TextField(
            controller: equationController,
            focusNode: focusNode,
            readOnly: true,
            showCursor: true,
            decoration: const InputDecoration(
              border: OutlineInputBorder(),
              hintText: 'Tap to edit equation',
            ),
          ),
        ),
        Padding(
          padding: const EdgeInsets.all(8.0),
          child: Math.tex(equation, textStyle: const TextStyle(fontSize: 24)),
        ),
      ],
    );
  }
}
