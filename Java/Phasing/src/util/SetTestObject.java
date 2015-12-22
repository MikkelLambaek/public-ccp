package util;

import java.util.Random;

/**
 * Created by figaw on 20/12/2015.
 */
public class SetTestObject {

    public static int[] x;
    public static int[] y;
    int intersection;
    static boolean intersectionCanBeZero = false;
    //maximum element to exclude the two dummy elements d1, d2.
    static int maxInt = (1 << Constants.sigma) - 3;

    public SetTestObject(int[] x, int[] y, int intersection) {

        this.x = x;
        this.y = y;
        this.intersection = intersection;

    }

    @Override
    public String toString(){

        String res = "";

        res += "x: ";
        res += printSet(x);
        res += "\n";
        res += "y: ";
        res += printSet(y);
        res += "\n";
        res += ". With intersection: " + intersection;

        return res;
    }

    private String printSet(int[] s) {
        String res = "{";

        for(int i = 0; i < s.length; i++){
            res += s[i];
            if(i != s.length-1){
                res += ", ";
            }
        }

        res += "}";

        return res;

    }

	    private static SetTestObject generateSet(int sizeX){

        Random r = new Random();

        int intersectionSize = r.nextInt(sizeX);

        if(intersectionSize == 0 && !intersectionCanBeZero){
            while(intersectionSize == 0){
                intersectionSize = r.nextInt(sizeX);
            }
        }

        return generateSet(sizeX, sizeX, intersectionSize);

    }

    public static SetTestObject generateSet(int sizeX, Integer sizeY, int sizeIntersection){

        if(sizeY == null)
            sizeY = sizeX;

        Random r = new Random();

        int[] x = new int[sizeX];
        int[] y = new int[sizeY];

        for (int i = 0; i < sizeX; i++){
            x[i] = r.nextInt(maxInt);
        }

        for (int i = 0; i < sizeY; i++){
            int elem = r.nextInt(maxInt);
            y[i] = elem;

            if(i < sizeIntersection){
                x[i] = elem;
            }

        }

        return new SetTestObject(x, y, sizeIntersection);

    }


}
