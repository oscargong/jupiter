package com.laioffer.jupiter.serverlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laioffer.jupiter.db.MySQLConnection;
import com.laioffer.jupiter.db.MySQLException;
import com.laioffer.jupiter.entity.User;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "RegisterServlet", value = "/register")
public class RegisterServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ObjectMapper mapper = new ObjectMapper();

        User user = mapper.readValue(request.getReader(), User.class);

        if (user == null) {
            System.err.println("[RegisterServlet], User information incorrect");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        boolean isUserAdded = false;

        try (MySQLConnection conn = new MySQLConnection()) {
            user.setPassword(
                    ServletUtil.encryptPassword(user.getUserId(), user.getPassword())
            );
            isUserAdded = conn.addUser(user);

        } catch (MySQLException | SQLException throwables) {
            throwables.printStackTrace();
            throw new ServletException(throwables);
        }

        if (!isUserAdded) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
        }

    }
}
