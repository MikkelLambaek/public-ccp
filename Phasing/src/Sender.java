import Cuckoo.CuckooHashing;
import Cuckoo.HashFunction;
import edu.biu.scapi.comm.Channel;
import edu.biu.scapi.comm.EncryptedChannel;
import edu.biu.scapi.comm.twoPartyComm.PartyData;
import util.*;

import java.io.IOException;
import java.util.Map;


/**
 * Created by Mikkel on 19-11-2015.
 */
public class Sender extends Thread {
    private static int[] set;
    private static int[][] T1;
    private static String[][] M1;
    EncryptedChannel receiverEnc, TDEnc;
    private static int beta, maxBeta, l;

    public static void main(String[] args) throws IOException {

//        Thread sender = new Sender();
//        sender.start();

    }

    public Sender(int[] x) {
        set = x;
    }


    @Override
    public void run() {
        try {
            Channel receiver, TD;
            Map<PartyData, Map<String, Channel>> map = CommunicationHelper.setChannel("Sender");

            receiver = CommunicationHelper.getChannel(map, 0);
            TD = CommunicationHelper.getChannel(map, 1);

            receiverEnc = CommunicationHelper.createSecureChannel(receiver, "Receiver", "Sender");
            TDEnc = CommunicationHelper.createSecureChannel(TD, "Sender", "TD");

            receiver = null;
            TD = null;

            //Precomputation
            TDEnc.send(set.length);
            String pre = (String) TDEnc.receive();

            String[] preSplit = pre.split(" ");
            beta = Integer.parseInt(preSplit[0]);
            maxBeta = Integer.parseInt(preSplit[1]);
            l = Integer.parseInt(preSplit[2]);
            preSplit = null;

            initSender();

            //Protocol start
            hashElements();

            String temp;
            String[] OTRes = new String[Constants.N];
            int i, j, k;
            for (int p = 0; p < beta * maxBeta * Constants.t; p++) {
                temp = (String) TDEnc.receive();

                i = Integer.parseInt(temp.split(" ")[0]);
                j = Integer.parseInt(temp.split(" ")[1]);
                k = Integer.parseInt(temp.split(" ")[2]);

                for (int l = 0; l < Constants.N; l++) {
                    OTRes[l] = temp.split(" ")[l+3];
                }

                OTOutput(i, j, k, OTRes);
            }

            receiverEnc.send(getV());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void initSender() {
        M1 = ArrayHelper.initArray(beta, maxBeta, l);
    }

    public void hashElements() throws IOException, ClassNotFoundException {
        HashFunction[] mHF = (HashFunction[]) TDEnc.receive();

        T1 = new int[beta][maxBeta];

        int[] binSize = new int[beta];

        CuckooHashing.kStartSize = beta;

        try {
            int hash = 0;
            for (HashFunction aMHF : mHF) {
                for (int aSet : set) {
                    hash = aMHF.hash(aSet);
                    T1[hash][binSize[hash]] = aSet; // Hash or value?
                    binSize[hash]++;
                }
            }
        } catch (IndexOutOfBoundsException e) {
            System.out.println("The sender's hashing failed.");
            e.printStackTrace();
            System.exit(0);
        }

        for (int i = 0; i < beta; i++) {

            while (binSize[i] < maxBeta) {
                T1[i][binSize[i]] = Constants.d1; //Define d1 somewhere.
                binSize[i]++;
            }

            FisherYatesShuffle.ShuffleArray(T1[i]);
        }

    }

    public static void OTOutput(int i, int j, int k, String[] res) {
        String vj = StringOperations.intToBinaryStringLengthTMu(T1[i][j]);

        //If the element looked at is the dummy element its xor value is irrelevant
        if (T1[i][j] == (Constants.d1)) {
            return;
        }

        int vjkBase10 = Integer.parseInt(vj.substring(k*Constants.mu, (k+1)*Constants.mu), 2);

        String mVjK = res[vjkBase10];

        String xorVal = mVjK.substring(j * l, (j+1)*l);

        M1[i][j] = StringOperations.xorStrings(M1[i][j], xorVal);
    }

    public static String[] getV() {
        String[] V = new String[set.length * Constants.numOfHashFunc];
        int c = 0;

        for (int i = 0; i < beta; i++) {
            for (int j = 0; j < maxBeta; j++) {
                if(T1[i][j] != Constants.d1) {
                    V[c] = M1[i][j];
                    c++;
                }
            }
        }

        FisherYatesShuffle.ShuffleArray(V);

        return V;
    }
}
