package util;

/**
 * Created by Mikkel on 19-11-2015.
 */
public class PrintHelper {
    public static String byteArrayAsString(byte[] byteArray) {

        String res = "";

        boolean first = true;
        res += "[";
        for (int i = 0; i < byteArray.length; i++){
            if(first) {
                first = false;
            } else {
                res += ", ";
            }
            res += byteArray[i];
        }

        res += "]";

        return res;
    }

    public static String intArrayAsString(int[] intArray) {

        String res = "";

        boolean first = true;
        res += "[";
        for (int i = 0; i < intArray.length; i++){
            if(first) {
                first = false;
            } else {
                res += ", ";
            }
            res += intArray[i];
        }

        res += "]";

        return res;
    }
}
