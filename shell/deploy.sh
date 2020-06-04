#!/bin/bash

JAR_NAME=my-tools-web-1.0.0.jar
PATH='/mydata/github-code/my-tools/'

cd $PATH

pwd

git pull

mvn clean package -Dmaven.test.skip=true

ps -ef|grep $JAR_NAME|grep -v grep|awk '{print $2}'|xargs kill -9

cd $PATH'my-tools-web/target'

nohup java -jar $JAR_NAME --spring.profiles.active=pro &

exit 0