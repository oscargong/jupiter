package com.laioffer.jupiter.external;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laioffer.jupiter.entity.Game;
import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class TwitchClient {

    private static final String TOKEN = "Bearer 4f8b8tznuohwsifln2de4amtx9r85c";
    private static final String CLIENT_ID = "62uhtf0avhtmeoq70b3qw26umcz5yz";
    private static final String TOP_GAME_URL = "https://api.twitch.tv/helix/games/top?first=%s";
    private static final String GAME_SEARCH_URL_TEMPLATE = "https://api.twitch.tv/helix/games?name=%s";
    private static final int DEFAULT_GAME_LIMIT = 20;


    private String buildGameURL(String url, String gameName, int limit) {
        /**
         * This method is a helper to generate the correct URL when calling the Twitch Game API.
         * @param url This is the Twitch endpoint urls, it should contain a format space, such as %s
         * @param gameName
         * @param limit a limit can be used to limit the number of the response
         * @return String The URL to call
         */

        if (gameName.equals("")) {
            return String.format(url, limit);
        }
        else {
            try {
                // Encode special characters in URL
                gameName = URLEncoder.encode(gameName, "UTF-8");

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            return String.format(url, gameName);
        }
    }


    private String searchTwitch(String url) throws TwitchException {
        /**
         * Send HTTP request to Twitch Backend based on the given URL
         * @return the body of the HTTP response returned from Twitch backend.
         */

        // the response handler to parse and return HTTP response body returned from Twitch
        ResponseHandler<String> responseHandler = response -> {
            int responseCode = response.getStatusLine().getStatusCode();
            if (responseCode != 200) {
                System.out.println("Response Status: " + response.getStatusLine().getReasonPhrase() );

                throw new TwitchException(
                        "Fail to get result from Twitch API:" + response.getStatusLine().getReasonPhrase()
                );
            }

                HttpEntity entity = response.getEntity();
                if (entity == null) {
                    throw new TwitchException(
                            "Fail to get result from Twitch API: failed to get entity"
                    );
                }

                JSONObject obj = new JSONObject(EntityUtils.toString(entity));
                return obj.getJSONArray("data").toString();
        };

        // the HTTP request
        try( CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            request.setHeader("Authorization", TOKEN);
            request.setHeader("Client-ID", CLIENT_ID);
            return httpClient.execute(request, responseHandler);
        } catch (IOException e) {
            e.printStackTrace();
            throw new TwitchException("Failed to get result from Twitch API");
        }
    }

    // Convert JSON format data returned from Twitch
    // to an Arraylist of Game objects
    private List<Game> getGameList(String data) throws TimeoutException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return Arrays.asList(mapper.readValue(data, Game[].class));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new TwitchException("Failed to parse game data from Twitch API: "+ data);
        }
    }

    public List<Game> topGames(int limit) throws TwitchException, TimeoutException {
        /**
         * Integrate search() and getGameList() together
         * @return the top x popular games
         */
        if (limit <= 0) {
            limit = DEFAULT_GAME_LIMIT;
        }
        return getGameList(searchTwitch(buildGameURL(TOP_GAME_URL,"",limit)));
    }

    public Game searchGame(String gameName) throws TwitchException, TimeoutException {
        /**
         * Integrate search() and getGameList() together
         * @return the dedicated game based on the game's name
         */
        List<Game> gameList = getGameList(searchTwitch(buildGameURL(GAME_SEARCH_URL_TEMPLATE,gameName,0)));

        if (gameList.size()!=0) {
            return gameList.get(0);
        } else {
            return null;
        }
    }


}

