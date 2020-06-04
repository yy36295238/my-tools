#!/bin/bash

#Jar包名称
JAR_NAME=my-tools-web-1.0.0.jar
#服务名称
SERVICE_NAME=risk-web-tool-server
#服务目录
SERVICE_HOME=/Users/yangyu/workspace/risk-web-tool-server/target
#日志目录
SERVICE_LOGS=$SERVICE_HOME/log

#java虚拟机启动参数
JAVA_OPTS="-ms512m -mx512m -Xmn256m -Djava.awt.headless=true"

#进入服务目录
cd $SERVICE_HOME

#脚本目录
SHELL_EXE=/Users/yangyu/my-shell/$0

case "$1" in
    start)
        if [ ! -d $SERVICE_LOGS ]; then
            mkdir "$SERVICE_LOGS"
        else
            echo "$SERVICE_LOGS exists!"
        fi
        nohup java $JAVA_OPTS -jar $JAR_NAME > $SERVICE_LOGS/$SERVICE_NAME.log  2>&1 &
        echo "==== start $SERVICE_NAME ===="
        ;;
    stop)
	ps -ef|grep $JAR_NAME|grep -v grep|awk '{print $2}'|xargs kill -9
        echo "==== stop $SERVICE_NAME ===="
        ;;
    restart)
        sh $SHELL_EXE stop
        sleep 2
        sh $SHELL_EXE start
        ;;
    *)
        sh $SHELL_EXE stop
        sleep 2
        sh $SHELL_EXE start
        ;;
esac
exit 0