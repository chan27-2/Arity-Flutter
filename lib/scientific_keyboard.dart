import 'package:arity_widget/button.dart';
import 'package:flutter/material.dart';

class ScientificKeyboard extends StatelessWidget {
  final Function(String) onPressed;

  const ScientificKeyboard({super.key, required this.onPressed});

  @override
  Widget build(BuildContext context) {
    return LayoutBuilder(
      builder: (BuildContext context, BoxConstraints constraints) {
        double buttonSize = constraints.maxWidth / 5;
        return Wrap(
          children: [
            'sin',
            'cos',
            'tan',
            '(',
            ')',
            '7',
            '8',
            '9',
            '/',
            'C',
            '4',
            '5',
            '6',
            '*',
            '√',
            '1',
            '2',
            '3',
            '-',
            'x',
            '0',
            '.',
            'π',
            '+',
            '⌫',
          ]
              .map((key) => SizedBox(
                    width: buttonSize,
                    height: buttonSize,
                    child: Button(
                      child: Text(key, style: TextStyle(fontSize: buttonSize * 0.3)),
                      onPressed: () => onPressed(key),
                    ),
                  ))
              .toList(),
        );
      },
    );
  }
}
