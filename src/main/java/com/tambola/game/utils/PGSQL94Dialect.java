package com.tambola.game.utils;

import java.sql.Types;
import org.hibernate.dialect.PostgreSQL94Dialect;

public class PGSQL94Dialect extends PostgreSQL94Dialect {

  public PGSQL94Dialect() {
    this.registerColumnType(Types.JAVA_OBJECT, "jsonb");
  }
}