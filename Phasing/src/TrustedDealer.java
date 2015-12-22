import Cuckoo.HashFunction;
import edu.biu.scapi.comm.Channel;
import edu.biu.scapi.comm.EncryptedChannel;
import edu.biu.scapi.comm.twoPartyComm.PartyData;
import util.CommunicationHelper;
import util.Constants;
import util.math.CalculateMaxBetaStrategy;
import util.math.eq6Strategy;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;

/**
 * Created by Mikkel on 01-12-2015.
 */
public class TrustedDealer extends Thread{
    private static Random rand = null;

    public static HashFunction[] hashFunctions;
    EncryptedChannel receiverEnc, senderEnc;
    private CalculateMaxBetaStrategy cMBS = new eq6Strategy();
    private int beta, maxBeta, l;


    public static void main(String[] args) throws IOException {

        Thread TD = new TrustedDealer();
        TD.start();

    }

    @Override
    public void run() {
        try {
            Channel receiver, sender;
            Map<PartyData, Map<String, Channel>> map = CommunicationHelper.setChannel("TD");

            receiver = CommunicationHelper.getChannel(map, 0);
            sender = CommunicationHelper.getChannel(map, 1);

            receiverEnc = CommunicationHelper.createSecureChannel(receiver, "Receiver", "TD");
            senderEnc = CommunicationHelper.createSecureChannel(sender, "Sender", "TD");

            receiver = null;
            sender = null;

            //Precomputation
            int n1 = (int)senderEnc.receive();
            int n2 = (int)receiverEnc.receive();
//            beta = (int) (2*(Constants.epsilon + 1) * n2);
            beta = (int) (2* n2);
            maxBeta = cMBS.calcMaxBeta(n1);
            l = (int) (Constants.lambda + Math.log(n1 - 1) / Math.log(2) + Math.log(n2 - 1) / Math.log(2) + 2);

            receiverEnc.send(beta + " " + maxBeta + " " + l);
            senderEnc.send(beta + " " + maxBeta + " " + l);

            //Protocol start
            hashFunctions = (HashFunction[]) receiverEnc.receive();

            senderEnc.send(hashFunctions);

            initRandOT();

            String temp;
            int i, j, k, wk;
            for (int p = 0; p < beta * maxBeta * Constants.t; p++) {
                temp = (String) receiverEnc.receive();

                i = Integer.parseInt(temp.split(" ")[0]);
                j = Integer.parseInt(temp.split(" ")[1]);
                k = Integer.parseInt(temp.split(" ")[2]);
                wk = Integer.parseInt(temp.split(" ")[3]);

                receiverEnc.send(randOT(i, j, k, wk));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void initRandOT() {
        //TODO : may want to change seed
        rand = new SecureRandom();
    }

    //i, k, wk in base 10
    //returns in binary
    public String randOT(int i, int j, int k, int wk) throws IOException {
        String temp = "", nextRand = "", randOTs;
        int remainingLength = 0;

        randOTs = i + " " + j + " " + k;

        for (int OTResults = 0; OTResults < Constants.N; OTResults++) {
            remainingLength = l * maxBeta;
            temp = "";

            while (temp.length() < l * maxBeta) {
                nextRand = Integer.toBinaryString(Math.abs(rand.nextInt()));

                temp += nextRand.substring(0, Integer.min(nextRand.length(), remainingLength));

                remainingLength -= nextRand.length();
            }

            randOTs = randOTs + " " + temp;
        }

        senderEnc.send(randOTs);

        return randOTs.split(" ")[wk + 3];
    }

}
