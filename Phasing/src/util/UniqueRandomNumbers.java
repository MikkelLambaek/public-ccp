package util;

public class UniqueRandomNumbers {

    public static int[] get(int numberOfInts, int fieldSize) {
        int[] arr = new int[fieldSize];
        for (int i = 0; i<fieldSize; i++) {
            arr[i] = i;
        }

        FisherYatesShuffle.ShuffleArray(arr);

        int[] res = new int[numberOfInts];
        System.arraycopy(arr, 0, res, 0, numberOfInts);

        return res;
    }
}