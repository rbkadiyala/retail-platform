@echo off
echo =================================================
echo Querying count of incoming logs for user-service (last 5m)...
curl -s -G http://localhost:3100/loki/api/v1/query ^
     --data-urlencode "query=count_over_time({job=\"user-service\", direction=\"incoming\"}[15m])"
echo.
echo =================================================
echo Querying total count of logs for user-service (last 5m)...
curl -s -G http://localhost:3100/loki/api/v1/query ^
     --data-urlencode "query=count_over_time({job=\"user-service\"}[15m])"
echo.
echo =================================================
echo Querying count of outgoing logs for user-service (last 5m)...
curl -s -G http://localhost:3100/loki/api/v1/query ^
     --data-urlencode "query=count_over_time({job=\"user-service\", direction=\"outgoing\"}[15m])"
echo.
echo Done.
pause
