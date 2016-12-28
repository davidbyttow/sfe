package com.davidbyttow.sfe.storage.entity;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.davidbyttow.sfe.common.MoreReflections;
import com.davidbyttow.sfe.storage.entity.index.CompositeIndexDef;
import com.davidbyttow.sfe.storage.entity.index.CompositeIndexes;
import com.davidbyttow.sfe.storage.entity.index.EntityCompositeIndex;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityMetadata {

  private static final EntityHandler<?> DEFAULT_HANDLER = new AbstractEntityHandler<Object>() {};

  private final Class<?> type;
  private final String kind;
  private final EntityIdRef entityIdRef;
  private final EntityHandler<?> handler;
  private final List<EntityPropField> indexedFields;
  private final List<EntityPropField> unindexedFields;
  private final List<EntityPropField> allFields;
  private final List<EntityCompositeIndex> compositeIndexes;
  private final Map<String, EntityPropField> indexedFieldMap;
  private final Map<String, EntityPropField> unindexedFieldMap;
  private final Map<String, EntityCompositeIndex> compositeIndexMap;

  private EntityMetadata(Class<?> type,
                         String kind,
                         EntityIdRef entityIdRef,
                         List<EntityPropField> indexedFields,
                         List<EntityPropField> unindexedFields,
                         EntityHandler<?> handler,
                         List<EntityCompositeIndex> compositeIndexes) {
    this.type = type;
    this.kind = kind;
    this.entityIdRef = entityIdRef;
    this.handler = handler;
    this.indexedFields = ImmutableList.copyOf(indexedFields);
    this.unindexedFields = ImmutableList.copyOf(unindexedFields);
    this.compositeIndexes = compositeIndexes;
    this.allFields = ImmutableList.<EntityPropField>builder().addAll(indexedFields).addAll(unindexedFields).build();
    this.indexedFieldMap = ImmutableMap.copyOf(Maps.uniqueIndex(indexedFields, EntityPropField::getName));
    this.unindexedFieldMap = ImmutableMap.copyOf(Maps.uniqueIndex(unindexedFields, EntityPropField::getName));
    this.compositeIndexMap = ImmutableMap.copyOf(Maps.uniqueIndex(compositeIndexes,
      index -> CompositeIndexes.getFieldNamesAsSortedString(index.getFieldNames())));
  }

  public static EntityMetadata fromType(Class<?> type, List<CompositeIndexDef> compositeIndexDefs) {
    if (type.getSuperclass() != Object.class) {
      throw new IllegalArgumentException("Entities may only derive from Object");
    }

    EntityKind kindAnn = type.getAnnotation(EntityKind.class);
    if (kindAnn == null) {
      throw new IllegalArgumentException("Object does not have a declared kind");
    }

    EntityHandler<?> handler = DEFAULT_HANDLER;

    HandleWith handleWith = type.getAnnotation(HandleWith.class);
    if (handleWith != null) {
      try {
        handler = MoreReflections.forceNewInstance(handleWith.value());
      } catch (Exception e) {
        throw Throwables.propagate(e);
      }
    }

    EntityIdRef idRef = null;
    Map<String, EntityPropField> indexedPropMap = new HashMap<>();
    ImmutableList.Builder<EntityPropField> indexedBuilder = ImmutableList.builder();
    ImmutableList.Builder<EntityPropField> unindexedBuilder = ImmutableList.builder();
    for (Field field : type.getDeclaredFields()) {
      if (field.isAnnotationPresent(Ignored.class)) {
        continue;
      }

      if (field.isAnnotationPresent(EntityId.class)) {
        if (idRef != null) {
          throw new IllegalArgumentException("Object can not have multiple id annotations");
        }
        idRef = new EntityIdRef.FieldEntityIdRef(field);
        continue;
      }

      boolean indexed = field.isAnnotationPresent(Indexed.class);
      EntityPropField propField = EntityPropField.fromField(field, indexed);
      if (!indexed) {
        unindexedBuilder.add(propField);
      } else {
        indexedBuilder.add(propField);
        indexedPropMap.put(propField.getName(), propField);
      }
    }

    List<EntityCompositeIndex> compositeIndexes = new ArrayList<>();
    for (CompositeIndexDef def : compositeIndexDefs) {
      if (def.getPropertyNames().size() < 2) {
        throw new IllegalArgumentException("Need more than 1 prop for entity composite index");
      }
      List<EntityPropField> props = new ArrayList<>(def.getPropertyNames().size());
      for (String propName : def.getPropertyNames()) {
        EntityPropField field = indexedPropMap.get(propName);
        if (field == null) {
          throw new IllegalArgumentException("Did not find indexed property with name " + propName);
        }
        props.add(field);
      }
      compositeIndexes.add(new EntityCompositeIndex(props));
    }

    for (Method method : type.getDeclaredMethods()) {
      if (method.isAnnotationPresent(EntityId.class)) {
        if (idRef != null) {
          throw new IllegalArgumentException("Object can not have multiple id annotations");
        }
        idRef = new EntityIdRef.MethodEntityIdRef(method);
      }
    }

    if (idRef == null) {
      throw new IllegalArgumentException("Missing id for entity");
    }

    return new EntityMetadata(
      type, kindAnn.value(), idRef, indexedBuilder.build(), unindexedBuilder.build(), handler, compositeIndexes);
  }

  public Class<?> getType() {
    return type;
  }

  public String getKind() {
    return kind;
  }

  public EntityIdRef getIdRef() {
    return entityIdRef;
  }

  public EntityHandler<?> getHandler() {
    return handler;
  }

  public List<EntityPropField> getAllFields() {
    return this.allFields;
  }

  public List<EntityPropField> getIndexedFields() {
    return indexedFields;
  }

  public List<EntityPropField> getUnindexedFields() {
    return unindexedFields;
  }

  public Map<String, EntityPropField> getIndexedFieldMap() {
    return indexedFieldMap;
  }

  public Map<String, EntityPropField> getUnindexedFieldMap() {
    return unindexedFieldMap;
  }

  public List<EntityCompositeIndex> getCompositeIndexes() {
    return compositeIndexes;
  }

  public Map<String, EntityCompositeIndex> getCompositeIndexMap() {
    return compositeIndexMap;
  }
}
