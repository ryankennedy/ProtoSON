# ProtoSON

A simple proof of concept REST service that demonstrates endpoints that
accept and consume either JSON or Protocol Buffers encoded messages.

This is accomplished by using [HTTP content negotiation](
https://www.w3.org/Protocols/rfc2616/rfc2616-sec12.html). The client specifies an
`Accept` and/or a `Content-Type` header and the server adjusts request
parsing/response rendering accordingly.

## Running Locally

To build and run locally, execute the following:

```
$ mvn compile exec:java
```

## Fetching Data

Send an HTTP GET with the appropriate `Accept` header.

### JSON

```
$ curl -H"Accept: application/json" http://localhost:8080/greetings/ryan
{
  "greeting": "Hello, ProtoSON!"
}
```

### Protocol Buffers

```
$ curl -H"Accept: application/x-protobuf" --silent http://localhost:8080/greetings/ryan | hexdump -C
  00000000  0a 10 48 65 6c 6c 6f 2c  20 50 72 6f 74 6f 53 4f  |..Hello, ProtoSO|
  00000010  4e 21                                             |N!|
  00000012
```

## Sending Data

Send an HTTP PUT (or POST) with the appropriate `Content-Type` header. You
should also send an appropriate `Accept` header if you're expecting a response
body.

### JSON

```
$ curl -XPUT -H"Accept: application/json" -H"Content-Type: application/json" --data '{"greeting": "Hello, Ryan!"}' http://localhost:8080/greetings/ryan
{
  "greeting": "Hello, Ryan!"
}⏎
```

### Protocol Buffers

```
$ curl -XPUT --silent -H"Accept: application/x-protobuf" -H"Content-Type: application/x-protobuf" --data-binary @ryan.protobuf http://localhost:8080/greetings/ryan | hexdump -C
  00000000  0a 0c 48 65 6c 6c 6f 2c  20 52 79 61 6e 21        |..Hello, Ryan!|
  0000000e
```

### Protocol Buffer Request, JSON Response

```
$ curl -XPUT -H"Accept: application/json" -H"Content-Type: application/x-protobuf" --data-binary @ryan.protobuf http://localhost:8080/greetings/ryan
{
"greeting": "Hello, Ryan!"
}
```