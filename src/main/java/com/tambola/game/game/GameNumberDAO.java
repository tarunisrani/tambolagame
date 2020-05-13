package com.tambola.game.game;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tambola.game.utils.JDBCTemplateWrapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class GameNumberDAO {

  public final JDBCTemplateWrapper jdbcTemplateWrapper;

  //COLUMN NAMES
  private final String COL_GAME_ID = "game_id";
  private final String COL_NUMBERS = "numbers";
  private final String STAR =
      String.join(
          ",",
          COL_GAME_ID,
          COL_NUMBERS);


  //SQL STATEMENTS
  private final String QUERY_ADD_NUMBER =
    String.format(
        "insert into game_numbers(%1$s, %2$s) values(:%1$s, :%2$s::jsonb) on conflict(%1$s) do update set %2$s=:%2$s::jsonb;",
        COL_GAME_ID,
        COL_NUMBERS);

  private final String SEARCH_QUERY =
      String.format("select " + STAR + " from game_numbers where %1$s=:%1$s", COL_GAME_ID);

  @Autowired
  public GameNumberDAO(JDBCTemplateWrapper jdbcTemplateWrapper) {
    this.jdbcTemplateWrapper = jdbcTemplateWrapper;
  }

  public Integer addNumber(Integer gameID, Map<Integer, Integer> numbers) {
    Map<String, Object> params = new HashMap<>();
    params.put(COL_GAME_ID, gameID);
    params.put(COL_NUMBERS, new Gson().toJson(numbers));

    return jdbcTemplateWrapper
        .update(QUERY_ADD_NUMBER, params);
  }

  public Map<Integer, Integer> getNumbers(Integer gameID){
    Map<String, Object> params = new HashMap<>();
    params.put(COL_GAME_ID, gameID);

    try {
      return jdbcTemplateWrapper
          .querySingleRow(SEARCH_QUERY, new RowMapper<Map<Integer, Integer>>() {
            @Override
            public Map<Integer, Integer> mapRow(ResultSet resultSet, int i) throws SQLException {
              return new Gson()
                  .fromJson(resultSet.getString(COL_NUMBERS), new TypeToken<Map<Integer, Integer>>() {
                  }.getType());
            }
          }, params);
    }catch (EmptyResultDataAccessException exp){
      return new HashMap<>();
    }
  }
}
