# health_check.sh

#!/bin/bash

# Crawl current connected port of WAS
CURRENT_PORT=$(cat /etc/nginx/conf.d/service_url.inc | grep -Po '[0-9]+' | tail -1)
TARGET_PORT=0

# Toggle port Number
if [ ${CURRENT_PORT} -eq 8081 ]; then
    TARGET_PORT=8082
elif [ ${CURRENT_PORT} -eq 8082 ]; then
    TARGET_PORT=8081
else
    echo "> No WAS is connected to nginx"
    exit 1
fi

TARGET_PID=$(lsof -Fp -i TCP:${TARGET_PORT} | grep -Po 'p[0-9]+' | grep -Po '[0-9]+')

if [ ! -z ${TARGET_PID} ]; then
  echo "> Kill WAS running at ${TARGET_PORT}."
  sudo kill ${TARGET_PID}
fi

nohup sudo java -jar -Dserver.port=${TARGET_PORT} /home/ubuntu/coffang/deploy/coffang-0.0.1-SNAPSHOT.jar > /home/ubuntu/nohup.out 2>&1 &
echo "> Now new WAS runs at ${TARGET_PORT}."

echo "> Start health check of WAS at 'http://127.0.0.1:${TARGET_PORT}' ..."

for RETRY_COUNT in 1 2 3 4 5 6 7 8 9 10
do
    echo "> #${RETRY_COUNT} trying..."
    RESPONSE_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://127.0.0.1:${TARGET_PORT}/health)
    # RESPONSE_CODE=$(curl -s -o /dev/null -w "%{http_code}" https://sparta-gi.shop/health)

    if [ ${RESPONSE_CODE} -eq 200 ]; then
        echo "> New WAS successfully running"
        exit 0
    elif [ ${RETRY_COUNT} -eq 10 ]; then
        echo "> Health check failed."
        exit 1
    fi
    sleep 10
done
