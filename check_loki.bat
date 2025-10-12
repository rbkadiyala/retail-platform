@echo off
echo Sending test request to generate log...
curl -s -X GET http://localhost:9083/api/users/1
echo.
echo Waiting 5 seconds for Promtail to send the log...
timeout /t 5 >nul

echo =================================================
echo Latest 5 logs for service="user-service":
echo =================================================
curl -s -G http://localhost:3100/loki/api/v1/query ^
     --data-urlencode "query={service=\"user-service\"}" ^
     --data-urlencode "limit=5"
echo.

echo =================================================
echo Latest 5 incoming logs only:
echo =================================================
curl -s -G http://localhost:3100/loki/api/v1/query ^
     --data-urlencode "query={service=\"user-service\"} |= \"incoming\"" ^
     --data-urlencode "limit=5"
echo.

echo Done.
pause
