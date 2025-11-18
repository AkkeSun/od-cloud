package com.odcloud.infrastructure.util;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public class TextUtil {

    public static String truncateTextLimit(String text) {
        try {
            if (text.getBytes(StandardCharsets.UTF_8).length < 65533) {
                return text;
            }
        } catch (Exception ignore) {
        }

        int byteCount = 0;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            int charByteSize = String.valueOf(c)
                .getBytes(StandardCharsets.UTF_8).length;

            if (byteCount + charByteSize > 65535) {
                break;
            }

            byteCount += charByteSize;
            sb.append(c);
        }

        return sb.toString();
    }

    public static String getFileExtension(MultipartFile file) {
        return StringUtils.getFilenameExtension(
            URLDecoder.decode(file.getOriginalFilename(), StandardCharsets.UTF_8));
    }
}
