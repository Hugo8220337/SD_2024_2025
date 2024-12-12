package ipp.estg.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for string manipulation.
 * This class provides methods for handling strings, such as splitting a command line string while respecting quoted sections.
 */
public class StringUtils {

    /**
     * Splits a command line string into individual tokens, respecting quoted sections.
     *
     * <p>For example, the input 'REGISTER "John Doe" password' will be split into the following tokens:
     * ["REGISTER", "John Doe", "password"].
     * The quoted sections are preserved as single tokens, even if they contain spaces.
     *
     * @param input The input command line string to be split.
     * @return An array of strings containing the individual tokens from the input string.
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
                if (!currentToken.isEmpty()) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0);
                }
            } else {
                currentToken.append(c);
            }
        }

        // Add the last token if there is one
        if (!currentToken.isEmpty()) {
            tokens.add(currentToken.toString());
        }

        return tokens.toArray(new String[0]);
    }
}
