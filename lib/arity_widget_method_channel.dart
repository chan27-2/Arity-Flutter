import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'arity_widget_platform_interface.dart';

/// An implementation of [ArityWidgetPlatform] that uses method channels.
class MethodChannelArityWidget extends ArityWidgetPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('arity_widget');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
