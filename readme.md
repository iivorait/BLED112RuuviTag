# Silicon Labs / Bluegiga BLED112 RuuviTag Java Sample

This sample code implements the RuuviTag Data Format 3 Protocol Specification (RAWv1) in Java when used with a virtual COM port of a BLE(D)112 Bluetooth adapter (not generic BLE). 
Formats 2, 4 and 5 are not implemented, but are straightforward to implement into RuuviTagPacketListener.java (pull requests are welcome).

See [https://github.com/ruuvi/ruuvi-sensor-protocols](https://github.com/ruuvi/ruuvi-sensor-protocols)
Uses the [Java implementation of the BGAPI binary protocol for Bluegiga BLE112 Bluetooth low energy modules](https://github.com/SINTEF-9012/bglib)