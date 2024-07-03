import 'package:flutter_test/flutter_test.dart';
import 'package:arity_widget/arity_widget.dart';
import 'package:arity_widget/arity_widget_platform_interface.dart';
import 'package:arity_widget/arity_widget_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockArityWidgetPlatform with MockPlatformInterfaceMixin implements ArityWidgetPlatform {
  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final ArityWidgetPlatform initialPlatform = ArityWidgetPlatform.instance;

  test('$MethodChannelArityWidget is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelArityWidget>());
  });

  test('getPlatformVersion', () async {
    ArityPlugin arityWidgetPlugin = ArityPlugin();
    MockArityWidgetPlatform fakePlatform = MockArityWidgetPlatform();
    ArityWidgetPlatform.instance = fakePlatform;

    expect(await arityWidgetPlugin.getPlatformVersion(), '42');
  });
}
