# journey-service

Steps to run:
1. Run docker-compose up --build   - This will spin up the redis server 
2. Run the springboot application
3. The swagger can be retrieved using the following url : http://localhost:8080/swagger-ui/index.html#
Journeys application using redis cache

Save journey:

Request:
curl --location --request POST 'http://localhost:8080/v1/journey' \
--header 'api-user-id: user1' \
--header 'method: 2' \
--header 'Content-Type: application/json' \
--data-raw '{
    "source":"DXB",
    "destination":"CPH"
    
}'
Response:
{
    "code": 0,
    "message": "Success",
    "data": {
        "user": null,
        "requestUser": "user1",
        "journeyId": "28d38915-df6a-4a41-9e2b-171b94ac83cc",
        "source": "DXB",
        "destination": "CPH",
        "method": 2
    }
}


Get Journey given a journey id :

curl --location --request GET 'http://localhost:8080/v1/journey/f94a2f93-3344-4358-85c2-50bbbd2f317f1' \
--header 'api-user-id: user1'

Get journeys of a user:

curl --location --request GET 'http://localhost:8080/v1/journey/user/user2/journeys' \
--header 'api-user-id: user2' \
--header 'method: 2'

Delete journeys of a user:

curl --location --request DELETE 'http://localhost:8080/v1/journey' \
--header 'api-user-id: user1'
