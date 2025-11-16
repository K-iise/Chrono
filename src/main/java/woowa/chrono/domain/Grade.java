package woowa.chrono.domain;

public enum Grade {
    NEWBIE("뉴비"),
    REGULAR("멤버"),
    ADMIN("관리자");

    private final String displayName;

    Grade(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Grade fromRoleName(String roleName) {
        return switch (roleName) {
            case "뉴비" -> NEWBIE;
            case "관리자" -> ADMIN;
            default -> REGULAR;
        };
    }
}
