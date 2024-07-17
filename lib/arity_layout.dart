import 'dart:math';

import 'package:arity_widget/arity_widget.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_math_fork/flutter_math.dart';
import 'package:math_expressions/math_expressions.dart';

class Calculator3DPage extends StatefulWidget {
  final ArityParams params;

  const Calculator3DPage({super.key, required this.params});
  @override
  _Calculator3DPageState createState() => _Calculator3DPageState();
}

class _Calculator3DPageState extends State<Calculator3DPage> {
  late String equation;
  String result = '';
  bool showKeyboard = false;

  List<String> equationHistory = [];
  TextEditingController equationController = TextEditingController();
  late ArityParams finalParams;
  FocusNode equationFocusNode = FocusNode();

  @override
  void initState() {
    super.initState();
    equation = widget.params.equation;
    equationController.text = widget.params.equation;
    finalParams = ArityParams(equation: widget.params.equation);
    equationFocusNode.addListener(() {
      if (equationFocusNode.hasFocus) {
        setState(() {
          showKeyboard = true;
        });
      }
    });
  }

  @override
  void dispose() {
    equationFocusNode.dispose();
    equationController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    if (widget.params.showOnlyGraph) {
      return AndroidView(
        key: ValueKey(finalParams),
        viewType: 'arity_widget_view',
        creationParams: widget.params.toMap(),
        creationParamsCodec: const StandardMessageCodec(),
      );
    }
    return Scaffold(
      resizeToAvoidBottomInset: false,
      body: Row(
        children: [
          Expanded(
            flex: 2,
            child: Stack(
              children: [
                Column(
                  children: [
                    EquationSection(
                      equation: widget.params.equation,
                      equationController: equationController,
                      focusNode: equationFocusNode,
                    ),
                    Expanded(
                      child: HistorySection(
                        history: equationHistory,
                        onHistoryItemSelected: selectHistoryItem,
                      ),
                    ),
                  ],
                ),
                if (showKeyboard)
                  Positioned(
                    left: 0,
                    right: 0,
                    bottom: 0,
                    child: ScientificKeyboard(onPressed: _onKeyPressed),
                  ),
              ],
            ),
          ),
          Expanded(
            flex: 3,
            child: Container(
              padding: EdgeInsets.all(16),
              child: AndroidView(
                key: ValueKey(finalParams),
                viewType: 'arity_widget_view',
                creationParams: finalParams.toMap(),
                creationParamsCodec: const StandardMessageCodec(),
              ),
            ),
          ),
        ],
      ),
      floatingActionButtonLocation: FloatingActionButtonLocation.startFloat,
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          setState(() {
            showKeyboard = !showKeyboard;
            if (!showKeyboard) {
              equationFocusNode.unfocus();
            }
          });
        },
        child: Icon(showKeyboard ? Icons.keyboard_hide : Icons.keyboard),
      ),
    );
  }

  void _onKeyPressed(String key) {
    final currentPosition = equationController.selection.start;

    if (key == 'C') {
      equationController.text = '';
    } else if (key == '⌫') {
      if (currentPosition > 0) {
        equationController.text = equationController.text.substring(0, currentPosition - 1) +
            equationController.text.substring(currentPosition);
        equationController.selection = TextSelection.fromPosition(
          TextPosition(offset: currentPosition - 1),
        );
      }
    } else {
      equationController.text = equationController.text.substring(0, currentPosition) +
          key +
          equationController.text.substring(currentPosition);
      equationController.selection = TextSelection.fromPosition(
        TextPosition(offset: currentPosition + key.length),
      );
    }
    updateEquation();
  }

  void updateEquation() {
    setState(() {
      equation = equationController.text;
      finalParams = ArityParams(equation: equation);
      if (!equationHistory.contains(equation)) {
        equationHistory.insert(0, equation);
        if (equationHistory.length > 5) equationHistory.removeLast();
      }
    });
  }

  void selectHistoryItem(String historyEquation) {
    setState(() {
      equation = historyEquation;
      finalParams = ArityParams(equation: equation);
      equationController.text = historyEquation;
    });
    updateEquation();
  }
}

class ScientificKeyboard extends StatelessWidget {
  final Function(String) onPressed;

  ScientificKeyboard({required this.onPressed});

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

class Button extends StatelessWidget {
  final Widget child;
  final VoidCallback onPressed;

  const Button({Key? key, required this.child, required this.onPressed}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: EdgeInsets.all(4),
      child: ElevatedButton(
        child: child,
        onPressed: onPressed,
        style: ElevatedButton.styleFrom(
          padding: EdgeInsets.all(8),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(8),
          ),
        ),
      ),
    );
  }
}

class EquationSection extends StatelessWidget {
  final String equation;
  final TextEditingController equationController;
  final FocusNode focusNode;

  const EquationSection({
    Key? key,
    required this.equation,
    required this.equationController,
    required this.focusNode,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Padding(
          padding: const EdgeInsets.all(8.0),
          child: Text('Equation', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
        ),
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 8.0),
          child: TextField(
            controller: equationController,
            focusNode: focusNode,
            readOnly: true,
            showCursor: true,
            decoration: InputDecoration(
              border: OutlineInputBorder(),
              hintText: 'Tap to edit equation',
            ),
          ),
        ),
        Padding(
          padding: const EdgeInsets.all(8.0),
          child: Math.tex(equation, textStyle: TextStyle(fontSize: 24)),
        ),
      ],
    );
  }
}

class HistorySection extends StatelessWidget {
  final List<String> history;
  final Function(String) onHistoryItemSelected;

  const HistorySection({
    Key? key,
    required this.history,
    required this.onHistoryItemSelected,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Padding(
          padding: const EdgeInsets.all(8.0),
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
