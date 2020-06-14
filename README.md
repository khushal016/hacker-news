# hacker-news

*Need docker and docker-compose to start the project

Steps to run project-
1. Generate jar file ->  mvn clean package
2. Build docker image -> docker build -t hacker-news .
3. Start docker containers -> docker-compose up -d
4. Check logs -> docker logs -f hacker-news
