package com.chan272.arity_widget;

import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.LinearLayout;

import org.javia.arity.Function;
import org.javia.arity.Symbols;

import java.util.ArrayList;
import java.util.Map;

public class CalculatorView extends LinearLayout implements View.OnClickListener {
    static Symbols symbols = new Symbols();
    private GraphView graphView;
    private Graph3dView graph3dView;
    static ArrayList<Function> graphedFunction;
    private final ArrayList<Function> auxFuncs = new ArrayList<>();
    static boolean useSmoothShading3D;
    static int resolution3D;

    private Map<String, Object> creationParams;

    public CalculatorView(@NonNull Context context, Object params) {
        super(context);
        creationParams = (Map<String, Object>) params;
        init(context);
    }

    public CalculatorView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CalculatorView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOrientation(LinearLayout.VERTICAL);
        setSaveEnabled(true);
        setFocusableInTouchMode(true);

        // Create GraphView
        graphView = new GraphView(context);
        LinearLayout.LayoutParams graphParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f);
        graphView.setLayoutParams(graphParams);
        graphView.setVisibility(View.GONE);
        graphView.setId(View.generateViewId());
        addView(graphView);

        // Create Graph3dView
        graph3dView = new Graph3dView(context);
        LinearLayout.LayoutParams graph3dParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f);
        int margin = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                16,
                context.getResources().getDisplayMetrics());
        graph3dParams.setMargins(margin, margin, margin, margin);
        graph3dView.setLayoutParams(graph3dParams);
        graph3dView.setVisibility(View.GONE);
        graph3dView.setId(View.generateViewId());
        addView(graph3dView);

        graphView.setOnClickListener(this);
        graph3dView.setOnClickListener(this);

        useSmoothShading3D = true;
        resolution3D = Integer.parseInt("72");
        evaluate();
    }

    @Override
    public void onClick(View target) {
        // if (target == graphView) {
        // Intent intent = new Intent(getContext(), ShowGraph.class);
        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // getContext().startActivity(intent);
        // }
    }

    void evaluate() {
        String[] init = {
                "4\u00d7sin(x^2+y^2)\u00f7(1+x^2+y^2)\u00d7cos(x\u00d7y)",
                "sqrt(pi)\u00f70.5!",
                "e^(i\u00d7pi)",
                "ln(e^100)",
                "sin(x)",
                "x^2"
        };
        log(creationParams + "creationParams");

        if (creationParams != null) {
            String expression = (String) creationParams.get("equation");
            if (expression != null) {
                init[0] = expression;
            }
        }
        evaluate(init[0]);
    }

    private void evaluate(String text) {
        if (text.isEmpty()) {
            return;
        }

        String unicodeEquationConverted = LatexToUnicodeConverter.latexToUnicode(text);
        System.out.println("unicodeEquationConverted: " + unicodeEquationConverted);

        text = unicodeEquationConverted;

        auxFuncs.clear();
        int end = -1;
        do {
            text = text.substring(end + 1);
            end = text.indexOf(';');
            String slice = end == -1 ? text : text.substring(0, end);
            try {
                Function f = symbols.compile(slice);
                auxFuncs.add(f);
            } catch (Exception e) {
                CalculatorView.log("error: " + e);
                continue;
            }
        } while (end != -1);

        graphedFunction = auxFuncs;
        int size = auxFuncs.size();
        if (size == 0) {
            return;
        } else if (size == 1) {
            Function f = auxFuncs.get(0);
            int arity = f.arity();
            if (arity == 1 || arity == 2) {
                showGraph(f);
            } else if (arity == 0) {
                showGraph(null);
            } else {
                showGraph(null);
            }
        } else {
            graphView.setFunctions(auxFuncs);
            if (graphView.getVisibility() != View.VISIBLE) {
                graph3dView.setVisibility(View.GONE);
                graph3dView.onPause();
                graphView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void showGraph(Function f) {
        CalculatorView.log("showGraph: " + f);
        if (f == null) {
            if (graphView.getVisibility() != View.VISIBLE) {
                graphView.setVisibility(View.GONE);
                graph3dView.setVisibility(View.GONE);
            }
        } else {
            // graphedFunction = f;
            if (f.arity() == 1) {
                graphView.setFunction(f);
                if (graphView.getVisibility() != View.VISIBLE) {
                    CalculatorView.log("setting graphView visible");
                    graph3dView.setVisibility(View.GONE);
                    graph3dView.onPause();
                    graphView.setVisibility(View.VISIBLE);
                }
            } else {
                graph3dView.setFunction(f);
                if (graph3dView.getVisibility() != View.VISIBLE) {
                    graphView.setVisibility(View.GONE);
                    graph3dView.setVisibility(View.VISIBLE);
                    graph3dView.onResume();
                }
            }
        }
    }

    // log
    static void log(String mes) {
        Log.d("Calculator", mes);
    }
}