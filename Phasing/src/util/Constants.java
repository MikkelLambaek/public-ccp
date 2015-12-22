package util;

import java.io.File;
import java.util.Map;

/**
 * Created by Mikkel on 19-11-2015.
 */
public class Constants {
    // TODO : The hash of 0 is always 0, so including that element in your list is not great.

    public static final String testFolder = "tests";
    public static File testOutputFile;

    //Statistical security parameter
    public static final int lambda = 40;

    public static final double epsilon = 0.2;

    public static final int mu = 3;
    public static final int N = (1 << mu);
    //TODO: Change sigma to be dependant on set size?
    public static final int sigma = 22;

    //Ceil(sigma/mu) = ((sigma - 1) / mu) + 1
    public static final int t = (sigma - 1) / mu + 1;

    public static final int numOfHashFunc = 2;

    //Assumed to not be used in either set.
    public static final int d1 = (1 << sigma) - 1;
    public static final int d2 = (1 << sigma) - 2;
}
