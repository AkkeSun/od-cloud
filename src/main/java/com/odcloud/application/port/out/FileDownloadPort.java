package com.odcloud.application.port.out;

import java.io.InputStream;

public interface FileDownloadPort {

    /**
     * 파일의 실제 내용을 읽어옵니다.
     *
     * @param fileLoc 파일의 저장 위치 (상대 경로)
     * @return 파일의 InputStream
     */
    InputStream readFile(String fileLoc);

    /**
     * 파일이 실제로 존재하는지 확인합니다.
     *
     * @param fileLoc 파일의 저장 위치 (상대 경로)
     * @return 파일 존재 여부
     */
    boolean fileExists(String fileLoc);
}
