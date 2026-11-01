package com.base.infrastructure.utils;

import java.time.Instant;

import com.base.infrastructure.jpa.entity.BaseCrudJpaEntity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

import lombok.Getter;
import lombok.Setter;

@ExtendWith(MockitoExtension.class)
class EntityUtilsTest implements EntityUtils {

    @Test
    void test() {
        var now = Instant.now();

        Entity entity1 = new Entity();
        entity1.name = "name";
        entity1.username = "username";

        Entity entity2 = new Entity();
        entity2.name = "name2";
        entity2.username = "username";
        entity2.myDate = now;

        setEntity(entity1, entity2, true);

        Assertions.assertEquals("name2", entity1.name);
        Assertions.assertEquals("username", entity1.username);
        Assertions.assertEquals(now, entity1.myDate);

        entity2.myDate = null;
        setEntity(entity1, entity2, true);
        Assertions.assertEquals(null, entity1.myDate);
    }

    @Test
    void Given_EntityUtilsTest_When_isObjectDiff_Then_return_correcValue() {
        Assertions.assertEquals(false, isObjectDiff(null, null));
        Assertions.assertEquals(true, isObjectDiff(null, "test"));
        Assertions.assertEquals(false, isObjectDiff("test", "test"));
        Assertions.assertEquals(true, isObjectDiff("test", "test2"));
    }

    @Getter
    @Setter
    class Entity extends BaseCrudJpaEntity {
        public String name;
        public String username;
        public Instant myDate;
    }

    @Getter
    @Setter
    class Entity2 extends BaseCrudJpaEntity {
        public String name;
    }

    @Test
    void testSetFields_NoSuchMethodException() {
        var entity1 = new Entity();
        entity1.setId(-1L);
        entity1.username = "username";
        var entity2 = new Entity2();

        setEntity(entity1, entity2, true);

        assertEquals(entity1.name, entity2.name);
    }
}
