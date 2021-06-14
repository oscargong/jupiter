package com.laioffer.jupiter.serverlet;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laioffer.jupiter.entity.Item;
import org.apache.commons.codec.digest.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ServletUtil {
    public static <T> void writeData(HttpServletResponse response, T data) throws IOException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print(new ObjectMapper().writeValueAsString(data));
    }


    public static String encryptPassword(String userId, String password) throws IOException {
        /**
         * Help encrypt the user password before save to the database
         * Use userId as the salt
         * @return
         */

        String md5Password = DigestUtils.md5Hex(password);

        return DigestUtils.md5Hex(
                userId + md5Password
        ).toLowerCase();

    }

    public static <T> T readRequestBody(Class<T> tClass, HttpServletRequest request) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.readValue(request.getReader(), tClass);
        } catch (JsonParseException | JsonMappingException e ) {
            e.printStackTrace();
            return null;
        }

    }


}