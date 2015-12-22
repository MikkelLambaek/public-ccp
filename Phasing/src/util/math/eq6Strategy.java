package util.math;

/**
 * Created by Mikkel on 15-12-2015.
 */
//Equation 6 in Phasing: Private Set Intersection using Permutation-based Hashing page 13, by Pinkas, Scheider, Segev and Zohner.
public class eq6Strategy implements CalculateMaxBetaStrategy {
    @Override
    public int calcMaxBeta(int n) {
        return (int) Math.max(6, 2 * Math.E * (Math.log(n) / Math.log(2)) / ((Math.log(Math.log(n) / Math.log(2)) / Math.log(2))) + 1);
    }
}
