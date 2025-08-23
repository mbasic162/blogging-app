package com.example.bloggingapp.utils;

public class UriSanitizer {
    public static String encode(String input) {
        StringBuilder sb = new StringBuilder();
        input = input.toLowerCase();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (isAllowedChar(c)) {
                sb.append(c);
            } else if (i > 0 && i < input.length() - 1 && input.charAt(i - 1) == ' ' && input.charAt(i + 1) == ' ') {
                sb.deleteCharAt(sb.length() - 1);
            }
        }
        return sb.toString().replaceAll(" ", "-");
    }

    private static boolean isAllowedChar(char c) {
        return Character.isLetterOrDigit(c) || c == '-' || c == '.' || c == '_' || c == '~' || c == ' ';
    }
}
