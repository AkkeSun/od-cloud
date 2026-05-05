-- ============================================================
-- file_history 테이블: 파일 등록/수정/삭제 이력 관리
-- ============================================================

CREATE TABLE file_history (
    id               BIGSERIAL    NOT NULL,
    file_id          BIGINT       NOT NULL,            -- 원본 file_info.id (삭제 후에도 이력 보존을 위해 FK 미설정)
    group_id         BIGINT       NOT NULL,
    action_type      VARCHAR(20)  NOT NULL,            -- UPLOAD | RENAME | MOVE | DELETE
    actor_email      VARCHAR(255),                     -- 작업 수행 계정 이메일
    before_file_name VARCHAR(255),                     -- 변경 전 파일명 (UPLOAD 시 NULL)
    after_file_name  VARCHAR(255),                     -- 변경 후 파일명 (DELETE 시 NULL)
    before_folder_id BIGINT,                           -- 변경 전 폴더 ID (UPLOAD 시 NULL)
    after_folder_id  BIGINT,                           -- 변경 후 폴더 ID (DELETE 시 NULL)
    file_size        BIGINT,                           -- 작업 시점 파일 크기 (bytes)
    reg_dt           TIMESTAMP    NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_file_history PRIMARY KEY (id)
);

-- 파일별 이력 조회 (단건 파일 히스토리 조회)
CREATE INDEX idx_file_history_file_id  ON file_history (file_id);

-- 그룹별 이력 조회 (그룹 전체 파일 활동 조회)
CREATE INDEX idx_file_history_group_id ON file_history (group_id);

-- 최신 이력 우선 정렬을 위한 복합 인덱스
CREATE INDEX idx_file_history_file_reg ON file_history (file_id, reg_dt DESC);

COMMENT ON TABLE  file_history                IS '파일 등록/수정/삭제 이력';
COMMENT ON COLUMN file_history.file_id        IS '대상 파일 ID (file_info 참조, 삭제 후 이력 보존을 위해 FK 미설정)';
COMMENT ON COLUMN file_history.action_type    IS '작업 유형: UPLOAD(등록), RENAME(이름변경), MOVE(이동), DELETE(삭제)';
COMMENT ON COLUMN file_history.actor_email    IS '작업 수행 계정 이메일';
COMMENT ON COLUMN file_history.before_file_name IS '변경 전 파일명 (UPLOAD 시 NULL)';
COMMENT ON COLUMN file_history.after_file_name  IS '변경 후 파일명 (DELETE 시 NULL)';
COMMENT ON COLUMN file_history.before_folder_id IS '변경 전 폴더 ID (UPLOAD 시 NULL)';
COMMENT ON COLUMN file_history.after_folder_id  IS '변경 후 폴더 ID (DELETE 시 NULL)';
COMMENT ON COLUMN file_history.file_size      IS '작업 시점 파일 크기 (bytes)';
