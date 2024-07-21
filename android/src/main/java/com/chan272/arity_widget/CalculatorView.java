package com.chan272.arity_widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.javia.arity.Function;
import org.javia.arity.Symbols;

import java.util.ArrayList;
import java.util.Map;

public class CalculatorView extends LinearLayout {
    private static final Symbols symbols = new Symbols();
    private GraphView graphView;
    private Graph3dView graph3dView;
    static ArrayList<Function> graphedFunction = new ArrayList<>();
    static boolean useSmoothShading3D = true;
    static final int resolution3D = 72;

    private Map<String, Object> creationParams;

    public CalculatorView(@NonNull Context context, Object params) {
        super(context);
        this.creationParams = (Map<String, Object>) params;
        init(context);
    }

    public CalculatorView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setOrientation(LinearLayout.VERTICAL);
        setSaveEnabled(true);
        setFocusableInTouchMode(true);

        initGraphView(context);
        initGraph3dView(context);

        evaluate();
    }

    private void initGraphView(Context context) {
        graphView = new GraphView(context);
        LayoutParams graphParams = new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f);
        graphView.setLayoutParams(graphParams);
        graphView.setVisibility(View.GONE);
        addView(graphView);
    }

    private void initGraph3dView(Context context) {
        graph3dView = new Graph3dView(context);
        LayoutParams graph3dParams = new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f);
        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16,
                context.getResources().getDisplayMetrics());
        graph3dParams.setMargins(margin, margin, margin, margin);
        graph3dView.setLayoutParams(graph3dParams);
        graph3dView.setVisibility(View.GONE);
        addView(graph3dView);
    }

    private void evaluate() {
        String equation = "4×sin(x^2+y^2)÷(1+x^2+y^2)×cos(x×y)";
        if (creationParams != null && creationParams.containsKey("equation")) {
            equation = (String) creationParams.get("equation");
        }
        evaluateExpression(equation);
    }

    private void evaluateExpression(String expression) {
        if (expression.isEmpty())
            return;

        expression = LatexToUnicodeConverter.convert(expression);
        System.out.println("Converted: " + expression);
        graphedFunction
                .clear();

        for (String slice : expression.split(";")) {
            try {
                Function f = symbols.compile(slice);
                graphedFunction
                        .add(f);
            } catch (Exception e) {
                log("Error: " + e);
            }
        }

        updateGraph();
    }

    private void updateGraph() {
        if (graphedFunction
                .isEmpty())
            return;

        if (graphedFunction
                .size() == 1) {
            showSingleFunctionGraph(graphedFunction
                    .get(0));
        } else {
            showMultipleFunctionsGraph();
        }
    }

    private void showSingleFunctionGraph(Function function) {
        if (function == null) {
            hideAllGraphs();
        } else if (function.arity() == 1) {
            showGraph2D(function);
        } else if (function.arity() == 2) {
            showGraph3D(function);
        } else {
            hideAllGraphs();
        }
    }

    private void showMultipleFunctionsGraph() {
        graphView.setFunctions(graphedFunction);
        showGraph2D(null);
    }

    private void showGraph2D(Function function) {
        if (function != null) {
            graphView.setFunction(function);
        }
        graph3dView.setVisibility(View.GONE);
        graph3dView.onPause();
        graphView.setVisibility(View.VISIBLE);
    }

    private void showGraph3D(Function function) {
        graph3dView.setFunction(function);
        graphView.setVisibility(View.GONE);
        graph3dView.setVisibility(View.VISIBLE);
        graph3dView.onResume();
    }

    private void hideAllGraphs() {
        graphView.setVisibility(View.GONE);
        graph3dView.setVisibility(View.GONE);
    }

    static void log(String message) {
        android.util.Log.d("Calculator", message);
    }
}