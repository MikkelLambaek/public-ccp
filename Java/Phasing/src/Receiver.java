import Cuckoo.CuckooHashing;
import edu.biu.scapi.comm.Channel;
import edu.biu.scapi.comm.EncryptedChannel;
import edu.biu.scapi.comm.twoPartyComm.PartyData;
import util.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Timer;

/**
 * Created by Mikkel on 19-11-2015.
 */
public class Receiver extends Thread {
    //The Receiver's private set.
    private static int[] set;
    private static CuckooHashing mCH;
    private static String[][] M2;
    private static String[] V;
    EncryptedChannel senderEnc, TDEnc;
    private static int beta, maxBeta, l;

    public static void main(String[] args) throws IOException {

//        Thread receiver = new Receiver();
//        receiver.start();

    }

    public Receiver(int[] x) {
        set = x;
    }


    @Override
    public void run() {

        try {
            Channel sender, TD;

            long t;

            Map<PartyData, Map<String, Channel>> map = CommunicationHelper.setChannel("Receiver");

            sender = CommunicationHelper.getChannel(map, 0);
            TD = CommunicationHelper.getChannel(map, 1);

            senderEnc = CommunicationHelper.createSecureChannel(sender, "Receiver", "Sender");
            TDEnc = CommunicationHelper.createSecureChannel(TD, "Receiver", "TD");

            sender = null;
            TD = null;

            //Precomputation
            TDEnc.send(set.length);
            String pre = (String) TDEnc.receive();

            String[] preSplit = pre.split(" ");
            beta = Integer.parseInt(preSplit[0]);
            maxBeta = Integer.parseInt(preSplit[1]);
            l = Integer.parseInt(preSplit[2]);
            preSplit = null;

            //Protocol start
            t = System.currentTimeMillis();
            hashElements();

            TDEnc.send(mCH.getHashFunctions());

            maskViaOTReceiver();


            V = (String[]) senderEnc.receive();

            findIntersection();
            //Protocol end

            //Output set size + time to times.txt
            t = System.currentTimeMillis() - t;
            PrintWriter writer = new PrintWriter(new FileOutputStream(
                    Constants.testOutputFile,
                    true /* append = true */));
            writer.append(set.length + " " + t + "\n");
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private static void hashElements() {

        mCH = new CuckooHashing(beta);

        for (int aSet : set) {
            mCH.insertVal(aSet);
        }
    }

    private void maskViaOTReceiver() throws IOException, ClassNotFoundException {
        String w, OTRes;
        int wk;

        M2 = ArrayHelper.initArray(beta, maxBeta, l);

        w = "";
        for (int i = 0; i < beta; i++) {

            //Step 2a
            //Obtain integer w in base 2^mu = N (as a binary string) (element d2 if it is not in the receiver's set
            if (mCH.elemArray[i] == 0) {
                w = StringOperations.intToBinaryStringLengthTMu(Constants.d2);
            } else {
                w = StringOperations.intToBinaryStringLengthTMu(mCH.elemArray[i]);
            }

            for (int k = 0; k < Constants.t; k++) {
                wk = Integer.parseInt(w.substring(k * Constants.mu, (k+1) * Constants.mu), 2);
                for (int j = 0; j < maxBeta; j++) {
                    TDEnc.send(i + " " + j + " " + k + " " +wk);
                    OTRes = (String) TDEnc.receive();

                    M2[i][j] = StringOperations.xorStrings(M2[i][j], OTRes.substring(j * l, (j+1) * l));
                }
            }
        }
    }

    private static void findIntersection() {
        int correct = 0;
        int[] intersection = new int[set.length];
        for (int i = 0; i < beta; i++) {
            for (int j = 0; j < maxBeta; j++) {
                for (String aV : V) {
                    if (aV.equals(M2[i][j])) {
                        //TODO : Fix intersection including an element twice in a less poor manner
                        if (!(correct > 0 && (intersection[correct-1] == mCH.elemArray[i]) || mCH.elemArray[i] == 0)) {
                            intersection[correct] = mCH.elemArray[i];
                            correct++;
                        }
                    }
                }
            }
        }

/*
        System.out.println("Size of intersection: " + correct);
        System.out.print("Elements in the intersection: ");
        for (int i = 0; i < correct; i++) {
            System.out.print(intersection[i] + " ");
        }
        System.out.println();
*/


    }
}



