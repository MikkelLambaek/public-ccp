package util;

/**
 * Created by Mikkel on 09-12-2015.
 */
public class StringOperations {
    public static String intToBinaryStringLengthTMu(int n) {
        String w = Integer.toBinaryString(n);
        //Ensure equal length of all elements, namely length t*mu
        while (w.length() < Constants.t * Constants.mu) {
            w = "0" + w;
        }
        return w;
    }

    //Assumes input strings are of equal length and represented as a binary number (only contains 0/1)
    public static String xorStrings(String s1, String s2) {
        String res = "", temp = "";
        int subString1 = 0, subString2 = 0;
        int index = 0, length = 0;

        while (index  < s1.length()) {
            //Get 28 bytes of the string at a time
            length = Integer.min(31, s1.length() - index);
            subString1 = Integer.parseInt(s1.substring(index, index + length), 2);
            subString2 = Integer.parseInt(s2.substring(index, index + length), 2);

            //ensure that leading 0s are included.
            temp = Integer.toBinaryString((subString1^subString2));
            while (temp.length() < length) {
                temp = "0" + temp;
            }
            index += length;

            res = res + temp;
        }

        return res;
    }
}
