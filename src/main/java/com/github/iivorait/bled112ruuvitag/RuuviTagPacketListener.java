package com.github.iivorait.bled112ruuvitag;

import java.util.HashMap;
import org.thingml.bglib.BGAPIPacket;
import org.thingml.bglib.BGAPIPacketReader;
import org.thingml.bglib.BGAPITransportListener;

public class RuuviTagPacketListener implements BGAPITransportListener {

    private HashMap<String, RuuviTagData> data = new HashMap<>();
    
    public RuuviTagData getMeasurement(String macAddress) {
        return this.data.get(macAddress);
    }
    
    @Override
    public void packetSent(BGAPIPacket packet) {
    }
    
    @Override
    public void packetReceived(BGAPIPacket packet) {
        /*
        An example of the packet payload (see packet.toString())
             ---MAC address---                     mnfg id  ver  humid  temp  tempf  press   accx   accy  accz  batt
        c6 2 f4 cb 9e f5 c0 c3 1 ff 15 2 1 6 11 ff 99 4     3    55     15    8      c3 32   ff ee  0 2   4 5   c 1f
        */
        if(packet.getMsgType() != 1 || packet.getClassID() != 6) {
            return;
        }
        
        BGAPIPacketReader reader = packet.getPayloadReader();
        try {
            reader.r_uint16();
            String macAddress = this.readMACAddress(reader);
            reader.r_uint32();
            reader.r_uint32();
            
            if(reader.r_uint16() != 0x0499) { //check manufacturer ID = 0x0499
                return;
            }
            
            RuuviTagData measurement = new RuuviTagData();
            measurement.setVersion(reader.r_uint8());
            switch(measurement.getVersion()) {
                case 3:
                    this.readVersion3Packet(reader, measurement);
                    break;
                default:
                    System.err.println("Protocol version " 
                            + measurement.getVersion() + " not implemented");
                    return;
            
            }
            measurement.setTimestamp(System.currentTimeMillis());
            this.data.put(macAddress, measurement);
            
        } catch (Exception e) {}
    }

    private String readMACAddress(BGAPIPacketReader reader) {
        StringBuilder macAddress = new StringBuilder();
        for(int i = 0; i < 6; i++) {
            macAddress.insert(0, Integer.toHexString(reader.r_uint8()));
        }
        return macAddress.toString();
    }
    
    private int readTwoByteValue(BGAPIPacketReader reader) {
        return (reader.r_uint8() << 8) + reader.r_uint8();
    }
    
    private int readTwoByteSignedValue(BGAPIPacketReader reader) {
        int value = this.readTwoByteValue(reader);
        if(value > 32768) {
            return value - 65536;
        }
        return value;
    }
    
    private void readVersion3Packet(BGAPIPacketReader reader, RuuviTagData measurement) {
        measurement.setHumidity(((double) reader.r_uint8()) / 2);
        double temperature = reader.r_int8() + ( (double) reader.r_int8() ) / 100;
        measurement.setTemperature(temperature);
        measurement.setPressure(50000 + this.readTwoByteValue(reader));
        measurement.setAccelerationX(this.readTwoByteSignedValue(reader));
        measurement.setAccelerationY(this.readTwoByteSignedValue(reader));
        measurement.setAccelerationZ(this.readTwoByteSignedValue(reader));
        measurement.setBattery(this.readTwoByteValue(reader));
    }
}
