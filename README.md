# TrafficDT
This represents the environment for traffic digital twins.
The idDT keyword in the following topics should be composed like that:
```
trafficdt-%roadId%-%direction%
```

# Create new Traffic DT via Administration Gateway
Make a POST request to the following url: /traffic.
The body should be a JSON described as follows:
```json
{
  "roadId": "",
  "direction": "",
  "numLanes": "",
  "numBlocks": ""
}
```

# Add new car from action via HTTP directly from the Traffic DT
Make a POST request to the following url:
```
http://localhost:%port%/state/actions/carEnteredAction
```
The body should be a JSON described as follows:
```json
{
  "idCar": "car10",
  "currentSpeed": "5",
  "state": "RUNNING",
  "lane": "0",
  "positionX": "11.1",
  "positionY": "11.1",
  "indexP": "0",
  "dPointX": "0.0",
  "dPointY": "0.0"
}
```

# Topics for Traffic Physical Adapter Communication
## Properties
### Road start point (x,y) coordinates
Topic: trafficdt-physical-%idDT%/positionAX \
Value type: Float 

Topic: trafficdt-physical-%idDT%/positionAY \
Value type: Float

### Road end point (x,y) coordinates
Topic: trafficdt-physical-%idDT%/positionBX \
Value type: Float

Topic: trafficdt-physical-%idDT%/positionBY \
Value type: Float

### Security distance
Topic: trafficdt-physical-%idDT%/securityDistance \
Value type: Float

### Road id
Topic: trafficdt-physical-%idDT%/roadId \
Value type: String

### Direction
Topic: trafficdt-physical-%idDT%/direction \
Value type: Int

## Events
### Car update
Topic: trafficdt-physical-%idDT%/carUpdate
```json
{
  "idCar": "",
  "currentSpeed": "",
  "state": "",
  "lane": "",
  "positionX": "",
  "positionY": "",
  "indexP": "",
  "dPointX": "",
  "dPointY": ""
}
```

### Car entered on road
Topic: trafficdt-physical-%idDT%/carEntered
```json
{
  "idCar": "",
  "currentSpeed": "",
  "state": "",
  "lane": "",
  "positionX": "",
  "positionY": "",
  "indexP": "",
  "dPointX": "",
  "dPointY": ""
}
```

### Car exited from road
Topic: trafficdt-physical-%idDT%/carExited
Value type: String (carId)

# Events emitted
## Next car distance
In order to know the next car distance from a specific car you should connect to the following topic:
```
trafficdt-digital-cars-digital-adapter/cars/%idCar%/distanceFromNext
```
You should get the following JSON:
```json
{
  "idCar": "car2",
  "idNextCar": "car3",
  "distance": 8.485281944274902,
  "speed": 5
}
```
The digital twin sends this update every 1 second.

## Update car
This update message is emitted via MQTT topic when a car is modified. The topic is the following:
```
trafficdt-digital-cars-digital-adapter/cars/carUpdate
```
```json
{
  "idCar": "",
  "currentSpeed": "",
  "state": "",
  "lane": "",
  "positionX": "",
  "positionY": "",
  "indexP": "",
  "dPointX": "",
  "dPointY": ""
}
```

# Traffic DT get requests
If you need to obtain information about actual running Traffic DTs you can use the aggregate DT microservice adapter.
## Get all the traffic DTs
Make a GET request to the following url.
```
http://localhost:%port%/getAllTrafficManagers
```
## Filter Traffic DTs
Make a GET request to the following url.
```
http://localhost:8091/getByRoadId?roadId=A1&direction=1
```