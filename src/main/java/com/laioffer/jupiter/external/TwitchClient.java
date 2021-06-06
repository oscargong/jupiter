package com.laioffer.jupiter.external;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laioffer.jupiter.entity.Game;
import com.laioffer.jupiter.entity.Item;
import com.laioffer.jupiter.entity.ItemType;
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
import java.util.*;
import java.util.concurrent.TimeoutException;

public class TwitchClient {

    private static final String TOKEN = "Bearer 4f8b8tznuohwsifln2de4amtx9r85c";
    private static final String CLIENT_ID = "62uhtf0avhtmeoq70b3qw26umcz5yz";
    private static final String TOP_GAME_URL = "https://api.twitch.tv/helix/games/top?first=%s";
    private static final String GAME_SEARCH_URL_TEMPLATE = "https://api.twitch.tv/helix/games?name=%s";
    private static final int DEFAULT_GAME_LIMIT = 20;

    private static final String STREAM_SEARCH_URL_TEMPLATE = "https://api.twitch.tv/helix/streams?game_id=%s&first=%s";
    private static final String VIDEO_SEARCH_URL_TEMPLATE = "https://api.twitch.tv/helix/videos?game_id=%s&first=%s";
    private static final String CLIP_SEARCH_URL_TEMPLATE = "https://api.twitch.tv/helix/clips?game_id=%s&first=%s";
    private static final String TWITCH_BASE_URL = "https://www.twitch.tv/";
    private static final int DEFAULT_SEARCH_LIMIT = 20;


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


    private String buildSearchURL(String url, String gameId, int limit) {
        try {
            gameId = URLEncoder.encode(gameId, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return String.format(url, gameId, limit);
    }

    // Similar to getGameList, convert the json data returned from Twitch to a list of Item objects.
    private List<Item> getItemList(String data) throws TwitchException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return Arrays.asList(mapper.readValue(data, Item[].class));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new TwitchException("Failed to parse item data from Twitch API");
        }
    }

    private List<Item> searchStreams(String gameId, int limit) throws TwitchException {
        List<Item> streams = getItemList(
                searchTwitch(
                        buildSearchURL(STREAM_SEARCH_URL_TEMPLATE, gameId, limit)
                )
        );
        for (Item item: streams) {
            item.setType(ItemType.STREAM);
            item.setUrl(TWITCH_BASE_URL + item.getBroadcasterName());
        }

        return streams;
    }

    // Returns the top x clips based on game ID.
    private List<Item> searchClips(String gameId, int limit) throws TwitchException {
        List<Item> clips = getItemList(
                searchTwitch(
                        buildSearchURL(CLIP_SEARCH_URL_TEMPLATE, gameId, limit)
                )
        );
        for (Item item : clips) {
            item.setType(ItemType.CLIP);
        }
        return clips;
    }

    // Returns the top x videos based on game ID.
    private List<Item> searchVideos(String gameId, int limit) throws TwitchException {
        List<Item> videos = getItemList(
                searchTwitch(
                        buildSearchURL(VIDEO_SEARCH_URL_TEMPLATE, gameId, limit)
                )
        );
        for (Item item : videos) {
            item.setType(ItemType.VIDEO);
        }
        return videos;
    }


    public List<Item> searchByType(String gameId, ItemType type, int limit)  throws TwitchException {
        List<Item> items = Collections.emptyList();

        switch (type) {
            case STREAM:
                items = searchStreams(gameId, limit);
                break;
            case VIDEO:
                items = searchVideos(gameId, limit);
                break;
            case CLIP:
                items = searchClips(gameId, limit);
                break;
        }

        // Update gameId for all items. GameId is used by recommendation system
        for (Item item : items) {
            item.setGameId(gameId);
        }
        return items;
    }


    public Map<String, List<Item>> searchItems(String gameId) throws TwitchException {
        Map<String, List<Item>> itemMap = new HashMap<>(); // Map 是 interface

        for (ItemType type : ItemType.values()) {
            itemMap.put(type.toString(), searchByType(gameId, type, DEFAULT_GAME_LIMIT));
        }

        return itemMap;
    }





}

