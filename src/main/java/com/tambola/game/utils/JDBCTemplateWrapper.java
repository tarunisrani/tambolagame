package com.tambola.game.utils;

import static com.tambola.game.utils.TryCatchUtils.tryCatch;
import static java.util.concurrent.CompletableFuture.supplyAsync;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.sql.DataSource;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

@Service
public class JDBCTemplateWrapper {
  private final NamedParameterJdbcTemplate jdbcTemplate;
  @Getter
  private final ExecutorService executorService;

  @Autowired
  public JDBCTemplateWrapper(DataSource dataSource, @Value("${db.noOfThreads}") int noOfThreads) {
    jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    this.executorService = Executors.newFixedThreadPool(noOfThreads);
  }

  public CompletionStage<Integer> updateWithGeneratedKeysAsync(
      String sql, Map<String, ?> paramsMap, KeyHolder keyHolder) {
    SqlParameterSource paramsSource = new MapSqlParameterSource(paramsMap);
    return supplyAsync(() -> jdbcTemplate.update(sql, paramsSource, keyHolder), executorService);
  }

  public Integer updateWithGeneratedKeys(
      String sql, Map<String, ?> paramsMap, KeyHolder keyHolder) {
    SqlParameterSource paramsSource = new MapSqlParameterSource(paramsMap);
    return jdbcTemplate.update(sql, paramsSource, keyHolder);
  }

  /*public CompletionStage<Integer> updateWithGeneratedKeysWithColumns(
      String sql, Map<String, ?> paramsMap, KeyHolder keyHolder, String columns[]) {
    SqlParameterSource paramsSource = new MapSqlParameterSource(paramsMap);
    return supplyAsync(
        () -> jdbcTemplate.update(sql, paramsSource, keyHolder, columns), executorService);
  }*/

  public Integer updateWithGeneratedKeysWithColumns(
      String sql, Map<String, ?> paramsMap, KeyHolder keyHolder, String columns[]) {
    SqlParameterSource paramsSource = new MapSqlParameterSource(paramsMap);
    return jdbcTemplate.update(sql, paramsSource, keyHolder, columns);
  }

  public CompletionStage<Integer> updateAsync(String sql, Map<String, ?> paramsMap) {
    return supplyAsync(() -> update(sql, paramsMap), executorService);
  }

  public Integer update(String sql, Map<String, ?> paramsMap) {
    return jdbcTemplate.update(sql, paramsMap);
  }

  public <T> CompletionStage<List<T>> queryAsync(String sql, RowMapper<T> rm,
      Map<String, ?> paramsMap) {
    return supplyAsync(() -> query(sql, rm, paramsMap), executorService);
  }

  public <T> List<T> query(String sql, RowMapper<T> rm, Map<String, ?> paramsMap) {
    return jdbcTemplate.query(sql, paramsMap, rm);
  }

  /*public <T> CompletionStage<Optional<T>> queryOptionalRowAsync(
      String sql, RowMapper<T> rm, Map<String, ?> paramsMap) {
    return querySingleRowAsync(sql, rm, paramsMap)
        .thenApply(Optional::of)
        .exceptionally(
            e -> {
              e = CompletableFutures.unwrapCompletionStateException(e);
              if (e instanceof EmptyResultDataAccessException) {
                return Optional.empty();
              }
              throw new CompletionException(e);
            });
  }*/

  public <T> Optional<T> queryOptionalRow(
      String sql, RowMapper<T> rm, Map<String, ?> paramsMap) {
    return tryCatch(() -> Optional.ofNullable(querySingleRow(sql, rm, paramsMap)),
        e -> Optional.empty(), ImmutableList.of(EmptyResultDataAccessException.class));
  }

  /*public <T> CompletionStage<Optional<T>> queryAnyRowAsync(
      String sql, RowMapper<T> rm, Map<String, ?> paramsMap) {
    return queryAsync(sql, rm, paramsMap)
        .thenApply(
            rows -> {
              return rows.stream().findFirst();
            })
        .exceptionally(
            e -> {
              e = CompletableFutures.unwrapCompletionStateException(e);
              if (e instanceof EmptyResultDataAccessException) {
                return Optional.empty();
              }
              throw new CompletionException(e);
            });
  }*/

  public <T> Optional<T> queryAnyRow(
      String sql, RowMapper<T> rm, Map<String, ?> paramsMap) {
    return query(sql, rm, paramsMap).stream().findFirst();
  }


  public <T> CompletionStage<T> querySingleRowAsync(
      String sql, RowMapper<T> rm, Map<String, ?> paramsMap) {
    return supplyAsync(() -> querySingleRow(sql, rm, paramsMap), executorService);
  }

  public <T> T querySingleRow(
      String sql, RowMapper<T> rm, Map<String, ?> paramsMap) {
    return jdbcTemplate.queryForObject(sql, paramsMap, rm);
  }

  public CompletionStage<int[]> batchUpdateAsync(String sql, SqlParameterSource[] batchArgs) {
    return supplyAsync(() -> batchUpdate(sql, batchArgs), executorService);
  }

  public int[] batchUpdate(String sql, SqlParameterSource[] batchArgs) {
    return jdbcTemplate.batchUpdate(sql, batchArgs);
  }
}
