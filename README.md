# Hacker News API

Hacker News API implementation using Spring boot and Redis

- It pulls the top stories from hacker news and stores top 10 stories ranked by score every minute
- Top stories are saved in redis, sorted by insert timestamp in a sorted set



## Quick Start

- Need docker and docker-compose to start the project
- If you want to run this project without docker then change redis hostname to 'localhost' in HNConfiguration.java


> clone this repo

```shell
$ git clone https://github.com/khushal016/hacker-news.git
```

> cd to repo folder

```shell
$ cd hacker-news
```

> generate jar file

```shell
$ mvn clean package
```
> build docker image

```shell
$ docker build -t hacker-news .
```

> start docker containers

```shell
$ docker-compose up -d
```

> check application logs

```shell
$ docker logs -f hacker-news
```

## Example

- **Get Top 10 stories ranked by score in the last 10 minutes**
    - http://localhost:8080/top-stories
    
- **Get all past top stories**
    - http://localhost:8080/past-stories
    
- **Get Top 10 parent comments on a given story(sorted by the total number of comments)**
    - http://localhost:8080/comments?storyId=23517193


