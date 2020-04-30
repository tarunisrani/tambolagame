package com.tambola.game.game;

import com.tambola.game.UserContext;
import com.tambola.game.utils.JDBCTemplateWrapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UserDAO {

  public final JDBCTemplateWrapper jdbcTemplateWrapper;

  //COLUMN NAMES
  private final String COL_USER_ID = "user_id";
  private final String COL_GAME_ID = "game_id";
  private final String COL_MOBILE_NUMBER = "mobile_number";
  private final String COL_NAME = "name";
  private final String COL_NOTIFICATION_KEY = "notification_key";



  //SQL STATEMENTS
  private final String QUERY_ADD_USER =
    String.format(
        "insert into user_details(%1$s, %2$s, %3$s) values(:%1$s, :%2$s, :%3$s) on conflict(%1$s) do update set %2$s=:%2$s, %3$s=:%3$s",
        COL_MOBILE_NUMBER,
        COL_NAME,
        COL_NOTIFICATION_KEY);

  private final String QUERY_SEARCH_BY_MOBILE_NUMBER =
      String.format("select * from user_details where %1$s=:%1$s", COL_MOBILE_NUMBER);

  private final String QUERY_GET_USERS_FOR_GAME_ID =
      String.format("select ud.user_id, ud.mobile_number, ud.name from game_ticket as gt join user_details ud on ud.mobile_number=gt.assigned_to where gt.%1$s=:%1$s",COL_GAME_ID);

  @Autowired
  public UserDAO(JDBCTemplateWrapper jdbcTemplateWrapper) {
    this.jdbcTemplateWrapper = jdbcTemplateWrapper;
  }

  public Integer addUser(UserContext context) {
    Map<String, Object> params = new HashMap<>();
    params.put(COL_NAME, context.getUserName());
    params.put(COL_MOBILE_NUMBER, context.getMobileNumber());
    params.put(COL_NOTIFICATION_KEY, context.getNotificationKey());

    return jdbcTemplateWrapper
        .insertAndGetKey(QUERY_ADD_USER, params, COL_USER_ID);
  }

  public List<UserContext> getUsersForGameID(Integer gameID){
    Map<String, Object> params = new HashMap<>();
    params.put(COL_GAME_ID, gameID);

    try {
      return jdbcTemplateWrapper
          .query(QUERY_GET_USERS_FOR_GAME_ID, new RowMapper<UserContext>() {
            @Override
            public UserContext mapRow(ResultSet resultSet, int i) throws SQLException {
              return UserContext.builder()
                  .userID(resultSet.getInt(COL_USER_ID))
                  .userName(resultSet.getString(COL_NAME))
                  .mobileNumber(resultSet.getString(COL_MOBILE_NUMBER))
                  .build();
            }
          }, params);
    }catch (EmptyResultDataAccessException exp){
      return Collections.EMPTY_LIST;
    }
  }

  public Optional<UserContext> getUserByMob(String mobileNumber){
    Map<String, Object> params = new HashMap<>();
    params.put(COL_MOBILE_NUMBER, mobileNumber);

    try {
      return jdbcTemplateWrapper
          .queryAnyRow(QUERY_SEARCH_BY_MOBILE_NUMBER, new RowMapper<UserContext>() {
            @Override
            public UserContext mapRow(ResultSet resultSet, int i) throws SQLException {
              return UserContext.builder()
                  .notificationKey(resultSet.getString(COL_NOTIFICATION_KEY))
                  .userID(resultSet.getInt(COL_USER_ID))
                  .userName(resultSet.getString(COL_NAME))
                  .build();
            }
          }, params);
    }catch (EmptyResultDataAccessException exp){
      return Optional.empty();
    }
  }

  public Optional<UserContext> getUserById(Integer userID){
    Map<String, Object> params = new HashMap<>();
    params.put(COL_USER_ID, userID);

    try {
      return jdbcTemplateWrapper
          .queryAnyRow(QUERY_SEARCH_BY_MOBILE_NUMBER, new RowMapper<UserContext>() {
            @Override
            public UserContext mapRow(ResultSet resultSet, int i) throws SQLException {
              return UserContext.builder()
                  .notificationKey(resultSet.getString(COL_NOTIFICATION_KEY))
                  .userID(resultSet.getInt(COL_USER_ID))
                  .userName(resultSet.getString(COL_NAME))
                  .build();
            }
          }, params);
    }catch (EmptyResultDataAccessException exp){
      return Optional.empty();
    }
  }
}
