package ipp.estg.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class containing validation methods.
 * This class provides methods for validating different types of input data.
 */
public class Validations {

    /**
     * Validates an email address using a regular expression.
     * The regular expression checks for the following pattern:
     * <ul>
     *     <li>Starts with a valid username that can include letters, digits, underscores, and hyphens.</li>
     *     <li>Contains an "@" symbol separating the username and domain.</li>
     *     <li>The domain consists of valid characters including letters, digits, hyphens, and periods.</li>
     *     <li>Ends with a valid top-level domain (TLD) with at least two letters.</li>
     * </ul>
     *
     * @param email the email address to be validated
     * @return {@code true} if the email address matches the pattern, {@code false} otherwise
     */
    public static Boolean emailValidation(String email) {
        String regex = "^(?=.{1,64}@)[\\p{L}0-9_-]+(\\.[\\p{L}0-9_-]+)*@"
                + "[^-][\\p{L}0-9-]+(\\.[\\p{L}0-9-]+)*(\\.\\p{L}{2,})$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }
}
