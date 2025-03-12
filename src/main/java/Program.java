
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InaccessibleObjectException;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import edu.wpi.first.networktables.IntegerArraySubscriber;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTablesJNI;
import edu.wpi.first.networktables.StringSubscriber;
import edu.wpi.first.util.CombinedRuntimeLoader;
import edu.wpi.first.util.WPIUtilJNI;

/**
 * Program
 */
public class Program {

    static SerialLED leds;

    static NetworkTableInstance   inst;     
    static NetworkTable           ledTable; 
    static IntegerArraySubscriber color;    
    static StringSubscriber       pattern;  

    static long[] currentColor = {0,0,128};
    static String currentPattern = "solid";

    static void updateLeds() {

        if (inst.isConnected()) {

            currentColor = color.get();
        
            if (currentColor.length == 3) {
                leds.setColor((byte)currentColor[0], (byte)currentColor[1], (byte)currentColor[2]);
            }
        }

        else {
            leds.setColor((byte)5, (byte)0, (byte)0);
        }
    }

    public static void main(String[] args) throws IOException {

        //WPILIB Library Loading Weirdness.
        NetworkTablesJNI.Helper.setExtractOnStaticLoad(false);
        WPIUtilJNI.Helper.setExtractOnStaticLoad(false);

        CombinedRuntimeLoader.loadLibraries(Program.class, "wpiutiljni", "ntcorejni");
        //=====================================================================================================

        inst     = NetworkTableInstance.getDefault();
        ledTable = inst.getTable("ledControl");
        color    = ledTable.getIntegerArrayTopic("color").subscribe(new long[] {0,0,128});
        pattern  = ledTable.getStringTopic("pattern").subscribe("solid");

        File config = new File("LEDBackend.cfg");

        String portDesc;

        if (!config.canRead())
            throw new InaccessibleObjectException("Cannot read LEDConfig.cfg");

        Scanner reader = new Scanner(config);

        portDesc = reader.nextLine();

        leds = new SerialLED(portDesc, 57600, 10);

        inst.startDSClient(); //Assume we are running on the driver station and attempt to connect to the robot.

        
        //Instantiate LED Controller

        Timer updateTimer = new Timer();

        updateTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                updateLeds();
            }
        }, 0, 100);
    }
}
