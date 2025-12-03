# TrafficDT
This represents the environment for traffic digital twins.

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
### Change lane request
Topic: trafficdt-physical-%idDT%/changeLaneRequest
```json
{
  "id": "idCar - String",
  "destinationLane": "destinationLane - Int"
}
```

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

## Actions
### Change lane action
Topic: trafficdt-physical-%idDT%/changeLaneAction
Body:
```json
{
  "idCar": "",
  "lane": "",
  "canChange": ""
}
```