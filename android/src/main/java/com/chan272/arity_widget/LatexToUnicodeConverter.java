package com.chan272.arity_widget;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LatexToUnicodeConverter {
    static String convertToSuperscript(String match) {
        return "^" + match;
    }

    public static String latexToUnicode(String latexExpression) {
        // Regular expression to find superscript patterns in LaTeX (e.g., x^{2})
        Pattern pattern = Pattern.compile("\\^\\{([^}]*)\\}");
        Matcher matcher = pattern.matcher(latexExpression);

        // Replace LaTeX superscripts with ^ notation
        StringBuffer unicodeExpression = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(unicodeExpression, convertToSuperscript(matcher.group(1)));
        }
        matcher.appendTail(unicodeExpression);

        // Replace \left and \right with their corresponding Unicode characters
        String result = unicodeExpression.toString()
                .replace("\\left(", "(")
                .replace("\\right)", ")")
                .replace("\\left[", "[")
                .replace("\\right]", "]")
                .replace("\\left\\{", "{")
                .replace("\\right\\}", "}")
                .replace("\\left|", "|")
                .replace("\\right|", "|");

        // Replace common LaTeX symbols with their Unicode equivalents
        String[][] replacements = {
                { "\\times", "\u00d7" },
                { "\\div", "\u00f7" },
                { "\\sin", "sin" },
                { "\\cos", "cos" },
                { "\\tan", "tan" },
                { "\\log", "log" },
                { "\\ln", "ln" },
                { "\\alpha", "α" },
                { "\\beta", "β" },
                { "\\gamma", "γ" },
                { "\\delta", "δ" },
                { "\\epsilon", "ε" },
                { "\\zeta", "ζ" },
                { "\\eta", "η" },
                { "\\theta", "θ" },
                { "\\iota", "ι" },
                { "\\kappa", "κ" },
                { "\\lambda", "λ" },
                { "\\mu", "μ" },
                { "\\nu", "ν" },
                { "\\xi", "ξ" },
                { "\\omicron", "ο" },
                { "\\pi", "π" },
                { "\\rho", "ρ" },
                { "\\sigma", "σ" },
                { "\\tau", "τ" },
                { "\\upsilon", "υ" },
                { "\\phi", "φ" },
                { "\\chi", "χ" },
                { "\\psi", "ψ" },
                { "\\omega", "ω" },
                { "\\Gamma", "Γ" },
                { "\\Delta", "Δ" },
                { "\\Theta", "Θ" },
                { "\\Lambda", "Λ" },
                { "\\Xi", "Ξ" },
                { "\\Pi", "Π" },
                { "\\Sigma", "Σ" },
                { "\\Upsilon", "Υ" },
                { "\\Phi", "Φ" },
                { "\\Psi", "Ψ" },
                { "\\Omega", "Ω" }
        };

        for (String[] replacement : replacements) {
            result = result.replace(replacement[0], replacement[1]);
        }

        return result;
    }
}
