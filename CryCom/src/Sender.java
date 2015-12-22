import edu.biu.scapi.comm.Channel;
import edu.biu.scapi.exceptions.DuplicatePartyException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeoutException;



/**
 * Created by Mikkel on 19-11-2015.
 */
public class Sender {
    static int[] set = {0, 1, 5, 7, 13};
    public static int[] getM(int[] Q) {
        int[] P = TrustedDealer.getSender();
        int[] M = new int[set.length];

        for (int i = 0; i < set.length; i++) {
            M[i] = P[Q[set[i]]];
        }

        Arrays.sort(M);

        return M;
    }
}
