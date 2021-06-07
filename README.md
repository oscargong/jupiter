

# Twitch Streaming and Recommendation Application 
This is a Twitch streaming application with a live video recommendation API and search system, based on a variety of recommendation algorithms.

Built by React, Java Servlet and Tomcat, deployed on AWS.


## Local API

- *GET* top games: `http://localhost:8080/jupiter/game`
- *GET* search game by name: `http://localhost:8080/jupiter/game?game_name=Starcraft II`

- *GET* search streams/video/clip by `game_id`: `http://localhost:8080/jupiter/search?game_id=490422`

## Twitch API Reference

- [Authentation](https://dev.twitch.tv/docs/authentication/getting-tokens-oauth#oauth-client-credentials-flow)
- [Get top games](https://dev.twitch.tv/docs/api/reference#get-top-games): Gets games sorted by number of current viewers on Twitch, most popular first.
- [Get games](https://dev.twitch.tv/docs/api/reference#get-games): Gets game information by game ID or name.
- Game related APIs:
    - https://dev.twitch.tv/docs/api/reference#get-streams
    - https://dev.twitch.tv/docs/api/reference#get-videos
    - https://dev.twitch.tv/docs/api/reference#get-clips

## AWS

### *MySQL DB Instance on RDS*

- Create a MySQL DB Instance on AWS RDS.

- Change `db/MySQLDBUtil` `INSTANCE` to your MySQL DB Instance's endpoint.

- Create a `config.properties` under `main/resources`, add the following lines.

    ```properties
    # MySQL Properties
    user=YOUR_ADMIN
    password=YOUR_PASSWORD
    ```
### EC2


## Diagram

![](./resources/TwichAPI.svg)

<img src="./resources/MySQL.svg" style="zoom:75%;" />    

