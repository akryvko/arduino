#include<Servo.h>
#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include "wifi_password.h"

class Timer{
  private:
    long initialDelayMs;
    long nextOccuranceMs;
    long rateMs;

  public:
    Timer(long initialDelayMs, long rateMs){
      this->initialDelayMs = initialDelayMs;
      this->rateMs = rateMs;
      this->nextOccuranceMs = LONG_MAX;
    }
  
    bool isFired(){
      if (nextOccuranceMs <= millis()) {
          nextOccuranceMs += rateMs;
          // rewing multiple steps in future if needed
          isFired();
          return true;
      } else {
        return false;
      }
    }
    
    void start(){
      this->nextOccuranceMs = millis() + this->initialDelayMs;
    }
};

const char* ssid = "KabelBox-4B98"; 
const char* password = WIFI_PASSWORD;

Servo servo;
Timer servoTimer = Timer(2000, 550);
bool isServoActivated = false;
bool servoStartPosition = true;

Timer statusTimer = Timer(3000, 4000);

void setup() {
  Serial.begin(9600);

  connectToInternet();

  servo.attach(0);
  delay(100);    
  servo.write(180);  

  servoTimer.start();
  statusTimer.start();
}

void loop() {
  if(statusTimer.isFired()){
    String servoStatus = getServoStatus();
    if (servoStatus.indexOf("start") >= 0) {
      isServoActivated = true;
    } else {
      isServoActivated = false;
    }
  }

  if (servoTimer.isFired() && isServoActivated) {
    servoStartPosition = !servoStartPosition;
    Serial.println("Servo moved");
  }

  if (isServoActivated){
    // activate servoß
    if (servoStartPosition) {
      servo.write(60);  
    } else {
      servo.write(90 + 60);
    }
  } else {
    // stop 
    servo.write(180);
  }
  
}

String getServoStatus(){
  Serial.println("Getting servo status");
  if (WiFi.status() == WL_CONNECTED) { 
    HTTPClient http;     
    http.begin("http://fathomless-cove-40821.herokuapp.com/kabachok/rock");
    int httpCode = http.GET();                                
    if (httpCode == 200) {
      String payload = http.getString();
      Serial.print("Got servo status: ");
      Serial.println(payload);
      return payload;
    } 
    http.end();  
  } else {
    connectToInternet();
  }

  return "n/a";
}

void connectToInternet(){
  delay(10);
 
  Serial.println();
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(ssid);
  WiFi.hostname("Name");
  WiFi.begin(ssid, password);
 
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.println("WiFi connected");
 
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());
}
