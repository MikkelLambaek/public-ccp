import util.Constants;
import util.SetTestObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.Logger;

/**
 * Created by Mikkel on 09-12-2015.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        Logger l = Logger.getLogger("SCAPI");
        Handler[] handlers = l.getHandlers();
        for (Handler h:
             handlers) {
            l.removeHandler(h);
        }

        // Update output filename
        updateOutputFile();

        //i is log(set size)
    for (int i = 2; i < 20; i++) {
            //j is number of repeats
            for (int j = 0; j < 10; j++) {
                System.out.println("i: " + i + ", j: " + j);

                //Generate two sets of equal size, with intersection size 1/4th
                SetTestObject.generateSet((1 << i), null, ((1 << i) >> 2));

                Thread sender = new Sender(SetTestObject.x);
                sender.start();

                Thread TD = new TrustedDealer();
                TD.start();

                Thread receiver = new Receiver(SetTestObject.y);
                //Abuse the Receiver, to ensure the protocol is done by the time of repeat.
                receiver.run();
            }
        }
    }

    // Create new file in testdirectory and assign this to constants.
    private static void updateOutputFile() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        Date resultDate = new Date(System.currentTimeMillis());

        File dir = new File(Constants.testFolder);
        Constants.testOutputFile = new File(dir, sdf.format(resultDate) + "_times.txt");

    }

}
