import java.security.InvalidParameterException;

import com.fazecast.jSerialComm.SerialPort;

public class SerialLED {
    
    SerialPort ledCom;

    int minUpdateInterval;
    long prevTime;
    
    public SerialLED(String port, int baud, int minUpdateInterval) {
        
        this.minUpdateInterval = minUpdateInterval;

        ledCom = SerialPort.getCommPort(port);

        if (!ledCom.openPort(20)) {
            throw new InvalidParameterException("Could not open port: " + port);
        }

        ledCom.setComPortParameters(baud, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY, false);

        init();
    }

    private void init() {

        //allow the arduino a moment to reset before spamming it with commands.

        try {
            Thread.sleep(2000);
        }
        catch (InterruptedException e) {
            System.out.println("LED Controller not properly initialized. First command will be ignored.");
        }

        prevTime = System.currentTimeMillis();
    }

    boolean setColor(byte R, byte G, byte B) {
        
        byte[] outBuffer = {R, G, B, '\r', '\n'};

        //waits for the minimum update interval to pass.
        while((System.currentTimeMillis() - prevTime) < minUpdateInterval);

        prevTime = System.currentTimeMillis();

        return ledCom.writeBytes(outBuffer, outBuffer.length, 0) == outBuffer.length;

    }

}