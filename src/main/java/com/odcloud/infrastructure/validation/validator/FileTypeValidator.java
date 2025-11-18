package com.odcloud.infrastructure.validation.validator;

import com.odcloud.infrastructure.util.TextUtil;
import com.odcloud.infrastructure.validation.FileType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import org.springframework.web.multipart.MultipartFile;

public class FileTypeValidator implements
    ConstraintValidator<FileType, MultipartFile> {

    private String[] allowedTypes;

    @Override
    public void initialize(FileType annotation) {
        this.allowedTypes = annotation.allowed();
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return true;
        }

        String fileExtension = TextUtil.getFileExtension(file).toUpperCase();
        return Arrays.asList(allowedTypes).contains(fileExtension);
    }
}