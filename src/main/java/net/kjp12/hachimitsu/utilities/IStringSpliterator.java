package net.kjp12.hachimitsu.utilities;// Created 2021-03-18T01:26:31

import org.jetbrains.annotations.Contract;

/**
 * @author KJP12
 * @since 0.0.0
 */
public interface IStringSpliterator {
    static IStringSpliterator of(String[] arr) {
        return new ArrayStringSpliterator(arr);
    }

    static IStringSpliterator of(String str) {
        return new StringSpliterator(str);
    }

    @Contract(pure = true)
    boolean contentEquals(String str);

    @Contract(pure = true)
    boolean contentEquals(String str, boolean ignoreCase);

    @Contract(pure = true)
    boolean startsWith(String str);

    @Contract(pure = true)
    boolean startsWith(String str, boolean ignoreCase);

    @Contract(pure = true)
    int currentLength();

    @Contract(pure = true)
    char charAt(int index);

    @Contract(pure = true)
    boolean isEmpty();

    void backtrack();

    @Contract(pure = true)
    boolean hasNext();

    void next();

    default String nextString() {
        next();
        return current();
    }

    default int nextHash() {
        next();
        return currentHash();
    }

    default int nextHashScreamingSnake() {
        next();
        return currentHashScreamingSnake();
    }

    default int nextInt() {
        next();
        return currentInt();
    }

    default long nextLong() {
        next();
        return currentLong();
    }

    default long nextDuration() {
        next();
        return currentDuration();
    }

    @Contract(pure = true)
    String current();

    @Contract(pure = true)
    int currentHash();

    @Contract(pure = true)
    int currentHashScreamingSnake();

    @Contract(pure = true)
    int currentInt();

    @Contract(pure = true)
    int currentInt(int def, int radix);

    @Contract(pure = true)
    long currentLong();

    @Contract(pure = true)
    long currentLong(long def, int radix);

    @Contract(pure = true)
    long currentDuration();

    @Contract(pure = true)
    long tryParseLong(long def, int radix);

    @Contract(pure = true)
    long tryParseUnsignedLong(long def, int radix);

    String rest();
}
