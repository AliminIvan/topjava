## MealRestController:
#### For pretty json output download and install NodeJS from its <a href="https://nodejs.org/en/">official homepage</a>.
#### Then open a new command prompt, type the following: "npm install -g json"
#### This command will install JSON tool. Then you can pretty print JSON output from curl using the follow command "| json" in the end of curl
#### To use below curl commands without pretty json output use them without "| json" in the end.

### GET commands:
- **getAll:** curl localhost:8080/topjava/rest/meals -v | json
- **get:** curl localhost:8080/topjava/rest/meals/100005 -v | json
- **getBetween (with empty startDate):** curl -G -d "startDate=" -d "startTime=10:00" -d "endDate=2020-01-30" -d "endTime=20:00" localhost:8080/topjava/rest/meals/filter -v | json

### CREATE and UPDATE commands:
**CAUTION!!! COPY THIS COMMANDS FROM PREVIEW, DO NOT COPY FROM EDITOR, BECAUSE BACKSLASH IS USED TO ESCAPE A SERVICE CHARACTER** 
- **createWithLocation:** curl localhost:8080/topjava/rest/meals -H "Content-Type: application/json" -d "{\\"id\\": null, \\"dateTime\\": \\"2023-11-28T20:00:00\\", \\"description\\": \\"Обед\\", \\"calories\\": 700, \\"user\\": null}" -v | json
- **update:** curl -X PUT localhost:8080/topjava/rest/meals/100004 -H "Content-Type: application/json" -d "{\\"id\\": 100004, \\"dateTime\\": \\"2020-01-30T13:00:00\\", \\"description\\": \\"Ужин\\", \\"calories\\": 800, \\"user\\": null}" -v | json

### DELETE command:
- **delete:** curl -X DELETE localhost:8080/topjava/rest/meals/100009 -v | json