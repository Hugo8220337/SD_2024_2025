package ipp.estg.database.models.enums;

public enum UserTypes {
    High(2),
    Medium(1),
    Low(0);

    private final int level;

    UserTypes(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public static UserTypes getUserType(String userType) {
        return switch (userType) {
            case "2", "High" -> UserTypes.High;
            case "1", "Medium" -> UserTypes.Medium;
            case "0", "Low" -> UserTypes.Low;
            default -> null;
        };
    }
}
