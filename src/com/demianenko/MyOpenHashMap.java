package com.demianenko;

import java.util.Arrays;

/**
 * Created by Viacheslav Demianenko
 *
 * Realization of open address hash map algorithm with put, get and size methods
 */
public class MyOpenHashMap {
    /**
     * The default value of {@link MyOpenHashMap#loadFactor}.
     */
    static final float DEFAULT_LOAD_FACTOR = 0.7f;

    /**
     * The default value of {@link MyOpenHashMap#multiplier}.
     */
    static final float DEFAULT_MULTIPLIER = 2.0f;

    /**
     * The default initial value of {@link MyOpenHashMap#capacity}.
     */
    static final int DEFAULT_INITIAL_CAPACITY = 16;

    /**
     * The maximum value of {@link MyOpenHashMap#capacity} when resize will not be done and new pair will not be put.
     */
    static final int MAXIMUM_CAPACITY = 1 << 30;

    /**
     * The constant for multiplication hash function.
     */
    static final double MULTIPLICATION_HASH_CONSTANT = (Math.sqrt(5)-1)/2;

    /**
     * Number of pairs currently stored in map.
     */
    private int size;

    /**
     * Number of rows in the hash table.
     */
    private int capacity;

    /**
     * The load factor for the hash table.
     */
    private float loadFactor;

    /**
     * Еhe multiplier by which the table will be increased.
     */
    private float multiplier;

    /**
     * The next size value at which to resize (capacity * load factor).
     */
    private int threshold;

    /**
     * An array where the keys stored.
     */
    private int[] keysArr;

    /**
     * An array where the values stored.
     */
    private long[] valuesArr;

    /**
     * An array where the hash table rows state stored.
     */
    private boolean[] filledBucketsArr;

    /**
     * Constructs an empty HashMap with the default initial capacity (16) default multiplier (2.0)
     * and the default load factor (0.7).
     */
    public MyOpenHashMap(){
        logger("Constructor MyOpenHashMap()");
        init(DEFAULT_INITIAL_CAPACITY,DEFAULT_LOAD_FACTOR,DEFAULT_MULTIPLIER);
        logger("Created new empty MyOpenHashMap with capacity="+DEFAULT_INITIAL_CAPACITY);
    }

    /**
     * Constructs an empty HashMap with the specified initial capacity default multiplier (2.0)
     * and the default load factor (0.7).
     *
     * @param initialCapacity the initial capacity must be >0
     */
    public MyOpenHashMap(int initialCapacity){
        logger("Constructor MyOpenHashMap(int initialCapacity)");
        init(initialCapacity,DEFAULT_LOAD_FACTOR,DEFAULT_MULTIPLIER);
        logger("Created new empty MyOpenHashMap with capacity="+initialCapacity);
    }

    /**
     * Constructs an empty HashMap with the specified initial capacity specified multiplier
     * and the specified load factor.
     *
     * @param initialCapacity the initial capacity must be >0
     * @param loadFactor the load factor must be float value >0 && <1
     * @param multiplier the multiplier must be float value >1 && <=2
     */
    public MyOpenHashMap(int initialCapacity, float loadFactor, float multiplier){
        logger("Constructor MyOpenHashMap(int initialCapacity, float loadFactor, float multiplier)");
        init(initialCapacity, loadFactor, multiplier);
        logger("Created new empty MyOpenHashMap with capacity="+initialCapacity+
                " loadFactor="+loadFactor+" multiplier="+multiplier);
    }

    /**
     * Initialization method (common part of all constructors).
     *
     * @param capacity the initial capacity must be >0
     * @param loadFactor the load factor must be float value >0 && <1
     * @param multiplier the multiplier must be float value >1 && <=2
     */
    private void init(int capacity, float loadFactor, float multiplier){
        if(capacity <= 0) throw new IllegalArgumentException("Initial capacity must be > 0");
        if (capacity > MAXIMUM_CAPACITY) capacity = MAXIMUM_CAPACITY;
        if(loadFactor<=0.0f || loadFactor>=1.0f)
            throw new IllegalArgumentException("Load factor must be float value >0 && <1");
        if(multiplier<=1.0f || multiplier>2.0f)
            throw new IllegalArgumentException("Multiplier must be float value >1 && <=2");
        this.size = 0;
        this.capacity = capacity;
        this.loadFactor = loadFactor;
        this.multiplier = multiplier;
        this.threshold = (int) (capacity*loadFactor);
        keysArr = new int[capacity];
        valuesArr = new long[capacity];
        filledBucketsArr = new boolean[capacity];
        Arrays.fill(filledBucketsArr, false);
    }

    /**
     * Associates the specified value with the specified key in this map.
     *
     * @param key the key
     * @param value the value
     * @return previous associated value if exists, or null if none
     */
    public Long put(int key, long value){
        logger("Method put for key="+key+" value="+value);
        if(size >= threshold) return resize(key, value);
        else {
            for (int currentBucket = bucketNumber(key); ; currentBucket++) {
                if (currentBucket == capacity) currentBucket = 0;
                logger("Try to put in bucket " + currentBucket);
                if (!filledBucketsArr[currentBucket]) {
                    keysArr[currentBucket] = key;
                    valuesArr[currentBucket] = value;
                    filledBucketsArr[currentBucket] = true;
                    size++;
                    logger("Successful!");
                    return null;
                }
                if (keysArr[currentBucket] == key) {
                    long prevValue = valuesArr[currentBucket];
                    valuesArr[currentBucket] = value;
                    logger("Successful! Replaced value=" + prevValue);
                    return prevValue;
                }
            }
        }
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this map contains no mapping for the key.
     *
     * @param key the key
     * @return associated value if exists, or null if none
     */
    public Long get (int key){
        logger("Method get for key="+key);
        for (int currentBucket=bucketNumber(key); ;currentBucket++){
            if(currentBucket == capacity) currentBucket=0;
            logger("Try to get from bucket "+currentBucket);
            if(!filledBucketsArr[currentBucket]){
                logger("Bucket empty. No mapping for the key");
                return null;
            }
            if(keysArr[currentBucket] == key ){
                logger("Successful!");
                return valuesArr[currentBucket];
            }
        }
    }

    /**
     * Returns the number of key-value mappings in this map.
     *
     * @return number of mappings in map
     */
    public int size(){
        return this.size;
    }

    /**
     * Increases the capacity of map {@link MyOpenHashMap#multiplier} times
     * than associates the specified value with the specified key in this map.
     *
     * @param key the key
     * @param value the value
     * @return previous associated value if exists, or null if none
     */
    private Long resize(int key, long value){
        logger("Method resize");
        if(capacity >= MAXIMUM_CAPACITY) throw new RuntimeException("Maximum capacity reached!");
        int[] oldKeys = Arrays.copyOf(keysArr,capacity);
        long[] oldValues = Arrays.copyOf(valuesArr,capacity);
        boolean[] oldFilledBuckets = Arrays.copyOf(filledBucketsArr,capacity);
        init((int) (capacity*multiplier),loadFactor,multiplier);
        for (int i=0;i<oldKeys.length;i++){
            if(oldFilledBuckets[i])put(oldKeys[i],oldValues[i]);
        }
        logger("Method resize done");
        return put(key, value);
    }

    /**
     * Computes the bucket number the specified key should be placed by multiplication method.
     *
     * @param key the key
     * @return the bucket number
     */
    private int bucketNumber(int key){
        return (int) Math.abs(Math.floor(capacity*((key*MULTIPLICATION_HASH_CONSTANT)%1)));
    }

    /**
     * Computes the bucket number the specified key should be placed by mod method.
     *
     * @param key the key
     * @return the bucket number
     */
    private int simpleBucketNumber(int key){
        return Math.abs(key%capacity);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("MyOpenHashMap size=").append(size).append(", capacity=").append(capacity).
                append(", threshold=").append(threshold).append("\n");
        for (int i=0; i< capacity; i++) {
            if(filledBucketsArr[i]){
                result.append("Bucket=").append(i).append(" key=").append(keysArr[i]).append(" value=").
                        append(valuesArr[i]).append("\n");
            }
        }

        return result.toString();
    }

    /**
     * The logger placeholder.
     *
     * @param message message to log
     */
    private void logger(String message){
        System.out.println("DEBUG: "+message);
    }
}
