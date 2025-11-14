package com.odcloud.infrastructure.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ToStringUtilTest {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class TestObject {
        private String name;
        private int age;
        private String email;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class TestObjectWithDateTime {
        private String name;
        private LocalDateTime createdAt;
    }

    @Nested
    @DisplayName("[toString] 객체를 문자열로 변환")
    class Describe_toString {

        @Test
        @DisplayName("[success] 정상적으로 객체를 JSON 문자열로 변환한다")
        void success() {
            // given
            TestObject obj = TestObject.builder()
                .name("홍길동")
                .age(30)
                .email("test@example.com")
                .build();

            // when
            String result = ToStringUtil.toString(obj);

            // then
            assertThat(result).isNotNull();
            assertThat(result).contains("\"name\":\"홍길동\"");
            assertThat(result).contains("\"age\":30");
            assertThat(result).contains("\"email\":\"test@example.com\"");
        }

        @Test
        @DisplayName("[success] null 객체는 빈 문자열을 반환한다")
        void success_nullObject() {
            // when
            String result = ToStringUtil.toString(null);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("[success] 빈 객체를 문자열로 변환한다")
        void success_emptyObject() {
            // given
            TestObject obj = TestObject.builder().build();

            // when
            String result = ToStringUtil.toString(obj);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("[success] 문자열 객체를 변환한다")
        void success_stringObject() {
            // given
            String str = "Hello World";

            // when
            String result = ToStringUtil.toString(str);

            // then
            assertThat(result).isEqualTo("\"Hello World\"");
        }

        @Test
        @DisplayName("[success] 숫자 객체를 문자열로 변환한다")
        void success_numberObject() {
            // given
            Integer number = 123;

            // when
            String result = ToStringUtil.toString(number);

            // then
            assertThat(result).isEqualTo("123");
        }

        @Test
        @DisplayName("[success] Boolean 객체를 문자열로 변환한다")
        void success_booleanObject() {
            // given
            Boolean bool = true;

            // when
            String result = ToStringUtil.toString(bool);

            // then
            assertThat(result).isEqualTo("true");
        }

        @Test
        @DisplayName("[success] List 객체를 문자열로 변환한다")
        void success_listObject() {
            // given
            List<String> list = Arrays.asList("item1", "item2", "item3");

            // when
            String result = ToStringUtil.toString(list);

            // then
            assertThat(result).isNotNull();
            assertThat(result).contains("item1");
            assertThat(result).contains("item2");
            assertThat(result).contains("item3");
        }

        @Test
        @DisplayName("[success] Map 객체를 문자열로 변환한다")
        void success_mapObject() {
            // given
            Map<String, Object> map = new HashMap<>();
            map.put("key1", "value1");
            map.put("key2", 123);
            map.put("key3", true);

            // when
            String result = ToStringUtil.toString(map);

            // then
            assertThat(result).isNotNull();
            assertThat(result).contains("key1");
            assertThat(result).contains("value1");
            assertThat(result).contains("key2");
        }

        @Test
        @DisplayName("[success] 중첩된 객체를 문자열로 변환한다")
        void success_nestedObject() {
            // given
            Map<String, Object> nestedMap = new HashMap<>();
            nestedMap.put("user", TestObject.builder()
                .name("홍길동")
                .age(30)
                .email("test@example.com")
                .build());

            // when
            String result = ToStringUtil.toString(nestedMap);

            // then
            assertThat(result).isNotNull();
            assertThat(result).contains("user");
            assertThat(result).contains("홍길동");
        }

        @Test
        @DisplayName("[success] LocalDateTime을 포함한 객체를 문자열로 변환한다")
        void success_withLocalDateTime() {
            // given
            TestObjectWithDateTime obj = TestObjectWithDateTime.builder()
                .name("test")
                .createdAt(LocalDateTime.of(2024, 1, 1, 10, 0))
                .build();

            // when
            String result = ToStringUtil.toString(obj);

            // then
            assertThat(result).isNotNull();
            assertThat(result).contains("\"name\":\"test\"");
            assertThat(result).contains("2024-01-01");
        }

        @Test
        @DisplayName("[success] 배열을 문자열로 변환한다")
        void success_arrayObject() {
            // given
            String[] array = {"apple", "banana", "cherry"};

            // when
            String result = ToStringUtil.toString(array);

            // then
            assertThat(result).isNotNull();
            assertThat(result).contains("apple");
            assertThat(result).contains("banana");
            assertThat(result).contains("cherry");
        }

        @Test
        @DisplayName("[success] 복잡한 객체 구조를 문자열로 변환한다")
        void success_complexObject() {
            // given
            Map<String, Object> complex = new HashMap<>();
            complex.put("users", Arrays.asList(
                TestObject.builder().name("User1").age(25).build(),
                TestObject.builder().name("User2").age(30).build()
            ));
            complex.put("metadata", Map.of("count", 2, "page", 1));

            // when
            String result = ToStringUtil.toString(complex);

            // then
            assertThat(result).isNotNull();
            assertThat(result).contains("users");
            assertThat(result).contains("User1");
            assertThat(result).contains("User2");
            assertThat(result).contains("metadata");
        }

        @Test
        @DisplayName("[success] null 필드를 포함한 객체를 문자열로 변환한다")
        void success_objectWithNullFields() {
            // given
            TestObject obj = TestObject.builder()
                .name("홍길동")
                .age(30)
                .build();

            // when
            String result = ToStringUtil.toString(obj);

            // then
            assertThat(result).isNotNull();
            assertThat(result).contains("\"name\":\"홍길동\"");
            assertThat(result).contains("\"age\":30");
        }
    }
}
