package com.odcloud.adapter.out.persistence.redis;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.odcloud.IntegrationTestSupport;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

class RedisStorageAdapterTest extends IntegrationTestSupport {

    @Autowired
    RedisStorageAdapter adapter;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @AfterEach
    void tearDown() {
        redisTemplate.delete("test-key");
        redisTemplate.delete("test-key-with-ttl");
        redisTemplate.delete("user-key");
        redisTemplate.delete("list-key");
        redisTemplate.delete("string-key");
        redisTemplate.delete("object-key");
        redisTemplate.delete("null-key");
        redisTemplate.delete("key-1");
        redisTemplate.delete("key-2");
        redisTemplate.delete("key-3");
    }

    @Nested
    @DisplayName("[register(key, data)] TTL 없이 데이터를 저장하는 메소드")
    class Describe_register {

        @Test
        @DisplayName("[success] 문자열 데이터를 저장한다")
        void success_string() {
            // given
            String key = "test-key";
            String data = "test-value";

            // when
            adapter.register(key, data);

            // then
            String savedData = redisTemplate.opsForValue().get(key);
            assertThat(savedData).isEqualTo(data);
        }

        @Test
        @DisplayName("[success] JSON 문자열을 저장한다")
        void success_json() throws Exception {
            // given
            String key = "user-key";
            TestUser user = new TestUser("test@example.com", "홍길동");
            String data = objectMapper.writeValueAsString(user);

            // when
            adapter.register(key, data);

            // then
            String savedData = redisTemplate.opsForValue().get(key);
            assertThat(savedData).isEqualTo(data);

            TestUser savedUser = objectMapper.readValue(savedData, TestUser.class);
            assertThat(savedUser.email).isEqualTo("test@example.com");
            assertThat(savedUser.name).isEqualTo("홍길동");
        }

        @Test
        @DisplayName("[success] 기존 키의 값을 덮어쓴다")
        void success_overwrite() {
            // given
            String key = "test-key";
            adapter.register(key, "old-value");

            // when
            adapter.register(key, "new-value");

            // then
            String savedData = redisTemplate.opsForValue().get(key);
            assertThat(savedData).isEqualTo("new-value");
        }

        @Test
        @DisplayName("[success] 여러 키에 데이터를 저장한다")
        void success_multipleKeys() {
            // given & when
            adapter.register("key-1", "value-1");
            adapter.register("key-2", "value-2");
            adapter.register("key-3", "value-3");

            // then
            assertThat(redisTemplate.opsForValue().get("key-1")).isEqualTo("value-1");
            assertThat(redisTemplate.opsForValue().get("key-2")).isEqualTo("value-2");
            assertThat(redisTemplate.opsForValue().get("key-3")).isEqualTo("value-3");
        }

        @Test
        @DisplayName("[success] 빈 문자열을 저장한다")
        void success_emptyString() {
            // given
            String key = "test-key";
            String data = "";

            // when
            adapter.register(key, data);

            // then
            String savedData = redisTemplate.opsForValue().get(key);
            assertThat(savedData).isEqualTo("");
        }
    }

    @Nested
    @DisplayName("[register(key, data, ttl)] TTL과 함께 데이터를 저장하는 메소드")
    class Describe_registerWithTtl {

        @Test
        @DisplayName("[success] TTL과 함께 데이터를 저장한다")
        void success() {
            // given
            String key = "test-key-with-ttl";
            String data = "test-value";
            long ttl = 60000; // 60초

            // when
            adapter.register(key, data, ttl);

            // then
            String savedData = redisTemplate.opsForValue().get(key);
            assertThat(savedData).isEqualTo(data);

            // TTL 확인
            Long remainingTtl = redisTemplate.getExpire(key);
            assertThat(remainingTtl).isNotNull();
            assertThat(remainingTtl).isGreaterThan(0);
            assertThat(remainingTtl).isLessThanOrEqualTo(60);
        }

        @Test
        @DisplayName("[success] 짧은 TTL로 데이터를 저장한다")
        void success_shortTtl() throws InterruptedException {
            // given
            String key = "test-key-with-ttl";
            String data = "test-value";
            long ttl = 100; // 100ms

            // when
            adapter.register(key, data, ttl);

            // then
            String savedData = redisTemplate.opsForValue().get(key);
            assertThat(savedData).isEqualTo(data);

            // TTL 만료 대기
            Thread.sleep(150);

            String expiredData = redisTemplate.opsForValue().get(key);
            assertThat(expiredData).isNull();
        }

        @Test
        @DisplayName("[success] JSON 데이터를 TTL과 함께 저장한다")
        void success_jsonWithTtl() throws Exception {
            // given
            String key = "user-key";
            TestUser user = new TestUser("test@example.com", "홍길동");
            String data = objectMapper.writeValueAsString(user);
            long ttl = 60000;

            // when
            adapter.register(key, data, ttl);

            // then
            String savedData = redisTemplate.opsForValue().get(key);
            assertThat(savedData).isEqualTo(data);

            Long remainingTtl = redisTemplate.getExpire(key);
            assertThat(remainingTtl).isGreaterThan(0);
        }

        @Test
        @DisplayName("[success] 기존 키를 TTL과 함께 덮어쓴다")
        void success_overwriteWithTtl() {
            // given
            String key = "test-key-with-ttl";
            adapter.register(key, "old-value", 60000);

            // when
            adapter.register(key, "new-value", 30000);

            // then
            String savedData = redisTemplate.opsForValue().get(key);
            assertThat(savedData).isEqualTo("new-value");

            Long remainingTtl = redisTemplate.getExpire(key);
            assertThat(remainingTtl).isLessThanOrEqualTo(30);
        }
    }

    @Nested
    @DisplayName("[findData] 단일 데이터를 조회하는 메소드")
    class Describe_findData {

        @Test
        @DisplayName("[success] 문자열 데이터를 조회한다")
        void success_string() {
            // given
            String key = "string-key";
            String data = "test-value";
            redisTemplate.opsForValue().set(key, data);

            // when
            String result = adapter.findData(key, String.class);

            // then
            assertThat(result).isEqualTo(data);
        }

        @Test
        @DisplayName("[success] 객체 데이터를 조회한다")
        void success_object() throws Exception {
            // given
            String key = "object-key";
            TestUser user = new TestUser("test@example.com", "홍길동");
            String data = objectMapper.writeValueAsString(user);
            redisTemplate.opsForValue().set(key, data);

            // when
            TestUser result = adapter.findData(key, TestUser.class);

            // then
            assertThat(result).isNotNull();
            assertThat(result.email).isEqualTo("test@example.com");
            assertThat(result.name).isEqualTo("홍길동");
        }

        @Test
        @DisplayName("[success] 존재하지 않는 키를 조회하면 null을 반환한다")
        void success_notFound() {
            // when
            String result = adapter.findData("non-existent-key", String.class);

            // then
            assertThat(result).isNull();
        }
        
        @Test
        @DisplayName("[success] 여러 키에서 각각 데이터를 조회한다")
        void success_multipleKeys() {
            // given
            redisTemplate.opsForValue().set("key-1", "value-1");
            redisTemplate.opsForValue().set("key-2", "value-2");
            redisTemplate.opsForValue().set("key-3", "value-3");

            // when
            String result1 = adapter.findData("key-1", String.class);
            String result2 = adapter.findData("key-2", String.class);
            String result3 = adapter.findData("key-3", String.class);

            // then
            assertThat(result1).isEqualTo("value-1");
            assertThat(result2).isEqualTo("value-2");
            assertThat(result3).isEqualTo("value-3");
        }
    }

    @Nested
    @DisplayName("[findDataList] 리스트 데이터를 조회하는 메소드")
    class Describe_findDataList {

        @Test
        @DisplayName("[success] 리스트 데이터를 조회한다")
        void success() throws Exception {
            // given
            String key = "list-key";
            List<TestUser> users = List.of(
                new TestUser("user1@example.com", "사용자1"),
                new TestUser("user2@example.com", "사용자2"),
                new TestUser("user3@example.com", "사용자3")
            );
            String data = objectMapper.writeValueAsString(users);
            redisTemplate.opsForValue().set(key, data);

            // when
            List<TestUser> result = adapter.findDataList(key, TestUser.class);

            // then
            assertThat(result).hasSize(3);
            assertThat(result.get(0).email).isEqualTo("user1@example.com");
            assertThat(result.get(1).email).isEqualTo("user2@example.com");
            assertThat(result.get(2).email).isEqualTo("user3@example.com");
        }

        @Test
        @DisplayName("[success] 빈 리스트를 조회한다")
        void success_emptyList() throws Exception {
            // given
            String key = "list-key";
            String data = objectMapper.writeValueAsString(List.of());
            redisTemplate.opsForValue().set(key, data);

            // when
            List<TestUser> result = adapter.findDataList(key, TestUser.class);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("[success] 존재하지 않는 키를 조회하면 빈 리스트를 반환한다")
        void success_notFound() {
            // when
            List<TestUser> result = adapter.findDataList("non-existent-key", TestUser.class);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("[success] 문자열 리스트를 조회한다")
        void success_stringList() throws Exception {
            // given
            String key = "list-key";
            List<String> strings = List.of("value1", "value2", "value3");
            String data = objectMapper.writeValueAsString(strings);
            redisTemplate.opsForValue().set(key, data);

            // when
            List<String> result = adapter.findDataList(key, String.class);

            // then
            assertThat(result).hasSize(3);
            assertThat(result).containsExactly("value1", "value2", "value3");
        }

        @Test
        @DisplayName("[success] 단일 요소 리스트를 조회한다")
        void success_singleElementList() throws Exception {
            // given
            String key = "list-key";
            List<TestUser> users = List.of(
                new TestUser("user@example.com", "사용자")
            );
            String data = objectMapper.writeValueAsString(users);
            redisTemplate.opsForValue().set(key, data);

            // when
            List<TestUser> result = adapter.findDataList(key, TestUser.class);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).email).isEqualTo("user@example.com");
            assertThat(result.get(0).name).isEqualTo("사용자");
        }

        @Test
        @DisplayName("[success] 많은 요소를 가진 리스트를 조회한다")
        void success_largeList() throws Exception {
            // given
            String key = "list-key";
            List<TestUser> users = new java.util.ArrayList<>();
            for (int i = 0; i < 100; i++) {
                users.add(new TestUser("user" + i + "@example.com", "사용자" + i));
            }
            String data = objectMapper.writeValueAsString(users);
            redisTemplate.opsForValue().set(key, data);

            // when
            List<TestUser> result = adapter.findDataList(key, TestUser.class);

            // then
            assertThat(result).hasSize(100);
            assertThat(result.get(0).email).isEqualTo("user0@example.com");
            assertThat(result.get(99).email).isEqualTo("user99@example.com");
        }
    }

    // 테스트용 클래스
    static class TestUser {

        public String email;
        public String name;

        public TestUser() {
        }

        public TestUser(String email, String name) {
            this.email = email;
            this.name = name;
        }
    }
}
