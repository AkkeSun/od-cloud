package com.odcloud.adapter.out.persistence.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import com.odcloud.IntegrationTestSupport;
import com.odcloud.domain.model.Group;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class GroupStorageAdapterTest extends IntegrationTestSupport {

    @Autowired
    GroupStorageAdapter adapter;

    @Autowired
    GroupRepository repository;

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Nested
    @DisplayName("[register] 그룹을 저장하는 메서드")
    class Describe_register {

        @Test
        @DisplayName("[success] 그룹을 저장한다")
        void success() {
            // given
            Group group = Group.builder()
                .id("test-group")
                .description("테스트 그룹")
                .regDt(LocalDateTime.now())
                .build();

            // when
            adapter.register(group);

            // then
            assertThat(repository.count()).isEqualTo(1);
            GroupEntity savedEntity = repository.findAll().get(0);
            assertThat(savedEntity.getId()).isEqualTo("test-group");
            assertThat(savedEntity.getDescription()).isEqualTo("테스트 그룹");
            assertThat(savedEntity.getRegDt()).isNotNull();
        }

        @Test
        @DisplayName("[success] 여러 그룹을 저장할 수 있다")
        void success_multipleGroups() {
            // given
            Group group1 = Group.builder()
                .id("group-1")
                .description("그룹 1")
                .regDt(LocalDateTime.now())
                .build();

            Group group2 = Group.builder()
                .id("group-2")
                .description("그룹 2")
                .regDt(LocalDateTime.now())
                .build();

            // when
            adapter.register(group1);
            adapter.register(group2);

            // then
            assertThat(repository.count()).isEqualTo(2);
        }

        @Test
        @DisplayName("[success] 등록 시간이 저장된다")
        void success_regDateTimeSaved() {
            // given
            LocalDateTime now = LocalDateTime.now();
            Group group = Group.builder()
                .id("timestamp-group")
                .description("시간 테스트")
                .regDt(now)
                .build();

            // when
            adapter.register(group);

            // then
            GroupEntity savedEntity = repository.findAll().get(0);
            assertThat(savedEntity.getRegDt()).isNotNull();
            assertThat(savedEntity.getRegDt()).isEqualToIgnoringNanos(now);
        }
    }

    @Nested
    @DisplayName("[existsById] 그룹 ID 존재 여부를 확인하는 메서드")
    class Describe_existsById {

        @Test
        @DisplayName("[success] 존재하는 그룹 ID이면 true를 반환한다")
        void success_exists() {
            // given
            Group group = Group.builder()
                .id("existing-group")
                .description("기존 그룹")
                .regDt(LocalDateTime.now())
                .build();
            adapter.register(group);

            // when
            boolean exists = adapter.existsById("existing-group");

            // then
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("[success] 존재하지 않는 그룹 ID이면 false를 반환한다")
        void success_notExists() {
            // when
            boolean exists = adapter.existsById("nonexistent-group");

            // then
            assertThat(exists).isFalse();
        }

        @Test
        @DisplayName("[success] 여러 그룹이 있을 때 특정 ID 존재 여부를 정확히 확인한다")
        void success_existsAmongMultiple() {
            // given
            Group group1 = Group.builder()
                .id("group-1")
                .description("그룹 1")
                .regDt(LocalDateTime.now())
                .build();

            Group group2 = Group.builder()
                .id("group-2")
                .description("그룹 2")
                .regDt(LocalDateTime.now())
                .build();

            adapter.register(group1);
            adapter.register(group2);

            // when & then
            assertThat(adapter.existsById("group-1")).isTrue();
            assertThat(adapter.existsById("group-2")).isTrue();
            assertThat(adapter.existsById("group-3")).isFalse();
        }
    }
}
