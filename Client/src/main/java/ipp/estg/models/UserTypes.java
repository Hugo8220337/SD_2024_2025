package ipp.estg.models;

public enum UserTypes {
    High(3),
    Medium(2),
    Low(1),
    All(0);

    private final int level;

    UserTypes(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

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
