package com.tambola.game.game;

import com.tambola.game.GamePrize;
import com.tambola.game.utils.JDBCTemplateWrapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class GamePrizeDAO {

  public final JDBCTemplateWrapper jdbcTemplateWrapper;

  //COLUMN NAMES
  private final String COL_GAME_ID = "game_id";
  private final String COL_PRIZE_NAME = "prize_name";
  private final String COL_PRIZE_AMOUNT = "prize_amount";
  private final String COL_PRIZE_WINNER = "prize_winner";

  //SQL STATEMENTS
  private final String QUERY_ADD_GAME_PRIZE =
    String.format(
        "insert into game_prizes(%1$s, %2$s, %3$s) values(:%1$s, :%2$s, :%3$s) on conflict(%1$s, %2$s) do update set %3$s=:%3$s",
        COL_GAME_ID,
        COL_PRIZE_NAME,
        COL_PRIZE_AMOUNT);

  private final String QUERY_UPDATE_GAME_PRIZE =
    String.format(
        "update game_prizes set %1$s=:%1$s WHERE %2$s=:%2$s AND %3$s=:%3$s",
        COL_PRIZE_WINNER,
        COL_GAME_ID,
        COL_PRIZE_NAME);

  private final String SEARCH_QUERY_FOR_PRIZE_LIST =
      String.format("select * from game_prizes where %1$s=:%1$s", COL_GAME_ID);

  private final String SEARCH_QUERY_FOR_PRIZE_LIST_BY_PRIZE_NAME =
      String.format("select * from game_prizes where %1$s=:%1$s AND %2$s=:%2$s", COL_GAME_ID, COL_PRIZE_NAME);

  @Autowired
  public GamePrizeDAO(JDBCTemplateWrapper jdbcTemplateWrapper) {
    this.jdbcTemplateWrapper = jdbcTemplateWrapper;
  }

  public Integer addGamePrize(Integer gameID, String prizeName, Integer amount) {
    Map<String, Object> params = new HashMap<>();
    params.put(COL_GAME_ID, gameID);
    params.put(COL_PRIZE_NAME, prizeName);
    params.put(COL_PRIZE_AMOUNT, amount);

    return jdbcTemplateWrapper
        .update(QUERY_ADD_GAME_PRIZE, params);
  }

  public Integer updatePrizeStatus(Integer gameID, String prizeName, String winnerName) {
    Map<String, Object> params = new HashMap<>();
    params.put(COL_GAME_ID, gameID);
    params.put(COL_PRIZE_NAME, prizeName);
    params.put(COL_PRIZE_WINNER, winnerName);

    return jdbcTemplateWrapper
        .update(QUERY_UPDATE_GAME_PRIZE, params);
  }

  public List<GamePrize> getPrizesByGameID(Integer gameID){
    Map<String, Object> params = new HashMap<>();
    params.put(COL_GAME_ID, gameID);
    return jdbcTemplateWrapper
        .query(SEARCH_QUERY_FOR_PRIZE_LIST, new RowMapper<GamePrize>() {
          @Override
          public GamePrize mapRow(ResultSet resultSet, int i) throws SQLException {
            return GamePrize.builder()
                .gameID(resultSet.getInt(COL_GAME_ID))
                .prizeAmount(resultSet.getLong(COL_PRIZE_AMOUNT))
                .prizeName(resultSet.getString(COL_PRIZE_NAME))
                .prizeWinner(resultSet.getString(COL_PRIZE_WINNER))
                .build();
          }
        }, params);
  }

  public GamePrize getPrizesByGameIDAndPrizeName(Integer gameID, String prizeName){
    Map<String, Object> params = new HashMap<>();
    params.put(COL_GAME_ID, gameID);
    params.put(COL_PRIZE_NAME, prizeName);
    return jdbcTemplateWrapper
        .querySingleRow(SEARCH_QUERY_FOR_PRIZE_LIST_BY_PRIZE_NAME, new RowMapper<GamePrize>() {
          @Override
          public GamePrize mapRow(ResultSet resultSet, int i) throws SQLException {
            return GamePrize.builder()
                .gameID(resultSet.getInt(COL_GAME_ID))
                .prizeAmount(resultSet.getLong(COL_PRIZE_AMOUNT))
                .prizeName(resultSet.getString(COL_PRIZE_NAME))
                .prizeWinner(resultSet.getString(COL_PRIZE_WINNER))
                .build();
          }
        }, params);
  }
}
