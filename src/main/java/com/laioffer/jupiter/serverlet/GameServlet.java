package com.laioffer.jupiter.serverlet;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

import org.json.JSONObject;

@WebServlet(name = "GameServlet", value = "/game")
//@WebServlet(name = "GameServlet", urlPatterns = {"/game"})

public class GameServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        response.getWriter().print("Hello World");
        String gamename = request.getParameter("gamename");
        response.setContentType("application/json");
        JSONObject game = new JSONObject();

        game.put("name", "World of Warcraft");
        game.put("developer", "Blizzard Entertainment");
        game.put("release_time", "Feb 11, 2005");
        game.put("website", "https://www.worldofwarcraft.com");
        game.put("price", 49.99);

        // Write game information to response body
        response.getWriter().print(game);

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
