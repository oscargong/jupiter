

# Twitch Streaming and Recommendation Application 
This is a Twitch streaming application with a live video recommendation API and search system, based on a variety of recommendation algorithms.

Build by React, Java Servlet and Tomcat, deplyed on AWS.

## Diagram

![](./resources/TwichAPI.png)

## Local API

- GET top games: `http://localhost:8080/jupiter/game`
- GET search game by name: `http://localhost:8080/jupiter/game?game_name=Starcraft II`



## Twitch API Reference

- [Authentation](https://dev.twitch.tv/docs/authentication/getting-tokens-oauth#oauth-client-credentials-flow)
- [Get top games](https://dev.twitch.tv/docs/api/reference#get-top-games): Gets games sorted by number of current viewers on Twitch, most popular first.
- [Get games](https://dev.twitch.tv/docs/api/reference#get-games): Gets game information by game ID or name.
- Game related APIs:
    - https://dev.twitch.tv/docs/api/reference#get-streams
    - https://dev.twitch.tv/docs/api/reference#get-videos
    - https://dev.twitch.tv/docs/api/reference#get-clips

