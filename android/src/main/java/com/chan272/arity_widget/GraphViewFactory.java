package com.chan272.arity_widget;

// MyCanvasViewFactory.java

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.flutter.plugin.common.MessageCodec;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;

class CalculatorViewFactory implements PlatformView {
    private final CalculatorView calculatorView;

    CalculatorViewFactory(Context context, int id, Object args) {
        calculatorView = new CalculatorView(context, args);
    }

    @NonNull
    @Override
    public View getView() {
        return calculatorView;
    }

    @Override
    public void dispose() {
    }

}

public class GraphViewFactory extends PlatformViewFactory {
    private final BinaryMessenger messenger;

    GraphViewFactory(BinaryMessenger messenger) {
        super(StandardMessageCodec.INSTANCE);
        this.messenger = messenger;
    }

    @NonNull
    @Override
    public PlatformView create(Context context, int id, Object args) {
        return new CalculatorViewFactory(context, id, args);
    }
}