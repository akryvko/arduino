
const int LEFT_FORWARD_PIN = 15;
const int LEFT_BACKWARD_PIN = 13;

const int RIGHT_FORWARD_PIN = 12;
const int RIGHT_BACKWARD_PIN = 14;

const long MK = 1000000;

long initMicros;

void setup() {
  pinMode(LEFT_FORWARD_PIN, OUTPUT);
  pinMode(LEFT_BACKWARD_PIN, OUTPUT);
  pinMode(RIGHT_BACKWARD_PIN, OUTPUT);
  pinMode(RIGHT_FORWARD_PIN, OUTPUT);

  analogWrite(LEFT_FORWARD_PIN, 0);
  analogWrite(RIGHT_FORWARD_PIN, 0);
  analogWrite(LEFT_BACKWARD_PIN, 0);
  analogWrite(RIGHT_BACKWARD_PIN, 0);

  initMicros = micros();

  Serial.begin(9600);
}

boolean w1 = false;
boolean w2 = false;
boolean w3 = false;

void loop() {

  if (isNowBetween(3 * MK, 4 * MK)){
    forward(900);
  }

  if (isNowBetween(4 * MK, 5 * MK)){
    backward(900);
  }

  if (isNowAfter(5 * MK)){
    stop();
  }

  delay(1);
}

boolean isNowAfter(long startMicro){
  return micros() > (startMicro + initMicros);
}

boolean isNowBefore(long endMicro){
  return micros() < (endMicro + initMicros);
}

boolean isNowBetween(long startMicro, long endMicro){
  return isNowAfter(startMicro) && isNowBefore(endMicro);
}

void forward(int speed){
  analogWrite(LEFT_FORWARD_PIN, speed);
  analogWrite(RIGHT_FORWARD_PIN, speed);
  
  analogWrite(RIGHT_BACKWARD_PIN, 0);
  analogWrite(LEFT_BACKWARD_PIN, 0);
}

void backward(int speed){
  analogWrite(LEFT_FORWARD_PIN, 0);
  analogWrite(RIGHT_FORWARD_PIN, 0);
  
  analogWrite(RIGHT_BACKWARD_PIN, speed);
  analogWrite(LEFT_BACKWARD_PIN, speed);
}

void forwardLeft(int speed, int radius){
  analogWrite(LEFT_FORWARD_PIN, speed * ((double)radius / 100));
  analogWrite(RIGHT_FORWARD_PIN, speed);
  
  analogWrite(RIGHT_BACKWARD_PIN, 0);
  analogWrite(LEFT_BACKWARD_PIN, 0);
}

void stop(){
  forward(0);
}
