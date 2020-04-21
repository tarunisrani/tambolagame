package com.tambola.game;

import com.google.gson.Gson;
import com.tambola.game.ticketgenerator.model.TambolaTicketVO;
import com.tambola.game.utils.JDBCTemplateWrapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class GameTicketDAO {

  public final JDBCTemplateWrapper jdbcTemplateWrapper;

  //COLUMN NAMES
  private final String COL_GAME_ID = "game_id";
  private final String COL_TICKET = "ticket";
  private final String COL_TICKET_ID = "ticket_id";
  private final String COL_ASSIGNED_TO = "assigned_to";
  private final String STAR =
      String.join(
          ",",
          COL_GAME_ID,
          COL_TICKET,
          COL_ASSIGNED_TO);

  //SQL STATEMENTS
  private final String QUERY_ADD_TICKET =
    String.format(
        "insert into game_ticket (%1$s, %2$s, %3$s) values (:%1$s, :%2$s, :%3$s::jsonb)",
        COL_GAME_ID,
        COL_TICKET_ID,
        COL_TICKET);

  private final String QUERY_ASSIGN_TICKET =
    String.format(
        "UPDATE game_ticket SET %1$s=:%1$s WHERE %2$s=:%2$s AND %3$s=:%3$s",
        COL_ASSIGNED_TO,
        COL_GAME_ID,
        COL_TICKET_ID);

  private final String SEARCH_QUERY =
      String.format("select " + STAR + " from game_ticket where ");

  @Autowired
  public GameTicketDAO(JDBCTemplateWrapper jdbcTemplateWrapper) {
    this.jdbcTemplateWrapper = jdbcTemplateWrapper;
  }

  public Integer addTickets(Integer gameID, Integer ticketID, TambolaTicketVO ticket) {
    Map<String, Object> params = new HashMap<>();
    params.put(COL_GAME_ID, gameID);
    params.put(COL_TICKET_ID, ticketID);
    params.put(COL_TICKET, new Gson().toJson(ticket));

    return jdbcTemplateWrapper
        .update(QUERY_ADD_TICKET, params);
  }

  public Integer assignTicketToUser(Integer gameID, Integer ticketID, String assignTo) {
    Map<String, Object> params = new HashMap<>();
    params.put(COL_GAME_ID, gameID);
    params.put(COL_TICKET_ID, ticketID);
    params.put(COL_ASSIGNED_TO, assignTo);

    KeyHolder keyHolder = new GeneratedKeyHolder();
    return jdbcTemplateWrapper
        .updateWithGeneratedKeysWithColumns(QUERY_ASSIGN_TICKET, params, keyHolder,
            new String[]{COL_TICKET_ID});
  }

  public Optional<GameTicket> getTicketForByGameIDAndUser(Integer gameID, String assignTo){

    Map<String, Object> params = new HashMap<>();
    params.put(COL_GAME_ID, gameID);
    params.put(COL_ASSIGNED_TO, assignTo);

    return jdbcTemplateWrapper
        .queryAnyRow(String.format(
            "SELECT * FROM game_ticket WHERE %1$s=:%1$s AND %2$s=:%2$s",
            COL_GAME_ID, COL_ASSIGNED_TO), new RowMapper<GameTicket>() {
          @Override
          public GameTicket mapRow(ResultSet resultSet, int i) throws SQLException {
            return GameTicket.builder()
                .gameID(resultSet.getInt(COL_GAME_ID))
                .ticketID(resultSet.getInt(COL_TICKET_ID))
                .ticket(new Gson().fromJson(resultSet.getString(COL_TICKET), TambolaTicketVO.class))
                .assignTo(resultSet.getString(COL_ASSIGNED_TO))
                .build();
          }
        }, params);
  }

  public GameTicket getTicketForByGameIDAndTicketID(Integer gameID, Integer ticketID){

    Map<String, Object> params = new HashMap<>();
    params.put(COL_GAME_ID, gameID);
    params.put(COL_TICKET_ID, ticketID);

    return jdbcTemplateWrapper
        .querySingleRow(String.format(
            "SELECT * FROM game_ticket WHERE %1$s=:%1$s AND %2$s=:%2$s",
            COL_GAME_ID, COL_TICKET_ID), new RowMapper<GameTicket>() {
          @Override
          public GameTicket mapRow(ResultSet resultSet, int i) throws SQLException {
            return GameTicket.builder()
                .gameID(resultSet.getInt(COL_GAME_ID))
                .ticketID(resultSet.getInt(COL_TICKET_ID))
                .ticket(new Gson().fromJson(resultSet.getString(COL_TICKET), TambolaTicketVO.class))
                .assignTo(resultSet.getString(COL_ASSIGNED_TO))
                .build();
          }
        }, params);
  }

  public Optional<GameTicket> getAvailableTicket(Integer gameID){

    Map<String, Object> params = new HashMap<>();
    params.put(COL_GAME_ID, gameID);

    return jdbcTemplateWrapper
        .queryAnyRow(String.format(
            "SELECT * FROM game_ticket WHERE %1$s=:%1$s AND (%2$s is null OR %2$s='')",
            COL_GAME_ID, COL_ASSIGNED_TO), new RowMapper<GameTicket>() {
          @Override
          public GameTicket mapRow(ResultSet resultSet, int i) throws SQLException {
            return GameTicket.builder()
                .gameID(resultSet.getInt(COL_GAME_ID))
                .ticketID(resultSet.getInt(COL_TICKET_ID))
                .ticket(new Gson().fromJson(resultSet.getString(COL_TICKET), TambolaTicketVO.class))
                .assignTo(resultSet.getString(COL_ASSIGNED_TO))
                .build();
          }
        }, params);
  }
}
