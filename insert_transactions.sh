curl --location --request PUT 'https://transaction-service-loco.herokuapp.com/transactionService/transaction/1120' \
--header 'Content-Type: application/json' \
--data-raw '{
    "value":10,

    "type":"car",

    "parentId":null
}'


curl --location --request PUT 'https://transaction-service-loco.herokuapp.com/transactionService/transaction/1121' \
--header 'Content-Type: application/json' \
--data-raw '{
    "value":100,

    "type":"personal",

    "parentId":1120
}'


curl --location --request PUT 'https://transaction-service-loco.herokuapp.com/transactionService/transaction/1122' \
--header 'Content-Type: application/json' \
--data-raw '{
    "value":1000,

    "type":"cash",

    "parentId":1120
}'

curl --location --request PUT 'https://transaction-service-loco.herokuapp.com/transactionService/transaction/1123' \
--header 'Content-Type: application/json' \
--data-raw '{
    "value":10000,

    "type":"stuff",

    "parentId":1122
}'