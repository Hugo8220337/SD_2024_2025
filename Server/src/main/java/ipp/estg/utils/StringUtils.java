package ipp.estg.utils;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {
    /**
     * Splits a command line string respecting quoted sections.
     * Example: 'REGISTER "John Doe" password' -> ["REGISTER", "John Doe", "password"]
     */
    public static String[] splitCommandLine(String input) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ' ' && !inQuotes) {
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0);
                }
            } else {
                currentToken.append(c);
            }
        }

        // Add the last token if there is one
        if (currentToken.length() > 0) {
            tokens.add(currentToken.toString());
        }

        return tokens.toArray(new String[0]);
    }
}
