package com.laioffer.jupiter.serverlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laioffer.jupiter.db.MySQLConnection;
import com.laioffer.jupiter.entity.LoginRequestBody;
import com.laioffer.jupiter.entity.LoginResponseBody;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "LoginServlet", value = "/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /**
         * read user data from request body
         */

        ObjectMapper mapper = new ObjectMapper();
        LoginRequestBody body =  mapper.readValue(request.getReader(), LoginRequestBody.class);

        if (body == null) {
            System.err.println("[LoginServlet] User info incorrect.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String username = "";

        // TODO 前端直接传 hash 过的密码
        try (MySQLConnection conn = new MySQLConnection()) {
            String userId = body.getUserId();
            String hashedPassword = ServletUtil.encryptPassword(
                    userId, body.getPassword()
            );

            username = conn.verifyLogin(userId, hashedPassword);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        // Create a new session for the user if user ID and password are correct,
        // otherwise return Unauthorized error.
        if (!username.isEmpty()) {
            // username 不为空，验证成功
            // Create a new session, put user ID as an attribute into the session object
            // set the expiration time to 600 seconds.
            HttpSession session = request.getSession(); // 无 fasle，因为不存在时候不创建新的
            session.setAttribute("user_id", body.getUserId());
            /* Setting a variable user_id containing the value as the fetched usernId as
               an attribute of the session which will be shared among different servlets
               of the application
           */

            session.setMaxInactiveInterval(600); // 10 mins

            var loginResponseBody = new LoginResponseBody(
                    body.getUserId(), username
            );

//            ServletUtil.writeData(response, loginResponseBody);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter()
                    .print(
                            new ObjectMapper().writeValueAsString(loginResponseBody)
                    );

        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // HTTP 401
        }


    }
}
