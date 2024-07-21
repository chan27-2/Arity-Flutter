package com.chan272.arity_widget;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LatexToUnicodeConverter {
    private static final Map<String, String> symbolMap = new HashMap<>();
    private static final Map<Character, Character> subscriptMap = new HashMap<>();

    static {
        initializeSymbolMap();
        initializeSubscriptMap();
    }

    private static void initializeSymbolMap() {
        // Basic operators
        symbolMap.put("\\times", "×");
        symbolMap.put("\\div", "÷");
        symbolMap.put("\\pm", "±");
        symbolMap.put("\\cdot", "·");
        symbolMap.put("\\bullet", "•");
        symbolMap.put("\\circ", "∘");
        symbolMap.put("\\oplus", "⊕");
        symbolMap.put("\\otimes", "⊗");

        // Greek letters
        symbolMap.put("\\alpha", "α");
        symbolMap.put("\\beta", "β");
        symbolMap.put("\\gamma", "γ");
        symbolMap.put("\\delta", "δ");
        symbolMap.put("\\epsilon", "ε");
        symbolMap.put("\\zeta", "ζ");
        symbolMap.put("\\eta", "η");
        symbolMap.put("\\theta", "θ");
        symbolMap.put("\\iota", "ι");
        symbolMap.put("\\kappa", "κ");
        symbolMap.put("\\lambda", "λ");
        symbolMap.put("\\mu", "μ");
        symbolMap.put("\\nu", "ν");
        symbolMap.put("\\xi", "ξ");
        symbolMap.put("\\omicron", "ο");
        symbolMap.put("\\pi", "π");
        symbolMap.put("\\rho", "ρ");
        symbolMap.put("\\sigma", "σ");
        symbolMap.put("\\tau", "τ");
        symbolMap.put("\\upsilon", "υ");
        symbolMap.put("\\phi", "φ");
        symbolMap.put("\\chi", "χ");
        symbolMap.put("\\psi", "ψ");
        symbolMap.put("\\omega", "ω");

        // Uppercase Greek letters
        symbolMap.put("\\Gamma", "Γ");
        symbolMap.put("\\Delta", "Δ");
        symbolMap.put("\\Theta", "Θ");
        symbolMap.put("\\Lambda", "Λ");
        symbolMap.put("\\Xi", "Ξ");
        symbolMap.put("\\Pi", "Π");
        symbolMap.put("\\Sigma", "Σ");
        symbolMap.put("\\Upsilon", "Υ");
        symbolMap.put("\\Phi", "Φ");
        symbolMap.put("\\Psi", "Ψ");
        symbolMap.put("\\Omega", "Ω");

        // Arrows
        symbolMap.put("\\rightarrow", "→");
        symbolMap.put("\\leftarrow", "←");
        symbolMap.put("\\leftrightarrow", "↔");
        symbolMap.put("\\Rightarrow", "⇒");
        symbolMap.put("\\Leftarrow", "⇐");
        symbolMap.put("\\Leftrightarrow", "⇔");

        // Set theory and logic
        symbolMap.put("\\in", "∈");
        symbolMap.put("\\notin", "∉");
        symbolMap.put("\\subset", "⊂");
        symbolMap.put("\\subseteq", "⊆");
        symbolMap.put("\\supset", "⊃");
        symbolMap.put("\\supseteq", "⊇");
        symbolMap.put("\\cap", "∩");
        symbolMap.put("\\cup", "∪");
        symbolMap.put("\\forall", "∀");
        symbolMap.put("\\exists", "∃");
        symbolMap.put("\\neg", "¬");
        symbolMap.put("\\vee", "∨");
        symbolMap.put("\\wedge", "∧");

        // Calculus and analysis
        symbolMap.put("\\partial", "∂");
        symbolMap.put("\\nabla", "∇");
        symbolMap.put("\\int", "∫");
        symbolMap.put("\\sum", "∑");
        symbolMap.put("\\prod", "∏");
        symbolMap.put("\\infty", "∞");
        symbolMap.put("\\approx", "≈");
        symbolMap.put("\\sim", "∼");
        symbolMap.put("\\cong", "≅");
        symbolMap.put("\\equiv", "≡");
        symbolMap.put("\\propto", "∝");

        // Misc
        symbolMap.put("\\therefore", "∴");
        symbolMap.put("\\because", "∵");
        symbolMap.put("\\angle", "∠");
        symbolMap.put("\\perp", "⊥");
        symbolMap.put("\\parallel", "∥");
    }

    private static void initializeSubscriptMap() {
        String normal = "0123456789aehijklmnoprstuvx";
        String subscript = "₀₁₂₃₄₅₆₇₈₉ₐₑₕᵢⱼₖₗₘₙₒₚᵣₛₜᵤᵥₓ";
        for (int i = 0; i < normal.length(); i++) {
            subscriptMap.put(normal.charAt(i), subscript.charAt(i));
        }
    }

    public static String convert(String latex) {
        String result = latex;

        // Replace symbols
        for (Map.Entry<String, String> entry : symbolMap.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }

        // Handle superscripts (now just removing braces if present)
        result = handleSuperscripts(result);

        // Handle subscripts
        result = handleSubscripts(result);

        // Handle fractions
        result = handleFractions(result);

        // Handle special cases
        result = result
                .replace("\\left", "")
                .replace("\\right", "")
                .replace("\\big", "")
                .replace("\\Big", "")
                .replace("\\bigg", "")
                .replace("\\Bigg", "");

        return result;
    }

    private static String handleSuperscripts(String input) {
        Pattern p = Pattern.compile("\\^\\{([^}]*)\\}");
        Matcher m = p.matcher(input);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, "^" + m.group(1));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private static String handleSubscripts(String input) {
        Pattern p = Pattern.compile("_\\{([^}]*)\\}");
        Matcher m = p.matcher(input);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String replacement = convertToSubscript(m.group(1));
            m.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private static String convertToSubscript(String text) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            sb.append(subscriptMap.getOrDefault(c, c));
        }
        return sb.toString();
    }

    private static String handleFractions(String input) {
        Pattern p = Pattern.compile("\\\\(d?)frac\\{([^}]*)\\}\\{([^}]*)\\}");
        Matcher m = p.matcher(input);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String numerator = m.group(2);
            String denominator = m.group(3);
            String replacement = "(" + numerator + ")÷(" + denominator + ")";
            m.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        m.appendTail(sb);
        return sb.toString();
    }
}