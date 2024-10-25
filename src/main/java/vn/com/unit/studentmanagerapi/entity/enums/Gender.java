package vn.com.unit.studentmanagerapi.entity.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum Gender {
    MALE('M'),
    FEMALE('F'),
    OTHER('O');

    final char code;

    public static Gender fromCode(char code) {
        switch (code) {
            case 'M':
                return MALE;
            case 'F':
                return FEMALE;
            case 'O':
                return OTHER;
            default:
                throw new IllegalArgumentException("Unknown Gender code: " + code);
        }
    }
}
