package ipp.estg.database.models.enums;

/**
 * Enum representing different user types with associated levels.
 * The user types are categorized by a level:
 * <ul>
 *     <li>High (level 3)</li>
 *     <li>Medium (level 2)</li>
 *     <li>Low (level 1)</li>
 *     <li>All (level 0)</li>
 * </ul>
 */
public enum UserTypes {
    High(3),
    Medium(2),
    Low(1),
    All(0);

    /**
     * The level associated with the user type.
     */
    private final int level;

    /**
     * Constructor to initialize the user type with the specified level.
     *
     * @param level the level associated with the user type.
     */
    UserTypes(int level) {
        this.level = level;
    }

    /**
     * Gets the level of the user type.
     *
     * @return the level of the user type.
     */
    public int getLevel() {
        return level;
    }

    /**
     * Returns the corresponding {@link UserTypes} enum based on the given user type string.
     * <p>
     * The string can be the level number (e.g., "3") or the user type name (e.g., "High").
     * </p>
     *
     * @param userType the user type string to convert.
     * @return the corresponding {@link UserTypes} enum, or {@code null} if the input string doesn't match any valid user type.
     */
    public static UserTypes getUserType(String userType) {
        return switch (userType) {
            case "3", "High" -> UserTypes.High;
            case "2", "Medium" -> UserTypes.Medium;
            case "1", "Low" -> UserTypes.Low;
            case "0", "All" -> UserTypes.All;
            default -> null;
        };
    }
}
