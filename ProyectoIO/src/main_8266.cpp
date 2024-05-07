#include "RestClient.h"
#include "ArduinoJson.h"
#include <ESP8266WiFi.h>
#include <PubSubClient.h>
#include <ESP8266HTTPClient.h>

int test_delay = 5000; //so we don't spam the API
boolean describe_tests = true;

RestClient client = RestClient("192.168.237.42", 8085);

String serverName = "http://localhost/";
HTTPClient http;

#define STASSID "OPPO A78 5G"
#define STAPSK  "permor455"

// MQTT configuration
WiFiClient espClient;
PubSubClient client2(espClient);


// Server IP, where de MQTT broker is deployed
const char *MQTT_BROKER_ADRESS = "192.168.1.154";
const uint16_t MQTT_PORT = 1883;

// Name for this MQTT client
const char *MQTT_CLIENT_NAME = "ArduinoClient_1";

// callback a ejecutar cuando se recibe un mensaje
// en este ejemplo, muestra por serial el mensaje recibido
void OnMqttReceived(char *topic, byte *payload, unsigned int length)
{
  Serial.print("Received on ");
  Serial.print(topic);
  Serial.print(": ");

  String content = "";
  for (size_t i = 0; i < length; i++)
  {
    content.concat((char)payload[i]);
  }
  Serial.print(content);
  Serial.println();
}

// inicia la comunicacion MQTT
// inicia establece el servidor y el callback al recibir un mensaje
void InitMqtt()
{
  client2.setServer(MQTT_BROKER_ADRESS, MQTT_PORT);
  client2.subscribe("topic_1");
  client2.setCallback(OnMqttReceived);
}

//Setup
void setup()
{
  Serial.begin(9600);
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(STASSID);

  /* Explicitly set the ESP8266 to be a WiFi-client, otherwise, it by default,
     would try to act as both a client and an access-point and could cause
     network-issues with your other WiFi-devices on your WiFi-network. */
  WiFi.mode(WIFI_STA);
  WiFi.begin(STASSID, STAPSK);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());
  Serial.println("Setup!");
}

String response;

String serializeBodySen(int idSensor, int placaid,String nombre, double val)
{
  StaticJsonDocument<200> doc;

  // StaticJsonObject allocates memory on the stack, it can be
  // replaced by DynamicJsonDocument which allocates in the heap.
  //
  // DynamicJsonDocument  doc(200);

  // Add values in the document
  //
  doc["id"] = idSensor;
  doc["placaid"] = placaid;
  doc["nombre"] = nombre;
  // Add an array.
  //
  doc["valor"]=val;

  // Generate the minified JSON and send it to the Serial port.
  //
  String output;
  serializeJson(doc, output);
  // The above line prints:
  // {"sensor":"gps","time":1351824120,"data":[48.756080,2.302038]}

  // Start a new line
  //Serial.println(output);

  // Generate the prettified JSON and send it to the Serial port.
  //
  //serializeJsonPretty(doc, output);
  // The above line prints:
  // {
  //   "sensor": "gps",
  //   "time": 1351824120,
  //   "data": [
  //     48.756080,
  //     2.302038
  //   ]
  // }
  return output;
}

String serializeBodyAct(int idSensor, int placaid,String nombre, int estado,String tipo)
{
  StaticJsonDocument<200> doc;

  // StaticJsonObject allocates memory on the stack, it can be
  // replaced by DynamicJsonDocument which allocates in the heap.
  //
  // DynamicJsonDocument  doc(200);

  // Add values in the document
  //
  doc["id"] = idSensor;
  doc["placaid"] = placaid;
  doc["nombre"] = nombre;

  // Add an array.
  //
  doc["estado"]=estado;
  doc["tipo"]=tipo;

  // Generate the minified JSON and send it to the Serial port.
  //
  String output;
  serializeJson(doc, output);
  return output;
}

void test_status(int statusCode)
{
  delay(test_delay);
  if (statusCode == 200 || statusCode == 201)
  {
    Serial.print("TEST RESULT: ok (");
    Serial.print(statusCode);
    Serial.println(")");
  }
  else
  {
    Serial.print("TEST RESULT: fail (");
    Serial.print(statusCode);
    Serial.println(")");
  }
}

void deserializeBody(String responseJson){
  if (responseJson != "")
  {
    StaticJsonDocument<200> doc;

    //char json[] =
    //    "{\"sensor\":\"gps\",\"time\":1351824120,\"data\":[48.756080,2.302038]}";

    // Deserialize the JSON document
    DeserializationError error = deserializeJson(doc, responseJson);

    // Test if parsing succeeds.
    if (error)
    {
      Serial.print(F("deserializeJson() failed: "));
      Serial.println(error.f_str());
      return;
    }

    // Fetch values.
    //
    // Most of the time, you can rely on the implicit casts.
    // In other case, you can do doc["time"].as<long>();
    const char *sensor = doc["id"];
    long time = doc["time"];
    double valor = doc["valor"];

    // Print values.
    Serial.println(sensor);
    Serial.println(time);
    Serial.println(valor, 6);
  }
}

void test_response()
{
  Serial.println("TEST RESULT: (response body = " + response + ")");
  response = "";
}

void describe(char *description)
{
  if (describe_tests)
    Serial.println(description);
}

void GET_tests()
{
  describe("Test GET with path");
  test_status(client.get("/api/sensores", &response));
  test_response();

  describe("Test GET with path and response");
  test_status(client.get("/api/placas/sensores/last/1", &response));
  test_response();

  describe("Test GET with params");
  test_status(client.get("/api/sensores/1", &response));
  test_response();
}

void POST_tests_sen()
{
  String post_body = serializeBodySen(1,1,"sen1", random(200, 400)/10);
  describe("Test POST with path and body and response");
  test_status(client.post("/api/sensores/new", post_body.c_str(), &response));
  test_response();
}

void POST_tests_Act()
{
  String post_body = serializeBodyAct(1,1,"act3",1, "led");
  describe("Test POST with path and body and response");
  test_status(client.post("/api/actuadores/new", post_body.c_str(), &response));
  test_response();
}


// Run the tests!
void loop()
{
  GET_tests();
  POST_tests_sen();
  InitMqtt();
}
