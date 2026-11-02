package com.base.infrastructure.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import com.base.infrastructure.jpa.entity.BaseCrudJpaEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public interface EntityUtils {
    Logger log = LoggerFactory.getLogger(EntityUtils.class);

    default void setEntity(Object entity1, Object entity2, boolean forceNull) {
        setEntity(entity1, entity2, entity1.getClass(), forceNull);
    }

    default void setEntity(
        Object entity1,
        Object entity2,
        Class<?> entityClass,
        boolean forceNull) {
        setFields(entity1, entity2, entityClass.getDeclaredFields(), forceNull);
        if (entityClass.getSuperclass() != null) {
            setEntity(entity1, entity2, entityClass.getSuperclass(), forceNull);
        }
    }

    private void setFields(
        Object entity1,
        Object entity2,
        Field[] fields,
        boolean forceNull) {
        for (var field : fields) {
            if (!field.getName().contains("_") && !field.getName().contains("$")
                && !"id".equals(field.getName())
                && !"serialVersionUID".equals(field.getName())
                && !"quarkusSyntheticLogger".equals(field.getName())) {
                Object value = null;
                var getterName = "get" + StringUtils.capitalize(field.getName());
                try {
                    value = entity2.getClass().getMethod(getterName).invoke(entity2);
                } catch (IllegalAccessException | InvocationTargetException
                    | NoSuchMethodException _) {
                    log.error("expcetion EntityUtils.setFields : {}", getterName);
                }
                doSetValue(entity1, value, field, forceNull);
            }
        }
    }

    private void doSetValue(
        Object entity,
        Object value,
        Field field,
        boolean forceNull) {
        var setterName = "set" + StringUtils.capitalize(field.getName());
        try {
            var newValue = value;
            log.debug("setValue {} : {}", setterName, newValue);
            if (newValue != null || forceNull) {
                if (value instanceof BaseCrudJpaEntity baseEntity
                    && baseEntity.getId().equals(-1L)) {
                    newValue = null;
                }
                entity.getClass().getMethod(setterName, (field.getType())).invoke(entity,
                    newValue);
            }
        } catch (IllegalAccessException | InvocationTargetException
            | NoSuchMethodException _) {
            log.error("expcetion EntityUtils.doSetValue : {}", setterName);
        }
    }

    default boolean isObjectDiff(Object first, Object second) {
        if (first == null && second == null) {
            return false;
        } else if (first == null) {
            return true;
        }
        return !first.equals(second);
    }
}