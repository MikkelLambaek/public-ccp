package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class UniqueRandomNumbers {

    public static int[] get(int numberOfInts, int fieldSize) {
        int[] arr = new int[fieldSize];
        for (int i = 0; i<fieldSize; i++) {
            arr[i] = i;
        }

        FisherYatesShuffle.ShuffleArray(arr);

        int[] res = new int[numberOfInts];
        for (int i = 0; i < numberOfInts; i++) {
            res[i] = arr[i];
        }
//        System.arraycopy(arr, 0, res, 0, numberOfInts);

        return res;
    }
}