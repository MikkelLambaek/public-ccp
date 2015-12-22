package Cuckoo;
/***************************************************************************
 * File: CuckooHashing.java
 * Author: Keith Schwarz (htiek@cs.stanford.edu)
 *
 * An implementation of a hash map backed by a cuckoo hash table.  Cuckoo hash
 * tables, first described in "Cuckoo Hashing" by Pugh and Rodler, is a hash
 * system with worst-case constant-time lookup and deletion, and amortized
 * expected O(1) insertion.
 *
 * Internally, cuckoo hashing works by maintaining two arrays of some size,
 * along with two universal hash functions f and g.  When an element x is
 * inserted, the value f(x) is computed and the entry is stored in that spot
 * in the first array.  If that spot was initially empty, we are done.
 * Otherwise, the element that was already there (call it y) is "kicked out."
 * We then compute g(y) and store element y at position g(y) in the second
 * array, which may in turn kick out another element, which will be stored in
 * the first array.  This process repeats until either a loop is detected (in
 * which case we pick a new hash function and rehash), or all elements finally
 * come to rest.
 *
 * The original paper by Pugh and Rodler proves a strong bound - for any
 * epsilon greater than zero, if the load factor of a Cuckoo hash table is at
 * most (1/2 - epsilon)n, then both the expected runtime and variance of
 * the expected runtime for an insertion is amortized O(1).  This means that
 * we will always want to keep the load factor at just below 50%, say, at 40%.
 *
 * The main challenge of implementing a cuckoo hash table in Java is that the
 * hash code provided for each object is not drawn from a universal hash
 * function.  To ameliorate this, internally we will choose a universal hash
 * function to apply to the hash code of each element.  This is only a good
 * hash if the hash codes for objects are distributed somewhat uniformly, but
 * we assume that this is the case.  If it isn't true - and in particular, if
 * more than two objects of the type hash to the same value - then all bets
 * are off and the cuckoo hash table will entirely fail.  Internally, the
 * class provides a default hash function (described below), but complex class
 * implementations should provide their own implementation.
 *
 * Our family of universal hash functions is based on the universal hash
 * functions described by Mikkel Thorup in "String Hashing for Linear Probing."
 * We begin by breaking the input number into two values, a lower 16-bit value
 * and an upper 16-bit value (denoted HIGH and LOW), then picking two random
 * 32-bit values A and B (which will remain constant across any one hash
 * function from this family).  We then compute
 *
 *           HashCode = ((HIGH + A) * (LOW * B)) / (2^(32 - k))
 *
 * Where 2^k is the number of buckets we're hashing into.
 */
import util.Constants;
import util.HashingHelper;
import util.PrintHelper;

import javax.xml.bind.DatatypeConverter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*; // For AbstractMap, AbstractSet, Arrays

@SuppressWarnings("unchecked") // For array casts
public final class CuckooHashing  {
    /* The initial size of each array. */
    public static int kStartSize;
    public static int logStartSize;

    /* The maximum load factor, which we arbitrarily decree to be 40%. */
    private static final float kMaxLoadFactor = 0.99f;

    public int[] elemArray;

    /* The two hash functions. */
    private final HashFunction<? super Integer> mHashFns[] = new HashFunction[2];

    /* The family of universal hash functions. */
    private final UniversalHashFunction<? super Integer> mUniversalHashFunction;

    /* The number of entries that are filled in. */
    private int mSize = 0;

    public HashFunction[] getHashFunctions() {
        return mHashFns;
    }

    /**
     * Utility class representing a default hash function, as described above.
     */
    private static final class DefaultHashFunction<T> implements HashFunction<T>, Serializable {
//        private final int mA, mB;  // Coefficients for this hash function
//        private final int mLgSize; // Log of the size of the hash tables.

        private final int mShifted;

        /**
         * Constructs a new hash function using the specified coefficients and
         * the log of the number of buckets in the hash table.
         *
//         * @param a The first coefficient
//         * @param b The second coefficient
//         * @param lgSize The base-two log of the number of buckets.
         */
        public DefaultHashFunction(/*int a, int b, int lgSize*/ int shifted) {
//            mA = a;
//            mB = b;
//            mLgSize = lgSize;
            mShifted = shifted;
        }

        /**
         * Given an object, evaluates its hash code.
         *
         * @param obj The object whose hash code should be evaluated.
         * @return Its hash code.
         */
        public int hash(int obj) {
            /* If the object is null, just evaluate to zero. */
            if (obj == 0) return 0;

            /* Otherwise, split its hash code into upper and lower bits. */
//            final int upper = obj >>> 16;
//            final int lower = obj & (0xFFFF);

            /* Return the pairwise product of those bits, shifted down so that
             * only lgSize bits remain in the output.
             */

            String i = HashingHelper.getInstance().getBits(obj, logStartSize, mShifted);

//            return ((upper * mA + lower * mB) >>> (32 - mLgSize));
//            return DigestUtils

            return Integer.parseInt(i, 2);
        }
    };

    /**
     * Utility class representing a default generator of universal hash
     * functions.  This class is hardcoded to assume that the number of
     * buckets is a perfect power of two, though in general the
     * UniversalHashFunction contract says nothing of this.
     */
    private static final class DefaultUniversalHashFunction<T> implements UniversalHashFunction<T>, Serializable {
        /* A random-number generator for producing the hash functions. */
        private final Random mRandom = new SecureRandom();

        /**
         * Produces a HashFunction from the given bucket size.
         *
         * @param numBuckets The number of buckets to use.
         */
        public HashFunction<T> randomHashFunction(int numBuckets) {
            /* Compute the base-2 logarithm of the number of buckets.  This
             * value is the number of bits required to hold the number of
             * buckets, but we want one minus this value because we want to
             * know the number of bits necessary to index any of these buckets.
             * This is given by the log minus one, and so we start a counter
             * at -1 and keep bumping it as many times as we can divide by two.
             */
            int shift = (int) Math.abs(mRandom.nextInt() % ( 256 - logStartSize));
//            System.out.println("shift: " + shift);
            return new DefaultHashFunction<>(shift);
//            int lgBuckets = -1;
//            for (; numBuckets > 0; numBuckets >>>= 1)
//                ++lgBuckets;
//
//            /* Return a default hash function initialized with random values
//             * and the log of the number of buckets.
//             */
//            int a = 0;
//            int b = 0;
//            while (a == 0) {
//                a = mRandom.nextInt();
//            }
//            while (b == 0) {
//                b = mRandom.nextInt();
//            }
//
//            return new DefaultHashFunction<T>(a, b,
//                    lgBuckets);
        }
    }

    /**
     * Creates a new, empty CuckooHashMap using a default family of universal
     * hash functions.  Note that this is in general NOT SAFE unless you can
     * positively guarantee that no two distinct objects have distinct hash
     * codes.  This will be true for objects that don't explicitly override
     * hashCode(), and certain numeric wrappers like Integer, but not for more
     * complex types like String.
     */
    public CuckooHashing(int beta) {
        /* Set us up with a default universal hash function. */
        this(new DefaultUniversalHashFunction(), beta);
    }

    /**
     * Creates a new, empty CuckooHashMap using the specified family of
     * universal hash functions.
     *
     * @param fn The family of universal hash functions to use.
     */
    public CuckooHashing(UniversalHashFunction<? super Integer> fn, int beta) {
        /* Confirm that the family of hash functions is not null; we can't use
         * it if it is.
         */
        kStartSize = beta;
        logStartSize = (int) (Math.log(kStartSize)/Math.log(2));
        elemArray = new int[kStartSize];

        if (fn == null)
            throw new NullPointerException("Universal hash function must be non-null.");

        /* Store the family for later use. */
        mUniversalHashFunction = fn;

        /* Set up the hash functions. */
        generateHashFunctions();
    }

    public void insertVal(int val) {
        /* Check whether this value already exists.  If so, just displace its
         * old value and hand it back.
         */
        for (int i = 0; i < 2; ++i) {
            /* Compute the hash code, then look up the entry there. */
            final int hash = mHashFns[i].hash(val);

            /* If the entry matches, we found what we're looking for. */
            if (elemArray[hash] != 0 && elemArray[hash] ==  val) {
                /* Cache the value so we can return it, then clobber it
                 * with the new value.
                 */
                return;
            }
        }

        int toInsert = val;

        while (true) {
            /* Add the entry to the table, then see what element was
             * ultimately displaced.
             */
            toInsert = tryInsertInt(toInsert);

            /* If nothing ended up displaced, we're done. */
            if (toInsert == 0) break;

            /* Otherwise, rehash and try again. */
            rehash();
        }

        /* We just added an entry, so increase our recorded size. */
        ++mSize;

        /* Nothing was associated with this value. */
        return;
    }

    /**
     * Given an Entry, tries to insert that entry into the hash table, taking
     * several iterations if necessary.  The return value is the last entry
     * that was displaced, which will be null if the element was inserted
     * correctly and will be some arbitrary other entry otherwise.
     *
     * @param toInsert The entry to insert into the hash table.
     * @return The last displaced entry, or null if all collisions were
     *         resolved.
     */

    private int tryInsertInt(int toInsert) {
        int pos;
        int temp;
        if (elemArray[mHashFns[0].hash(toInsert)] == toInsert || elemArray[mHashFns[1].hash(toInsert)] == toInsert){
            return 0;
        }
        pos = mHashFns[0].hash(toInsert);
        for (int numTries = 0; numTries < size() + 1; ++numTries) {
            if (elemArray[pos] == 0) {
                elemArray[pos] = toInsert;
                return 0;
            }
            temp = elemArray[pos];
            elemArray[pos] = toInsert;
            toInsert = temp;

            if (pos == mHashFns[0].hash(toInsert)) {
                pos = mHashFns[1].hash(toInsert);
            } else {
                pos = mHashFns[0].hash(toInsert);
            }
        }
        return toInsert;
    }

    /**
     * Utility function to choose new hash functions for the hash table.
     */
    private void generateHashFunctions() {
        /* Create two new hash functions using the log size of the buckets and
         * two random integers.
         */
        for (int i = 0; i < 2; ++i) {
            mHashFns[i] = mUniversalHashFunction.randomHashFunction(elemArray.length);
        }

    }

    /**
     * Utility function to rehash all of the elements in the hash table.  This
     * does NOT grow the size of the hash tables; rather, it recomputes the
     * hash values for all of the entries according to some new hash function.
     */
    private void rehash() {
        /* Begin by creating an array of elements suitable for holding all the
         * elements in the hash table.  We need to do this here, since we're
         * going to be mucking around with the contents of the arrays and
         * otherwise have no way of tracking what values got inserted.
         */
        int[] values = new int[size()];
        int c = 0;
        for (int i = 0; i < elemArray.length; i++) {
            if (elemArray[i] != 0) {
                values[c] = elemArray[i];
                c++;
            }
        }

        /* Continuously spin, trying to add more and more values to the table.
         * If at any point we can't add something, pick new hash functions and
         * start over.
         */
        reinsert: while (true) {
            /* Clear all the arrays. */
            for (int i = 0; i < elemArray.length; i++) {
                elemArray[i] = 0;
            }

            /* Pick two new hash functions. */
            generateHashFunctions();

            /* Try adding everything. */
            for (int i = 0; i < values.length; i++) {
                if (tryInsertInt(values[i]) != 0) {
                    continue reinsert;
                }
            }

            /* If we made it here, we successfully inserted everything and are
             * done.
             */
            break;
        }
    }

    /**
     * Returns the number of elements in the hash map.
     *
     * @return The number of elements in the hash map.
     */
    public int size() {
        return mSize;
    }

    /**
     * Returns whether the hash map is empty.
     *
     * @return Whether the hash map is empty.
     */
    public boolean isEmpty() {
        return size() == 0;
    }

}