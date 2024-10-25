package vn.com.unit.studentmanagerapi.util;

import org.springframework.web.multipart.MultipartFile;
import vn.com.unit.studentmanagerapi.exception.AppException;
import vn.com.unit.studentmanagerapi.exception.ErrorCode;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class MyImageUtil {

    private static final long MAX_FILE_SIZE = (500 * 1024); // 500KB
    private static final List<String> ACCEPTED_FILE_TYPES = Arrays.asList(
            "image/jpeg",
            "image/png",
            "image/jpg",
            "image/webp",
            "image/svg+xml",
            "image/vnd.adobe.photoshop"
    );

    public static void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.AVATAR_NULL);
        }

        // check file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new AppException(ErrorCode.AVATAR_TOO_LARGE);
        }

        // check file type
        String contentType = file.getContentType();
        if (!ACCEPTED_FILE_TYPES.contains(contentType)) {
            throw new AppException(ErrorCode.AVATAR_UNSUPPORTED_FORMAT);
        }
    }

    public static String convertToBase64(byte[] photo) {
        return Base64.getEncoder().encodeToString(photo);
    }

    public static byte[] convertFileToBytes(MultipartFile photo) {
        try {
            return photo.getBytes();
        } catch (IOException e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
    }
}
