## Arity Flutter Plugin

This is the README for the Arity Flutter plugin, which allows you to integrate a native Android view that can process and display mathematical expressions.

**Features:**

- Leverages a native Android view for equation processing.
- Provides a `getPlatformVersion` method to retrieve the platform information.

**Installation:**

1. Add this dependency to your `pubspec.yaml` file:

```yaml
dependencies:
  arity_widget: ^1.0.0 (replace with the latest version)
```

2. Run `flutter pub get` to install the package.

**Usage:**

1. Import the necessary packages:

```dart
import 'package:arity_widget/arity_widget.dart';
```

2. Create an instance of `ArityParams` with the equation you want to process:

```dart
ArityParams params = ArityParams(equation: '4 * sin(x^2 + y^2) / (1 + x^2 + y^2) * cos(x * y)');
```

3. Use the `ArityPlugin` class to interact with the native view:

```dart
ArityPlugin().getPlatformVersion().then((version) {
  if (version != null) {
    print('Platform version: $version');
  }
});
```

4. Display the native view using `ArityView`:

```dart
class MyHomePage extends StatefulWidget {
  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Arity Flutter Plugin'),
      ),
      body: ArityView(
        params: ArityParams(equation: 'Your equation here'),
      ),
    );
  }
}
```

**Important Notes:**

- The `ArityView` widget is currently only supported on Android platforms. The fallback widget displays a message on other platforms.

**Contributing:**

We welcome contributions to this plugin! Please refer to the CONTRIBUTING file for guidelines.

**License:**

This plugin is licensed under the MIT License (see LICENSE file).
