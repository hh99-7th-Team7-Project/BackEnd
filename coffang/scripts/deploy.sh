REPOSITORY=/home/ubuntu/coffang
cd $REPOSITORY

APP_NAME=coffang #1
JAR_NAME=$(ls $REPOSITORY/ | grep '.jar' | tail -n 1)
JAR_PATH=$REPOSITORY/$JAR_NAME

CURRENT_PID=$(pgrep -f $APP_NAME)
if [ -z $CURRENT_PID ] #2
then
  echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> kill -15 $CURRENT_PID"
  sudo kill -15 $CURRENT_PID
  sleep 5
fi

echo "> $JAR_PATH 배포" #3
nohup java -jar /home/ubuntu/coffang/coffang-0.0.1-SNAPSHOT.jar > /dev/null 2> /dev/null < /dev/null &
