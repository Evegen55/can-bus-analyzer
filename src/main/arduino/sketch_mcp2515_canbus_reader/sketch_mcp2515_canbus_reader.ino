#include <SPI.h>
#include <mcp2515.h>

struct can_frame canMsg;
MCP2515 mcp2515(10);


void setup() {
  Serial.begin(9600);
  
  mcp2515.reset();
  mcp2515.setBitrate(CAN_500KBPS,MCP_8MHZ);
  mcp2515.setNormalMode();
  
  Serial.println("------- Reading from CAN BUS NO TIME FLAG ----------");
  Serial.println("ID  DLC   DATA");
}

//NOTE that to send data back to CAN-bus it needs to add 0x preffix to data
void loop() {
  if (mcp2515.readMessage(&canMsg) == MCP2515::ERROR_OK) {
    Serial.print(canMsg.can_id, HEX); // print ID without 0x preffix
    Serial.print(" "); 
    Serial.print(canMsg.can_dlc, HEX); // print DLC without 0x preffix
    Serial.print(" ");
    
    for (int i = 0; i<canMsg.can_dlc; i++)  {  // print the data without 0x preffix
      Serial.print(canMsg.data[i],HEX);
      Serial.print(" ");
    }

    Serial.println();      
  }

}
