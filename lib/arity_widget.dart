import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';

import 'arity_widget_platform_interface.dart';

class ArityPlugin {
  Future<String?> getPlatformVersion() {
    return ArityWidgetPlatform.instance.getPlatformVersion();
  }
}

class ArityParams {
  final String equation;

  const ArityParams({required this.equation});

  Map<String, dynamic> toMap() {
    return <String, dynamic>{
      'equation': equation,
    };
  }
}

class ArityView extends StatelessWidget {
  final ArityParams params;
  const ArityView({super.key, required this.params});

  @override
  Widget build(BuildContext context) {
    // This is used in place of `AndroidView` when running on iOS
    const Text fallback = Text('MyAndroidView is not available on this platform');

    if (defaultTargetPlatform == TargetPlatform.android) {
      return AndroidView(
        key: ValueKey(params),
        viewType: 'arity_widget_view',
        creationParams: params.toMap(),
        creationParamsCodec: const StandardMessageCodec(),
      );
    }

    return fallback;
  }
}
