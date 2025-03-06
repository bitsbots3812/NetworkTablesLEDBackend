
import edu.wpi.first.networktables.IntegerArraySubscriber;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTablesJNI;
import edu.wpi.first.networktables.StringSubscriber;
import edu.wpi.first.util.CombinedRuntimeLoader;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import edu.wpi.first.util.WPIUtilJNI;

/**
 * Program
 */
public class Program {

    static SerialLED leds = new SerialLED("COM4", 57600, 10);

    static NetworkTableInstance inst = NetworkTableInstance.getDefault();
    static NetworkTable ledTable = inst.getTable("ledControl");
    static IntegerArraySubscriber color   = ledTable.getIntegerArrayTopic("color").subscribe(new long[] {0,0,128});
    static StringSubscriber       pattern = ledTable.getStringTopic("pattern").subscribe("solid");

    static long[] currentColor = {0,0,128};
    static String currentPattern = "solid";

    static void updateLeds() {
        long[] currentColor = color.get();
        
        if (currentColor.length == 3) {
            leds.setColor((byte)currentColor[0], (byte)currentColor[1], (byte)currentColor[2]);
        }
    }

    public static void main(String[] args) throws IOException {

        //WPILIB Library Loading Weirdness.
        NetworkTablesJNI.Helper.setExtractOnStaticLoad(false);
        WPIUtilJNI.Helper.setExtractOnStaticLoad(false);

        CombinedRuntimeLoader.loadLibraries(Program.class, "wpiutiljni", "ntcorejni");
        //=====================================================================================================

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
