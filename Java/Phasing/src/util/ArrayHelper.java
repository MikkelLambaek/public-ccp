package util;

/**
 * Created by Mikkel on 09-12-2015.
 */
public class ArrayHelper {
    public static String[][] initArray(int firstDim, int secondDim, int l) {
        String[][] arr = new String[firstDim][secondDim];

        arr[0][0] = "";
        while (arr[0][0].length() < (l)) {
            arr[0][0] += "0";
        }

        for (int i = 0; i < firstDim; i++) {
            for (int j = 0; j < secondDim; j++) {
                arr[i][j] = arr[0][0];
            }
        }

        return arr;

    }
}
