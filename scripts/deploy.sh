#!/bin/bash

set -e

APP_NAME="od-cloud"
JAR_NAME="od-cloud.jar"
SERVICE_PATH="/home/od/moimism"
LOG_FILE="$SERVICE_PATH/application.log"
PID_FILE="$SERVICE_PATH/application.pid"
HEALTH_CHECK_URL="http://localhost:8081/actuator/health"
HEALTH_CHECK_MAX_RETRY=30
HEALTH_CHECK_INTERVAL=2

cd $SERVICE_PATH

echo "=========================================="
echo "Deployment started at $(date)"
echo "=========================================="

# 1. Stop existing process
echo "[1/3] Stopping existing process..."
if [ -f "$PID_FILE" ]; then
    PID=$(cat $PID_FILE)
    if ps -p $PID > /dev/null 2>&1; then
        echo "Stopping process with PID: $PID"
        kill $PID

        # Wait for process to terminate (max 30 seconds)
        WAIT_COUNT=0
        while ps -p $PID > /dev/null 2>&1; do
            if [ $WAIT_COUNT -ge 30 ]; then
                echo "Process did not terminate gracefully. Force killing..."
                kill -9 $PID
                break
            fi
            echo "Waiting for process to stop... ($WAIT_COUNT/30)"
            sleep 1
            ((WAIT_COUNT++))
        done
        echo "Process stopped."
    else
        echo "PID file exists but process is not running."
    fi
    rm -f $PID_FILE
else
    echo "No PID file found. Checking for running process..."
    RUNNING_PID=$(pgrep -f "$JAR_NAME" || true)
    if [ -n "$RUNNING_PID" ]; then
        echo "Found running process with PID: $RUNNING_PID. Stopping..."
        kill $RUNNING_PID
        sleep 5
    else
        echo "No running process found."
    fi
fi

# 2. Start application
echo "[2/3] Starting application..."
nohup java -jar $SERVICE_PATH/$JAR_NAME \
    --spring.profiles.active=prod \
    > $LOG_FILE 2>&1 &

NEW_PID=$!
echo $NEW_PID > $PID_FILE
echo "Application started with PID: $NEW_PID"

# 3. Health check
echo "[3/3] Performing health check..."
sleep 5

RETRY_COUNT=0
while [ $RETRY_COUNT -lt $HEALTH_CHECK_MAX_RETRY ]; do
    HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" $HEALTH_CHECK_URL 2>/dev/null || echo "000")

    if [ "$HTTP_STATUS" = "200" ]; then
        echo "Health check passed! (HTTP $HTTP_STATUS)"
        echo "=========================================="
        echo "Deployment completed successfully!"
        echo "=========================================="
        exit 0
    fi

    ((RETRY_COUNT++))
    echo "Health check attempt $RETRY_COUNT/$HEALTH_CHECK_MAX_RETRY (HTTP $HTTP_STATUS)"
    sleep $HEALTH_CHECK_INTERVAL
done

echo "=========================================="
echo "Health check failed after $HEALTH_CHECK_MAX_RETRY attempts!"
echo "Check logs: tail -f $LOG_FILE"
echo "=========================================="
exit 1
