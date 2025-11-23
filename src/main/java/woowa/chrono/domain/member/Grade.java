package woowa.chrono.domain.member;

import java.util.Arrays;
import java.util.List;

public enum Grade {
    NEWBIE("공부새싹"),
    REGULAR("공부벌레"),
    ADMIN("공부대장");


    private final String displayName;

    Grade(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Grade fromRoleName(String roleName) {
        return switch (roleName) {
            case "공부새싹" -> NEWBIE;
            case "공부대장" -> ADMIN;
            case "공부벌레" -> REGULAR;
            default -> throw new IllegalStateException("Unexpected value: " + roleName);
        };
    }

    public static List<String> getAllRoleNames() {
        return Arrays.stream(values())
                .map(Grade::getDisplayName)
                .toList();
    }
}
