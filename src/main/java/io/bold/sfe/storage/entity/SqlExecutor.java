package io.bold.sfe.storage.entity;

import com.google.inject.Inject;
import io.bold.sfe.storage.ForWrites;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import javax.inject.Provider;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SqlExecutor {
  private final Provider<DBI> dbiProvider;

  @Inject public SqlExecutor(@ForWrites Provider<DBI> dbiProvider) {
    this.dbiProvider = dbiProvider;
  }

  public void update(String sql) {
    DBI dbi = dbiProvider.get();
    try (Handle handle = dbi.open()) {
      for (String stmt : sql.split(";")) {
        if (!stmt.trim().isEmpty()) {
          handle.update(stmt);
        }
      }
    }
  }

  public void query(String sql, Consumer<Map<String, Object>> consumer) {
    DBI dbi = dbiProvider.get();
    try (Handle handle = dbi.open()) {
      org.skife.jdbi.v2.Query<Map<String, Object>> sqlQuery = handle.createQuery(sql);
      for (Map<String, Object> row : sqlQuery) {
        consumer.accept(row);
      }
    }
  }

  public List<Map<String, Object>> query(String sql) {
    List<Map<String, Object>> rows = new ArrayList<>();
    query(sql, rows::add);
    return rows;
  }
}
