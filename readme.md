[![Build Status](https://travis-ci.org/YelsewB/CryptoAPI.svg?branch=master)](https://travis-ci.org/YelsewB/CryptoAPI) 
[![codecov](https://codecov.io/gh/YelsewB/CryptoAPI/branch/master/graph/badge.svg)](https://codecov.io/gh/YelsewB/CryptoAPI)




**Show all currencies**


*GET /api/currencies*

```bash 
curl -v -H "Accept: application/json" -H "Content-type: application/json" GET http://localhost:8080/api/currencies
```

**Get specific currency**

*GET /api/currencies/:ticker*

```bash 
curl -v -H "Accept: application/json" -H "Content-type: application/json" GET http://localhost:8080/api/currencies/XRP
```

**Create currency**

*POST /api/currencies*

Body:
```json
{"ticker":"XRP","name":"Ripple","numberOfCoins":1234567890,"marketCap":987654321}
```

```bash 
curl -v -H "Accept: application/json" -H "Content-type: application/json" POST -d '{"ticker":"XRP","name":"Ripple","numberOfCoins":1234567890,"marketCap":987654321}' http://localhost:8080/api/currencies
```



**Update currency**

*PUT /api/currencies/:ticker*

Body:
```json
{"ticker":"XRP","name":"New Ripple","numberOfCoins":13371337,"marketCap":7331733112}
```

```bash 
curl -v -H "Accept: application/json" -H "Content-type: application/json" -X PUT -d '{"ticker":"XRP","name":"New Ripple",
"numberOfCoins":13371337,"marketCap":7331733112}' http://localhost:8080/api/currencies/XRP
```

**Delete currency**

*DELETE /api/currencies/:ticker*

```bash 
curl -v -H "Accept: application/json" -H "Content-type: application/json" -X DELETE http://localhost:8080/api/currencies/XRP
```
