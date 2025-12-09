package at.fhtw.society.backend.session.dto;

import com.fasterxml.jackson.annotation.JsonValue;

/*
 * Backend representation of the fixed avatar ids used by the frontend.
 *
 * JSON representation uses lowercase ids ("blue", "orange", ...),
 * while the enum constants follow the usual UPPER_CASE style.
 */
public enum AvatarId {
    BLUE("blue"),
    RED("red"),
    YELLOW("yellow"),
    PURPLE("purple"),
    BROWN("brown"),
    ORANGE("orange"),
    PINK("pink"),
    TURQUOISE("turquoise"),
    WHITE("white"),
    BLACK("black");

    private final String id;

    AvatarId(String id) {
        this.id = id;
    }

    /*
     * This value will be used when serializing the enum to JSON
     */
    @JsonValue
    public String getId() {
        return id;
    }

    public static AvatarId fromValue(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("avatarId cannot be null or empty");
        }

        for (AvatarId avatarId : AvatarId.values()) {
            if (avatarId.id.equalsIgnoreCase(value)) {
                return avatarId;
            }
        }

        throw new IllegalArgumentException("Unknown avatarId: " + value);
    }

}
