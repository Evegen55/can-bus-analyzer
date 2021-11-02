81, F2, FF, BB - mock bytes in can frame

# Navi pad - CAN ID 63E, 63D and ? (life probe) TODO

![**navi_pad**](https://raw.githubusercontent.com/Evegen55/can-bus-analyzer/main/src/test/resources/navi_pad.png)

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

58F 8 89 0 43 BB BB BB BB BB - ??? TODO

58F 8 89 0 4 BB BB BB BB BB - volume DOWN (-)

58F 8 89 0 44 BB BB BB BB BB - volume DOWN (-) - why - I don't know

58F 8 89 0 5 BB BB BB BB BB - mute/unmute, phone call on/off

58F 8 89 0 1 BB BB BB BB BB - top right triangle button (Radiosat uses it for phone calls, but in future it could be changed)

58F 8 89 0 2 BB BB BB BB BB - change audio source

58F 8 89 0 6 BB BB BB BB BB - lowest lever-button (Radiosat uses it for phone calls, but in future it could be changed)

# Radiosat audio/radio tuner - CAN ID 5B8 and 3DF

3DF 8 28 8 81 81 81 81 81 81 - ENABLE sign 4 times right after power on

5B8 8 31 81 81 81 81 81 81 81 - READY sign 1 times 500 ms after enable sign

5B8 8 10 81 81 81 81 81 81 81 - WOKE UP life probe each 500 ms till switch off

3DF 8 28 F 81 81 81 81 81 81 - WAKEUP sign after button on radiosat??? TODO chek it

3DF 8 28 0 81 81 81 81 81 81 - SLEEP sign 1 times each 100 ms after sleep sign radiosat??? TODO chek it

5B8 8 30 81 81 81 81 81 81 81 - SLEEP life probe each 500 ms till WAKEUP or SWITCH OFF on radiosat??? TODO chek it


# UNKNOWN devices
568 8 0 0 69 0 46 2F BB BB - very often, life probe??? TODO

1C1 8 3 60 0 0 FF FF FF FF - very rare TODO

1C1 8 2 62 F FF FF FF FF FF - very rare TODO

