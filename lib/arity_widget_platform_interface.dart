import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'arity_widget_method_channel.dart';

abstract class ArityWidgetPlatform extends PlatformInterface {
  /// Constructs a ArityWidgetPlatform.
  ArityWidgetPlatform() : super(token: _token);

  static final Object _token = Object();

  static ArityWidgetPlatform _instance = MethodChannelArityWidget();

  /// The default instance of [ArityWidgetPlatform] to use.
  ///
  /// Defaults to [MethodChannelArityWidget].
  static ArityWidgetPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [ArityWidgetPlatform] when
  /// they register themselves.
  static set instance(ArityWidgetPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
