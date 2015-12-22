package util;

import java.security.SecureRandom;

/**
 * Created by Mikkel on 19-11-2015.
 */
public class FisherYatesShuffle {
    //Code copied from: http://stackoverflow.com/a/18456998
    public static void ShuffleArray(int[] array)
    {
        int index, temp;
        SecureRandom random = new SecureRandom();
        for (int i = array.length - 1; i > 0; i--)
        {
            index = random.nextInt(i + 1);
            temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    public static void ShuffleArray(Object[] array)
    {
        int index;
        Object temp;
        SecureRandom random = new SecureRandom();
        for (int i = array.length - 1; i > 0; i--)
        {
            index = random.nextInt(i + 1);
            temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }
}


