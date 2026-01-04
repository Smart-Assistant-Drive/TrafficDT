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
NOTE: leave the link empty.

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

# Next car distance
In order to know the next car distance from a specific car you should connect to the following topic:
```
trafficdt-digital-cars-digital-adapter/cars/$idCar/distanceFromNext
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