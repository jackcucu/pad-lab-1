## Description of the `brk` protocol

The protocol for the system `brk` is based on JSON.

`brk` automatically creates new queue on publisher request and based on field transporting_type makes queue persistent or not.

`transporting_type` is type of message transporting. It must be one of the following:
- `PERSISTENT` - makes transport persistent;
- `NON_PERSISTENT` - makes transport non persistent;

`client_type` is type of client. It must be one of the following:
- `PUBLISHER` - define a subscriber;
- `SUBSCRIBER` - define a publisher;

`payload` is payload of sent/received message.

`topic` is pattern for topic recognition.

`broken` is a flag that notify if connection is broken or not, if broken flag is present in payload will be last and will message.

`command` is type of action to be executed by the `notibroker` for the `command` message.

`notibroker` exposes the following commands:
- `send` - send a message to the queue;
- `subscribe` - subscribe to queue(s) topic(s) based on regex;
- `disconnect` - inform broker about planned disconnect of a subscriber;
- `keep_alive` - ask broker to keep the connection alive;

Examples of the structure for the `brk` command messages (dicts dumped to json):
```json
{
    "transporting_type": "PERSISTENT",
    "client_type": "PUBLISHER",
    "topic": "1.1.*.10",
    "payload": "<message>"
}
```

```json
{
    "client_type": "SUBSCRIBER",
    "topic": "1.1.*.10"
}
```

```json
{
    "client_type": "PUBLISHER",
    "payload": "<last will>",
    "broken" : true
}
```

```json
{
    "client_type": "SUBSCRIBER",
    "payload": "<last will>",
    "broken" : true
}
```
