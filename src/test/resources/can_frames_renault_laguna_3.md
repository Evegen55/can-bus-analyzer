### CAN frame structure

[canid] [number of frames] [0] [1] [2] [3] [4] [5] [6] [7]

111        8               D1  FA  28  2   CF  0   BB  BB

mock bytes in can frame 81, F2, FF, BB

# Navi pad - CAN ID 63E, 63D and 63F

![**navi_pad**](https://raw.githubusercontent.com/Evegen55/can-bus-analyzer/main/src/test/resources/navi_pad.png)

### life probe, whole device - CAN ID 63F

63F 8 4 5 0 1 2 0 F2 F2 - each 500 ms right after power on, LIFE PROBE

## joystick - CAN ID 63E
63E 8 0 5 F2 F2 F2 F2 F2 F2 - turn left 1 step

63E 8 0 6 F2 F2 F2 F2 F2 F2 - turn right 1 step

63E 8 1 0 F2 F2 F2 F2 F2 F2 - pressed

### joystick tilt and push
63E 8 10 0 F2 F2 F2 F2 F2 F2 - right

63E 8 20 0 F2 F2 F2 F2 F2 F2 - top

63E 8 30 0 F2 F2 F2 F2 F2 F2 - left

63E 8 40 0 F2 F2 F2 F2 F2 F2 - bottom

## buttons around joystick - CAN ID 63D  (9 pieces)
63D 8 0 F2 F2 F2 F2 F2 F2 F2 - |__ (top left) button

63D 8 1 F2 F2 F2 F2 F2 F2 F2 - Back (top center) button

63D 8 2 F2 F2 F2 F2 F2 F2 F2 - __| (top left) button

63D 8 3 F2 F2 F2 F2 F2 F2 F2 - INFO route (center right) button

63D 8 4 F2 F2 F2 F2 F2 F2 F2 - MENU set (bottom right) button

63D 8 5 F2 F2 F2 F2 F2 F2 F2 - LIGHT dark (bottom center right) button

63D 8 6 F2 F2 F2 F2 F2 F2 F2 - REPEAT mute (bottom center left) button

63D 8 7 F2 F2 F2 F2 F2 F2 F2 - DEST home (bottom left) button

63D 8 8 F2 F2 F2 F2 F2 F2 F2 - MAP 2D 3D (center left) button

# Audio control below steering wheel - CAN ID 58F
![**audio_control_below_steering_wheel**](https://raw.githubusercontent.com/Evegen55/can-bus-analyzer/main/src/test/resources/audio_control_below_steering_wheel.png)

58F 8 80 2 BB BB BB BB BB BB - life probe each 100(?) ms (very often, most often signal in multimedia bus) from open car till switch off full car

58F 8 80 0 BB BB BB BB BB BB - ??? TODO

## Buttons (9 pieces)
58F 8 89 0 0 BB BB BB BB BB - ролик нажат

58F 8 89 1 1 BB BB BB BB BB - ролик прокручен вниз

58F 8 89 1 41 BB BB BB BB BB - ролик прокручен вверх

58F 8 89 0 3 BB BB BB BB BB - volume UP (+)

58F 8 89 0 43 BB BB BB BB BB - volume UP (+) LONG TIME

58F 8 89 0 4 BB BB BB BB BB - volume DOWN (-)

58F 8 89 0 44 BB BB BB BB BB - volume DOWN (-) LONG TIME

58F 8 89 0 5 BB BB BB BB BB - mute/unmute, phone call on/off

58F 8 89 0 1 BB BB BB BB BB - top right triangle button (Radiosat uses it for phone calls, but in future it could be changed)

58F 8 89 0 2 BB BB BB BB BB - change audio source

58F 8 89 0 6 BB BB BB BB BB - lowest lever-button (Radiosat uses it for phone calls, but in future it could be changed)

# Radiosat audio/radio tuner - CAN ID 5B8 and 3DF

## Algorithm to show that Radiosat is enabled and life, implemented in sketch_mcp2515_carminat_stub.ino

3DF 8 28 8 81 81 81 81 81 81 - ENABLE sign 4 times right after power on

5B8 8 31 81 81 81 81 81 81 81 - READY sign 1 times 500 ms after enable sign

5B8 8 10 81 81 81 81 81 81 81 - LIFE PROBE each 500 ms till switch off


## POSSIBLE algorithm to sleep multimedia system ??? TODO chek it

3DF 8 28 F 81 81 81 81 81 81 - WAKEUP sign after button on radiosat??? TODO chek it

3DF 8 28 0 81 81 81 81 81 81 - SLEEP sign 1 times each 100 ms after sleep sign radiosat??? TODO chek it

5B8 8 30 81 81 81 81 81 81 81 - SLEEP PROBE each 500 ms till WAKEUP or SWITCH OFF on radiosat??? TODO chek it

## Temperature and lights - CAN ID 558

558 8 D1 AF 29 2 CF 0 BB BB - typical frame

### byte 0: lights (and, possible, engine state) - to switch on/off button illumination

D1 - lights off

D5 - lights on

С1 - ??

С5 - ??

С0 - ??

### bytes 1, 2: temperature

FA  28 - 0 degree C negative (-0&deg;C), 28 HEX = 40 decimal (PROBABLE, check it)

AF 28 - 0 degree C positive (+0&deg;C), 28 HEX = 40 decimal

AF 29 - 1 degree C positive (+1&deg;C), 29 HEX = 41 decimal

AF 2A - 2 degrees C positive (+2&deg;C), 2A HEX = 42 decimal

AF 2B - 3 degrees C positive (+3&deg;C), 2B HEX = 43 decimal

AF 2C - 4 degrees C positive (+4&deg;C), 2C HEX = 44 decimal

AF 2D - 5 degrees C positive (+5&deg;C), 2D HEX = 45 decimal

AF 2E - 6 degrees C positive (+6&deg;C), 2E HEX = 46 decimal

AF 2F - 7 degrees C positive (+7&deg;C), 2F HEX = 47 decimal

AF 30 - 8 degrees C positive (+8&deg;C), 30 HEX = 48 decimal

AF 31 - 9 degrees C positive (+9&deg;C), 31 HEX = 49 decimal

AF 32 - 10 degrees C positive (+10&deg;C), 31 HEX = 49 decimal

AF 3A - 12 degrees C positive (+12&deg;C), 3A HEX = 58 decimal

558 8 FC FF FF 0 CF 0 BB BB - unknown, appears if navipad disconnected from bus, FF is mock data, meaning probable is health-chek

# Radiosat and TomTom text data (track name, radio frequency, radiosat settings etc.) - CAN ID 121, 521

## can id 121

121 8 10 27 25 41 13 1 20 80 - ??? TODO

121 8 10 E 20 0 0 0 0 0 0 - ??? TODO

121 8 10 E 20 0 C 42 0 0 - ??? TODO

121 8 21 AF 2 0 0 1 21 0 - ??? TODO

121 8 21 AB 2 0 0 1 21 0 - ??? TODO

121 8 21 AB 1 0 0 1 21 0 - ??? TODO

121 8 10 E 20 0 4 42 0 0 - ??? TODO

121 8 22 E1 81 81 81 81 81 81 - ??? TODO

121 8 10 E 20 0 4 42 0 0 - ??? TODO

121 8 3 31 0 0 81 81 81 81 - ??? TODO

## can id 521

521 8 30 1 0 FF FF FF FF FF - ??? TODO, start or stop block of data as can frames, or accept from tomtom

521 8 74 FF FF FF FF FF FF FF - ??? TODO, start or stop block of data as can frames, or accept from tomtom


# UNKNOWN devices and can-frames

### can id 1C1  - very rare signs, only if Radiosat connected

1C1 8 3 60 0 0 FF FF FF FF - very rare TODO

1C1 8 2 62 F FF FF FF FF FF - very rare TODO

### can id 3CF

3CF 8 22 0 FF FF FF FF FF FF - very rare TODO

### can id 548

548 8 0 0 3 0 0 0 BB BB - very often, ??? TODO

548 8 0 0 1 0 0 0 BB BB

### can id 568

568 8 0 0 69 0 46 2F BB BB - approx each 100 ms, very often, life probe??? TODO

### can id 578

578 8 18 9F AC 10 0 BB BB BB - ??? TODO

578 8 19 0 4F D0 0 BB BB BB

578 8 19 0 4F D0 4 BB BB BB

578 8 19 0 4F D0 5 BB BB BB

### can id 588

588 8 0 BB F 0 0 0 BB BB - ??? TODO

588 8 1 4D 1B 3 0 0 BB BB - ??? TODO

588 8 1 4B 1B 0 0 0 BB BB - ??? TODO

588 8 1 44 1B 0 0 0 BB BB - ??? TODO

588 8 1 45 1B 0 0 0 BB BB - ??? TODO

TODO - something appears if navipad disconnected from bus

### can id 57F

57F 8 0 FF FF FF FF FF FF FF - each 500 ms right after power on, but what the device??? LIFE PROBE ?? TODO

### can id 5A8

5A8 8 7 FF FF FF FF 4 0 BB - ??? TODO, unusual mock bytes of two types in one frame

5A8 8 5 7F DF 7F F8 5 0 BB - ??? TODO

5A8 8 5 7F DD 7F F8 5 0 BB - ??? TODO

### can id 5D8, unusual length of frame of 5, appears only when TomTom enabled

5D8 5 0 4 7C 3 FF - ??? TODO

5D8 5 0 7 FF 3 FF - ??? TODO

5D8 5 0 4 94 3 FF - ??? TODO

5D8 5 0 4 91 3 FF - ??? TODO

5D8 5 0 4 87 3 FF - ??? TODO

5D8 5 0 4 A5 3 FF - ??? TODO

5D8 5 0 4 A4 3 FF - ??? TODO

### can id 5E7

5E7 8 0 0 0 0 BB BB BB BB - ??? TODO


# Candidates for unknown devices

## Light sensor - changes UI automatically 
## Speedometer - warns about speed limit ?
## Rain sensor ?