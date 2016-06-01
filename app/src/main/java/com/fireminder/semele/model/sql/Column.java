package com.fireminder.semele.model.sql;

public class Column {
  public enum Type {
    BOOLEAN("integer"),
    INTEGER("integer"),
    REAL("real"),
    TEXT("text"),
    BLOB("blob"),
    DATE("datetime");

    public final String type;

    Type(String type) {
      this.type = type;
    }
  }

  public Column(String name, Type type) {
    this(name, type, null, false, false);
  }

  public Column(String name, Type type, Object defaultValue) {
    this(name, type, defaultValue, false, false);
  }

  public Column(String name, Type type, Object defaultValue, boolean isPrimaryKey, boolean isUnique) {
    this.name = name;
    this.type = type;
    this.defaultValue = defaultValue;
    this.isPrimaryKey = isPrimaryKey;
    this.isUnique = isUnique;
  }

  public String getCreateCommand() {
    StringBuilder builder = new StringBuilder();
    builder.append(this.name)
        .append(" ")
        .append(type.type);

    if (isPrimaryKey) {
      builder.append(" PRIMARY KEY AUTOINCREMENT");
    }

    if (isUnique) {
      builder.append(" UNIQUE");
    }

    if (defaultValue != null) {
      builder.append(" DEFAULT ")
          .append(defaultValue);
    }

    return builder.toString();
  }

  public final String name;
  public final Type type;
  public final Object defaultValue;
  public final boolean isPrimaryKey;
  public final boolean isUnique;

}
