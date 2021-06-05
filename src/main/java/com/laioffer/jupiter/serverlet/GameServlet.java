package com.laioffer.jupiter.serverlet;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.laioffer.jupiter.entity.Game;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laioffer.jupiter.external.TwitchClient;
import com.laioffer.jupiter.external.TwitchException;
import org.json.JSONObject;
import org.apache.commons.io.IOUtils;

@WebServlet(name = "GameServlet", value = "/game")
//@WebServlet(name = "GameServlet", urlPatterns = {"/game"})

public class GameServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // get GameName from request URL
        String gameName = request.getParameter("game_name");

        TwitchClient client = new TwitchClient();

        response.setContentType("application/json");
        try {
            if (gameName != null) {
                response.getWriter().print(
                        new ObjectMapper().writeValueAsString(
                                client.searchGame(gameName)
                        )
                );
            }
            else {
                response.getWriter().print(
                        new ObjectMapper().writeValueAsString(client.topGames(0))
                );
            }
        } catch (TwitchException | TimeoutException e) {
            e.printStackTrace();
            throw new ServletException(e);
        }

    }


//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//    }
}
