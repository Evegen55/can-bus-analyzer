#include <SPI.h>
#include <mcp2515.h>

struct can_frame canMsg_1; // 3DF 8 28 8 81 81 81 81 81 81 при включении первые четыре раза
struct can_frame canMsg_2; // 5B8 8 31 81 81 81 81 81 81 81 далее через полсекунды один раз
                           // 5B8 8 10 81 81 81 81 81 81 81 далее через полсекунды постоянно

MCP2515 mcp2515(10);

void setup() 
{
  while (!Serial);
  Serial.begin(9600);
  
  SPI.begin();
  
  mcp2515.reset();
  mcp2515.setBitrate(CAN_500KBPS,MCP_8MHZ);
  mcp2515.setNormalMode();

  canMsg_1.can_id  = 0x3DF;
  canMsg_1.can_dlc = 0x8;
  canMsg_1.data[0] = 0x28;
  canMsg_1.data[1] = 0x8;
  canMsg_1.data[2] = 0x81;
  canMsg_1.data[3] = 0x81;
  canMsg_1.data[4] = 0x81;
  canMsg_1.data[5] = 0x81;
  canMsg_1.data[6] = 0x81;
  canMsg_1.data[7] = 0x81;

  canMsg_2.can_id  = 0x5B8;
  canMsg_2.can_dlc = 0x8;
  canMsg_2.data[0] = 0x31;
  canMsg_2.data[1] = 0x81;
  canMsg_2.data[2] = 0x81;
  canMsg_2.data[3] = 0x81;
  canMsg_2.data[4] = 0x81;
  canMsg_2.data[5] = 0x81;
  canMsg_2.data[6] = 0x81;
  canMsg_2.data[7] = 0x81;
  
  mcp2515.sendMessage(&canMsg_1);
  mcp2515.sendMessage(&canMsg_1);
  mcp2515.sendMessage(&canMsg_1);
  mcp2515.sendMessage(&canMsg_1);
  delay(500);
  mcp2515.sendMessage(&canMsg_2);

  
// Double check whats going on
//  for (int i = 0; i<canMsg_1.can_dlc; i++)  {  // print the data
//      Serial.print(canMsg_1.data[i],HEX);
//      Serial.print(" ");
//    }
// 28 8 81 81 81 81 81 81 


// by using Raspberry candump command:
//pi@raspberrypi:~ $ candump can0
//  can0  3DF   [8]  28 08 81 81 81 81 81 81
}

void loop() {
  canMsg_2.data[0] = 0x10;
  delay(500);
  mcp2515.sendMessage(&canMsg_2);
}
