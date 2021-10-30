#include <SPI.h>          //Library for using SPI Communication 
#include <mcp2515.h>      //Library for using CAN Communication

struct can_frame canMsg;
MCP2515 mcp2515(10);

void setup() 
{
  while (!Serial);
  Serial.begin(9600);
  
  SPI.begin(); //Begins SPI communication
  
  mcp2515.reset();//see MCP2515 datasheet
  mcp2515.setBitrate(CAN_500KBPS,MCP_8MHZ); //Sets CAN at speed 500KBPS and Clock 8MHz
  mcp2515.setNormalMode();
}

void loop() 
{

  canMsg.can_id  = 0x3DF;
  canMsg.can_dlc = 0x8;
  canMsg.data[0] = 0x28;
  canMsg.data[1] = 0x8;
  canMsg.data[2] = 0x81;
  canMsg.data[3] = 0x81;
  canMsg.data[4] = 0x81;
  canMsg.data[5] = 0x81;
  canMsg.data[6] = 0x81;
  canMsg.data[7] = 0x81;


 Serial.print(canMsg.can_id, HEX); // print ID without 0x preffix
 Serial.print(" "); 
 Serial.print(canMsg.can_dlc, HEX); // print DLC without 0x preffix
 Serial.print(" ");
    
 for (int i = 0; i<canMsg.can_dlc; i++)  {  // print the data without 0x preffix
  Serial.print(canMsg.data[i],HEX);
  Serial.print(" ");
 }
 Serial.println();
 delay(500);
}
