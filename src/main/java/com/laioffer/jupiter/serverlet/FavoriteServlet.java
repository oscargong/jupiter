package com.laioffer.jupiter.serverlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laioffer.jupiter.db.MySQLConnection;
import com.laioffer.jupiter.db.MySQLException;
import com.laioffer.jupiter.entity.FavoriteRequestBody;
import com.laioffer.jupiter.entity.Item;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "FavoriteServlet", value = "/favorite")
//@WebServlet(name = "FavoriteServlet", urlPatterns = {"/favorite"})
public class FavoriteServlet extends HttpServlet {

    // Get user ID from request URL, this is a temporary solution since we don’t support session now
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        String userId = request.getParameter("user_id");

        HttpSession session = request.getSession(false); // fasle，不存在时候创建新的
        if (session == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String userId = (String) session.getAttribute("user_id");

        ObjectMapper mapper = new ObjectMapper();
        FavoriteRequestBody body = mapper.readValue( request.getReader(), FavoriteRequestBody.class);

        if (body == null) {
            System.err.println("Convert JSON to FavoriteRequestBody failed");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        try (MySQLConnection conn = new MySQLConnection() ) {
            conn.setFavoriteItem(userId, body.getFavoriteItem());
        } catch (MySQLException | SQLException e) {
            throw new ServletException(e);
        }

    }


    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false); // fasle，不存在时候创建新的
        if (session == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        String userId = (String) session.getAttribute("user_id");

        ObjectMapper mapper = new ObjectMapper();
        FavoriteRequestBody body = mapper.readValue(request.getReader(), FavoriteRequestBody.class);

        if (body == null) {
            System.err.println("Convert JSON to FavoriteRequestBody failed");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        try (MySQLConnection conn = new MySQLConnection()) {
            conn.unsetFavoriteItem(userId, body.getFavoriteItem().getId() );
        } catch (MySQLException | SQLException e) {
            throw new ServletException(e);
        }

    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        String userId = request.getParameter("user_id");

        HttpSession session = request.getSession(false); // fasle，存在时候创建新的
        if (session == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        String userId = (String) session.getAttribute("user_id");

        try(MySQLConnection conn = new MySQLConnection()) {
            Map<String, List<Item>> itemMap = conn.getFavoriteItems(userId);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print(
                    new ObjectMapper().writeValueAsString(itemMap)
            );
        } catch (MySQLException | SQLException e ) {
            throw new ServletException(e);
        }
    }

}
