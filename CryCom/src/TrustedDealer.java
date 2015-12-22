import util.Constants;

/**
 * Created by Mikkel on 19-11-2015.
 */
public class TrustedDealer {

    //Generate permutation, r and s
    static int[] P ;
    static int[] R;
    static int[] S;

    public static int[] getR() {
        return R;
    }

    public static int[] getS() {
        return S;
    }

    public static void getReceiver(int k) {
        S = new int[k];
        P = new int[Constants.fieldSize];

        for (int i = 0; i < P.length; i++)
            P[i] = i;
        util.FisherYatesShuffle.ShuffleArray(P);

        R = util.UniqueRandomNumbers.get(k, Constants.fieldSize);

        for (int i = 0; i < k; i++) {
            S[i] = P[R[i]];
        }
    }

    public static int[] getSender() {
        return P;
    }
}