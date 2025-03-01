
import edu.wpi.first.networktables.IntegerArraySubscriber;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTablesJNI;
import edu.wpi.first.networktables.StringSubscriber;
import edu.wpi.first.util.CombinedRuntimeLoader;

import java.io.IOException;

import edu.wpi.first.util.WPIUtilJNI;

/**
 * Program
 */
public class Program {
    public static void main(String[] args) throws IOException {

        //WPILIB Library Loading Weirdness.
        NetworkTablesJNI.Helper.setExtractOnStaticLoad(false);
        WPIUtilJNI.Helper.setExtractOnStaticLoad(false);

        CombinedRuntimeLoader.loadLibraries(Program.class, "wpiutiljni", "ntcorejni");
        //=====================================================================================================


        NetworkTableInstance inst = NetworkTableInstance.getDefault();
        inst.startDSClient(); //Assume we are running on the driver station and attempt to connect to the robot.

        NetworkTable ledTable = inst.getTable("ledControl");

        IntegerArraySubscriber color   = ledTable.getIntegerArrayTopic("color").subscribe(new long[] {0,0,0});
        StringSubscriber       pattern = ledTable.getStringTopic("pattern").subscribe("solid");

        
        //Instantiate LED Controller
        SerialLED leds = new SerialLED("COM4", 57600, 10);

        byte R = 0;

        while (true) {
            leds.setColor(R, (byte) R, (byte) R);
            R++;
        }
    }
}
