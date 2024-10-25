package vn.com.unit.studentmanagerapi.entity.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum Role {
    ADMIN('A'),
    STUDENT('T'),

    @Deprecated
    NULL('N')
    ;

    final char code;

    public static Role fromCode(char code) {
        switch (code) {
            case 'A':
                return ADMIN;
            case 'T':
                return STUDENT;
            default:
                return NULL;
        }
    }



}
