import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:arity_widget/arity_widget.dart';
import 'equation_section.dart';
import 'history_section.dart';
import 'scientific_keyboard.dart';

class Calculator3DPage extends StatefulWidget {
  final ArityParams params;

  const Calculator3DPage({super.key, required this.params});

  @override
  Calculator3DPageState createState() => Calculator3DPageState();
}

class Calculator3DPageState extends State<Calculator3DPage> {
  late TextEditingController equationController;
  late FocusNode equationFocusNode;
  List<String> equationHistory = [];
  bool showKeyboard = false;

  @override
  void initState() {
    super.initState();
    equationController = TextEditingController(text: widget.params.equation);
    equationFocusNode = FocusNode()
      ..addListener(() {
        setState(() => showKeyboard = equationFocusNode.hasFocus);
      });
  }

  @override
  void dispose() {
    equationController.dispose();
    equationFocusNode.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    print("Calculator3DPage.build");
    if (widget.params.showOnlyGraph) {
      return _buildGraphView(widget.params);
    }

    return Scaffold(
      resizeToAvoidBottomInset: false,
      body: Row(
        children: [
          Expanded(
            flex: 2,
            child: _buildLeftPanel(),
          ),
          Expanded(
            flex: 3,
            child: _buildGraphView(ArityParams(equation: equationController.text)),
          ),
        ],
      ),
      floatingActionButton: _buildKeyboardToggleButton(),
      floatingActionButtonLocation: FloatingActionButtonLocation.startFloat,
    );
  }

  Widget _buildLeftPanel() {
    return Stack(
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
                onHistoryItemSelected: _selectHistoryItem,
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
    );
  }

  Widget _buildGraphView(ArityParams params) {
    return AndroidView(
      key: ValueKey(params.hashCode),
      viewType: 'arity_widget_view',
      creationParams: params.toMap(),
      creationParamsCodec: const StandardMessageCodec(),
    );
  }

  Widget _buildKeyboardToggleButton() {
    return FloatingActionButton(
      onPressed: () => setState(() {
        showKeyboard = !showKeyboard;
        if (!showKeyboard) equationFocusNode.unfocus();
      }),
      child: Icon(showKeyboard ? Icons.keyboard_hide : Icons.keyboard),
    );
  }

  void _onKeyPressed(String key) {
    final currentPosition = equationController.selection.start;
    final text = equationController.text;

    switch (key) {
      case 'C':
        equationController.clear();
        break;
      case '⌫':
        if (currentPosition > 0) {
          equationController.text = text.replaceRange(currentPosition - 1, currentPosition, '');
          equationController.selection = TextSelection.fromPosition(TextPosition(offset: currentPosition - 1));
        }
        break;
      default:
        equationController.text = text.replaceRange(currentPosition, currentPosition, key);
        equationController.selection = TextSelection.fromPosition(TextPosition(offset: currentPosition + key.length));
    }

    _updateEquation();
  }

  void _updateEquation() {
    setState(() {
      final equation = equationController.text;
      if (!equationHistory.contains(equation)) {
        equationHistory.insert(0, equation);
        if (equationHistory.length > 5) equationHistory.removeLast();
      }
    });
  }

  void _selectHistoryItem(String historyEquation) {
    setState(() {
      equationController.text = historyEquation;
    });
    _updateEquation();
  }
}
