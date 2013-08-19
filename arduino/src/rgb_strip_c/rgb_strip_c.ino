/**
* 
* Code to read value from serial and change RGB value.
* Accepted value is (r=? OR g=? OR b=? OR red=? OR green=? OR blue=?)
* where ? is integer range from 0 to 255. Key is in case insensitive.
*
* @author Khairul
* @date 20130819
*/

#define RED_PORT 9
#define GREEN_PORT 10
#define BLUE_PORT 11

void setup(){
  // Setup all rgb pin to output mode
  pinMode(RED_PORT, OUTPUT); 
  pinMode(GREEN_PORT, OUTPUT); 
  pinMode(BLUE_PORT, OUTPUT); 
  
  Serial.begin(9600);
  Serial.write("Start Listening\n");
  analogWrite(RED_PORT, 255);
  analogWrite(GREEN_PORT, 255);
  analogWrite(BLUE_PORT, 255);
}

String getValue(String data, char separator, int index)
{
  int found = 0;
  int strIndex[] = {
    0, -1        };
  int maxIndex = data.length()-1;

  for(int i=0; i<=maxIndex && found<=index; i++){
    if(data.charAt(i)==separator || i==maxIndex){
      found++;
      strIndex[0] = strIndex[1]+1;
      strIndex[1] = (i == maxIndex) ? i+1 : i;
    }
  }

  return found>index ? data.substring(strIndex[0], strIndex[1]) : "";
}

void loop() {
  String content = "";
  char character;
  while(Serial.available()) { // Read from serial, if available, continue.
    character = Serial.read();
    content.concat(character);
  }
  if (content != "") {
    Serial.println(content);
    String ledToChange = getValue(content, '=', 0);
    String ledValueStr = getValue(content, '=', 1);
    int ledValueInt = ledValueStr.toInt();
    Serial.println("Changing led " + ledToChange + " to value " + ledValueStr);
    ledToChange.toLowerCase();
    if(ledToChange == "r" || ledToChange == "red") {
      analogWrite(RED_PORT, ledValueInt);
    } 
    else if (ledToChange == "g" || ledToChange == "green") {
      analogWrite(GREEN_PORT, ledValueInt);
    }  
    else if (ledToChange == "b" || ledToChange == "blue") {
      analogWrite(BLUE_PORT, ledValueInt);
    } 
    else if (ledToChange == "off") {
      analogWrite(RED_PORT, 0);
      analogWrite(GREEN_PORT, 0);
      analogWrite(BLUE_PORT, 0);
    }
  } 
  delay(100); // Just to be safe, we delay every 100miliseconds
}







