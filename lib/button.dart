import 'package:flutter/material.dart';

class Button extends StatelessWidget {
  final Widget child;
  final VoidCallback onPressed;

  const Button({super.key, required this.child, required this.onPressed});

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.all(4),
      child: ElevatedButton(
        onPressed: onPressed,
        style: ElevatedButton.styleFrom(
          padding: const EdgeInsets.all(8),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(8),
          ),
        ),
        child: child,
      ),
    );
  }
}
