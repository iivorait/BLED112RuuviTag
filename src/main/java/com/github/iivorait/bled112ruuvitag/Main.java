package com.github.iivorait.bled112ruuvitag;

import org.thingml.bglib.BGAPI;
import org.thingml.bglib.BGAPITransport;
import org.thingml.bglib.samples.BLED112;

public class Main {
    
    public static void main( String[] args )
    {
        BGAPITransport bgapi = BLED112.connectBLED112(); //Open GUI selector 
        RuuviTagPacketListener listener = new RuuviTagPacketListener();
        bgapi.addListener(listener);
        BGAPI impl = new BGAPI(bgapi);
        impl.send_gap_set_scan_parameters(10, 250, 1);
        impl.send_gap_discover(1);

        while(true) {
            RuuviTagData measurement = listener.getMeasurement("c3c0f59ecbf4"); //set your tag's MAC address
            if(measurement != null) {
                System.out.println(measurement.getTimestamp() + " Temperature: " 
                        + measurement.getTemperature() + " oC");
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                break;
            }
        }
    }

}
