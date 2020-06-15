# Hacker News

## Table of Contents

- [Setup](#setup)
- [Features](#features)

## Setup

- Need docker and docker-compose to start the project

#### Clone

- Clone this repo to your local machine using `https://github.com/khushal016/hacker-news.git`

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

> check service logs

```shell
$ docker logs -f hacker-news
```

## Features

- **Get Top 10 stories ranked by score in the last 10 minutes**
    - http://localhost:8080/top-stories
    
- **Get all past top stories**
    - http://localhost:8080/past-stories
    
- **Get Top 10 parent comments on a given story(sorted by the total number of comments)**
    - http://localhost:8080/comments?storyId=23517193


