#include <SoftwareSerial.h>

SoftwareSerial mySerial(10, 11);      // RX, TX of RFId reader
String voice,voice1;                  //variables for the incoming serial data
char flag,flag1,flagx,flagy,flagz,flagacci;
int x2,y2,z2;

void setup() {
  Serial.begin(9600);
  pinMode(13, OUTPUT);                //buzzer
  mySerial.begin(9600);
  }

void loop() {
  while (mySerial.available()){
    char d = mySerial.read();        //read the incoming data from the RFId reader
    if(d=='\r') {
      break;}                       //Exit the loop when the # is detected after the word
    voice1 += d;                    //Store the RFId to 'voice1'
  }
    if (voice1.length() > 0){
      Serial.println(voice1); 
      if(voice1 == "08017875"){    //1st RFId number
        flag1=1;
      } 
      if(voice1 == "08018078"){    //2nd RFid number
        flag1=2;
      }
    }
    voice1=""; 
    while (Serial.available()){    //Check if there is an available byte to read
      delay(10);                    
      char c = Serial.read();      //Conduct a serial read, data coming from the RPi
      if(c=='\r') {
        break;}                   //Exit the loop when the # is detected after the word
      voice += c;                 //Store the data from RPi to 'voice'
    }
    if (voice.length() > 0) {
      Serial.println(voice); 
      if(voice == "drowsy") {
        flag=1;
        digitalWrite(13, HIGH);   //set the buzzer
        delay(1000);
        if((flag==1) && (flag1==1)) {  //driver1 is drowsy
          Serial.println("1drowsy");
        }
        if((flag==1) && (flag1==2)) {  //driver2 is drowsy
          Serial.println("2drowsy");
        }
      } 
      else {
        flag=0;
        digitalWrite(13,LOW);
      }
    voice="";}                //Reset the variable after initiating
//accelerometer
  int x = analogRead(A0);     //read the x axis value of the accelerometer
  delay(100);
  int x1 = analogRead(A0);
  if(x>x1)
    x2=x-x1;                  //find the difference between consecutive readings
  else
    x2=x1-x;
  if(x2>50)                  //set flag if the difference>50
    flagx=1;
  int y = analogRead(A1);
  delay(100);
  int y1 = analogRead(A1);
  if(y>y1)
    y2=y-y1;
  else
    y2=y1-y;
  if(y2>50)
    flagy=1;
  int z = analogRead(A2);
  delay(100);
  int z1 = analogRead(A2);
  if(z>z1)
    z2=z-z1;
  else
    z2=z1-z;
  if(z2>50)
    flagz=1;
  if(flagx||flagy||flagz==1){        //if any of the flags for x, y, or z axes are set, reset them and set flagacci high
     flagx=0;flagy=0;flagz=0;
     flagacci=1;
     if((flagacci==1) && (flag1==1)){ //driver1 has met with an accident an accident
        Serial.println("1accident");
        flagacci=0;
     }
     else if((flagacci==1) && (flag1==2)){ //driver2 has met with an accident
        Serial.println("2accident");
        flagacci=0;
     }
  }
}



 
