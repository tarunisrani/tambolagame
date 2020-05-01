package com.tambola.game.game;

import com.tambola.game.Game;
import com.tambola.game.utils.JDBCTemplateWrapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class GameDAO {

  public final JDBCTemplateWrapper jdbcTemplateWrapper;

  //COLUMN NAMES
  private final String COL_GAME_ID = "game_id";
  private final String COL_OWNER_ID = "owner_id";
  private final String COL_NOTIFICATION_KEY = "notification_key";
  private final String COL_STATUS = "status";
  private final String STAR =
      String.join(
          ",",
          COL_GAME_ID,
          COL_OWNER_ID,
          COL_NOTIFICATION_KEY);


  //SQL STATEMENTS
  private final String QUERY_ADD_GAME =
    String.format(
        "insert into game_details(%1$s, %2$s, %3$s) values(:%1$s, :%2$s, :%3$s)",
        COL_GAME_ID,
        COL_OWNER_ID,
        COL_STATUS);

  private final String QUERY_UPDATE_GAME =
    String.format(
        "update game_details set %1$s=:%1$s WHERE %2$s=:%2$s",
        COL_STATUS,
        COL_GAME_ID);

  private final String UPDATE_NOTIFICATION_KEY =
      String.format(
          "UPDATE game_details SET %1$s=:%1$s WHERE %2$s=:%2$s",
          COL_NOTIFICATION_KEY,
          COL_GAME_ID);

  private final String SEARCH_QUERY =
      String.format("select * from game_details where %1$s=:%1$s", COL_GAME_ID);

  private final String GET_NOTIFICATION_KEY_FOR_GAME =
      String.format("select %1$s from game_details where %2$s=:%2$s", COL_NOTIFICATION_KEY, COL_GAME_ID);

  private final String GET_GAME_BY_ID =
      String.format("select * from game_details where %1$s=:%1$s", COL_GAME_ID);

  @Autowired
  public GameDAO(JDBCTemplateWrapper jdbcTemplateWrapper) {
    this.jdbcTemplateWrapper = jdbcTemplateWrapper;
  }

  public Integer addGame(Integer gameID, Integer ownerID) {
    Map<String, Object> params = new HashMap<>();
    params.put(COL_GAME_ID, gameID);
    params.put(COL_OWNER_ID, ownerID);
    params.put(COL_STATUS, "ACTIVE");

    return jdbcTemplateWrapper
        .update(QUERY_ADD_GAME, params);
  }

  public Integer updateGameStatus(Integer gameID, String status) {
    Map<String, Object> params = new HashMap<>();
    params.put(COL_GAME_ID, gameID);
    params.put(COL_STATUS, status);

    return jdbcTemplateWrapper
        .update(QUERY_UPDATE_GAME, params);
  }

  public List<Integer> getGameIds(){
    Map<String, Object> params = new HashMap<>();
    return jdbcTemplateWrapper
        .query("SELECT game_id from game_details", new RowMapper<Integer>() {
          @Override
          public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
            return resultSet.getInt(COL_GAME_ID);
          }
        }, params);
  }

  public Integer updateNotificationKey(Integer gameID, String notificationKey) {
    Map<String, Object> params = new HashMap<>();
    params.put(COL_GAME_ID, gameID);
    params.put(COL_NOTIFICATION_KEY, notificationKey);

    return jdbcTemplateWrapper
        .update(UPDATE_NOTIFICATION_KEY, params);
  }

  public Optional<Game> getGameByID(Integer gameID){
    Map<String, Object> params = new HashMap<>();
    params.put(COL_GAME_ID, gameID);

    try {
      return jdbcTemplateWrapper
          .queryAnyRow(GET_GAME_BY_ID, new RowMapper<Game>() {
            @Override
            public Game mapRow(ResultSet resultSet, int i) throws SQLException {
              return Game.builder()
                  .notificationKey(resultSet.getString(COL_NOTIFICATION_KEY))
                  .gameID(resultSet.getLong(COL_GAME_ID))
                  .ownerID(resultSet.getLong(COL_OWNER_ID))
                  .status(resultSet.getString(COL_STATUS))
                  .build();
            }
          }, params);
    }catch (EmptyResultDataAccessException exp){
      return Optional.empty();
    }
  }

  public Optional<String> getGameNotificationKey(Integer gameID){
    Map<String, Object> params = new HashMap<>();
    params.put(COL_GAME_ID, gameID);

    try {
      return jdbcTemplateWrapper
          .queryAnyRow(GET_NOTIFICATION_KEY_FOR_GAME, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet resultSet, int i) throws SQLException {
              return resultSet.getString(COL_NOTIFICATION_KEY);
            }
          }, params);
    }catch (EmptyResultDataAccessException exp){
      return Optional.empty();
    }
  }
}
