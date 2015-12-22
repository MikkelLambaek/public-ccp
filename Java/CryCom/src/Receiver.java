import edu.biu.scapi.comm.Channel;
import edu.biu.scapi.exceptions.DuplicatePartyException;
import util.Constants;
import util.FisherYatesShuffle;
import util.PrintHelper;

import javax.rmi.CORBA.Util;
import java.io.IOException;
import java.security.cert.CertStoreSpi;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Created by Mikkel on 19-11-2015.
 */
public class Receiver {
    public static void main(String[] args) throws IOException {
        //The Sender's private set.
        int[] set = {0, 3, 4, 5, 8, 13, 15};
        TrustedDealer.getReceiver(set.length);

        int[] R = TrustedDealer.getR();
        int[] S = TrustedDealer.getS();

        int[] Q = new int[Constants.fieldSize];
        for (int i = 0; i < Q.length; i++) {
            Q[i] = i;
        }

        int tmp;
        for (int i = 0; i < set.length; i++) {
                if (Q[set[i]] != R[i]) {
                    tmp = Q[set[i]];
                    for (int j = 0; j < Q.length; j++) {
                        if (Q[j] == R[i]) {
                            Q[set[i]] = R[i];
                            Q[j] = tmp;
                            break;
                        }
                    }
                }
        }
        int[] M;
        M = Sender.getM(Q);

        List<Integer> I = new ArrayList<>();
        for (int i = 0; i < S.length; i++) {
            for (int j = 0; j < M.length; j++) {
                if (S[i] == M[j])
                    I.add(R[i]);
            }
        }
        List<Integer> z = new ArrayList<>();
        for (int i = 0; i < Constants.fieldSize; i++) {
            for (int j = 0; j < I.size(); j++) {
                if (Q[i] == I.get(j)) {
                    z.add(i);
                    System.out.println(i);
                }
            }
        }
    }

}



