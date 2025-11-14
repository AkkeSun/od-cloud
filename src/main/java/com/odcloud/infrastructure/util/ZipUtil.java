package com.odcloud.infrastructure.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ZipUtil {

    /**
     * 여러 파일을 ZIP으로 압축합니다.
     *
     * @param fileInfos 압축할 파일 정보 리스트 (파일명, InputStream)
     * @return 압축된 ZIP 파일의 바이트 배열
     */
    public static byte[] createZip(List<FileInfo> fileInfos) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(baos)) {

            for (FileInfo fileInfo : fileInfos) {
                try (InputStream inputStream = fileInfo.getInputStream()) {
                    ZipEntry entry = new ZipEntry(fileInfo.getFileName());
                    zos.putNextEntry(entry);

                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        zos.write(buffer, 0, bytesRead);
                    }

                    zos.closeEntry();
                } catch (Exception e) {
                    log.error("[createZip] 파일 압축 실패: {}, error: {}", fileInfo.getFileName(),
                        e.getMessage());
                    throw new RuntimeException("파일 압축 실패: " + fileInfo.getFileName(), e);
                }
            }

            zos.finish();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("[createZip] ZIP 생성 실패: {}", e.getMessage());
            throw new RuntimeException("ZIP 생성 실패", e);
        }
    }

    /**
     * ZIP 압축에 필요한 파일 정보
     */
    public static class FileInfo {

        private final String fileName;
        private final InputStream inputStream;

        public FileInfo(String fileName, InputStream inputStream) {
            this.fileName = fileName;
            this.inputStream = inputStream;
        }

        public String getFileName() {
            return fileName;
        }

        public InputStream getInputStream() {
            return inputStream;
        }
    }
}
