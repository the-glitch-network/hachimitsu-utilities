package net.kjp12.hachimitsu.utilities;

import org.jetbrains.annotations.Contract;

import static net.kjp12.hachimitsu.utilities.StringUtils.*;

public final class StringHashSpliterator implements IStringSpliterator {
    private final boolean respectQuotes;
    private final char[] delimiters;
    private final String toSplit;
    private String cache;
    private int ib, ia, ib$q;

    public StringHashSpliterator(String toSplit) {
        this(toSplit, ARRAY_DEFAULT_DELIMITERS, true);
    }

    public StringHashSpliterator(String toSplit, String delimiters) {
        this(toSplit, delimiters, true);
    }

    public StringHashSpliterator(String toSplit, boolean respectQuotes) {
        this(toSplit, ARRAY_DEFAULT_DELIMITERS, respectQuotes);
    }

    public StringHashSpliterator(String toSplit, String delimiters, boolean respectQuotes) {
        this(toSplit, createCharHashArray(delimiters), respectQuotes);
    }

    public StringHashSpliterator(String toSplit, char[] delimiters, boolean respectQuotes) {
        this.toSplit = toSplit;
        this.delimiters = delimiters;
        this.respectQuotes = respectQuotes;
    }

    public boolean hasNext() {
        return ib < toSplit.length();
    }

    public void seekToNonDelimiter() {
        ib = StringUtils.seekToNonDelimiter(toSplit, delimiters, toSplit.length(), ib);
    }

    public void seekToDelimiter(char[] delimiters) {
        ib = StringUtils.seekToDelimiter(toSplit, delimiters, toSplit.length(), ib);
    }

    public void seekToDelimiter() {
        ib = StringUtils.seekToDelimiter(toSplit, delimiters, toSplit.length(), ib);
    }

    public void seekToEndQuote(final char quote) {
        ib = StringUtils.seekToEndQuote(toSplit, delimiters, quote, toSplit.length(), ib);
    }

    public void next() {
        if (ib < ia) {
            // We backtracked, we're just going to return the cached value.
            // We will also set index to lastIndex and lastIndex to index as they're swapped anyways on backtracking.
            final int i = ia;
            ia = ib;
            ib = i;
        } else {
            seekToNonDelimiter();
            ia = ib;
            if (respectQuotes) {
                final char c = toSplit.charAt(ib);
                if (ARRAY_QUOTES[c & ARRAY_QUOTES.length] == c) {
                    ia++;
                    ib++;
                    seekToEndQuote(c);
                    ib$q = ib++;
                } else if (ARRAY_QUOTE_PAIRED[c & ARRAY_QUOTE_PAIRED.length] == c) {
                    ia++;
                    ib++;
                    seekToEndQuote((char) (c + 1));
                    ib$q = ib++;
                } else {
                    seekToDelimiter();
                    ib$q = ib;
                }
            } else {
                seekToDelimiter();
                ib$q = ib;
            }
            cache = null; // this is invalid now
        }
    }

    /**
     * Throws the spliterator into an invalid state for certain functions.
     */
    public void backtrack() {
        int i = ib;
        ib = ia;
        ia = i;
    }

    @Contract(pure = true)
    public String current() {
        return cache == null ? (cache = toSplit.substring(Math.min(ia, ib), ib$q)) : cache;
    }

    @Contract(pure = true)
    public boolean startsWith(String str) {
        int i = Math.min(ia, ib), l = ib$q - i;
        return str.length() <= l && toSplit.startsWith(str, i);
    }

    @Contract(pure = true)
    public boolean startsWith(String str, boolean ignoreCase) {
        int i = Math.min(ia, ib), l = ib$q - i;
        return str.length() <= l && str.regionMatches(ignoreCase, i, str, 0, str.length());
    }

    @Override
    public int currentLength() {
        return ib$q - Math.min(ia, ib);
    }

    @Override
    public char charAt(int index) {
        return toSplit.charAt(Math.min(ia, ib) + index);
    }

    @Contract(pure = true)
    public boolean contentEquals(String str) {
        int i = Math.min(ia, ib), l = ib$q - i;
        return str.length() == l && str.regionMatches(i, str, 0, l);
    }

    @Contract(pure = true)
    public boolean contentEquals(String str, boolean ignoreCase) {
        int i = Math.min(ia, ib), l = ib$q - i;
        return str.length() == l && toSplit.regionMatches(ignoreCase, i, str, 0, l);
    }

    @Contract(pure = true)
    public int currentHashScreamingSnake() {
        int h = 0;
        for (int i = ia; i < ib$q; i++) {
            char c = toSplit.charAt(i);
            h = 31 * h + (c == '-' ? '_' : Character.toUpperCase(c));
        }
        return h;
    }

    @Contract(pure = true)
    public int currentHash() {
        if (cache != null) return cache.hashCode();
        int h = 0;
        for (int i = ia; i < ib$q; i++) {
            char c = toSplit.charAt(i);
            h = 31 * h + c;
        }
        return h;
    }

    @Contract(pure = true)
    public int currentInt() throws NumberFormatException {
        var i = currentInt(0, 10);
        if (i != 0 || toSplit.indexOf(ia) == '0') return i;
        throw new NumberFormatException("For input string " + toSplit + " @ [" + ia + ", " + ib$q + "] (effectively " + current() + ')');
    }

    @Contract(pure = true)
    public int currentInt(int def, int radix) {
        return parseInt(toSplit, Math.min(ia, ib), ib$q, def, radix);
    }

    @Contract(pure = true)
    public long currentLong() throws NumberFormatException {
        var i = currentLong(0L, 10);
        if (i != 0 || toSplit.indexOf(ia) == '0') return i;
        throw new NumberFormatException("For input string " + toSplit + " @ [" + ia + ", " + ib$q + "] (effectively " + current() + ')');
    }

    @Contract(pure = true)
    public long currentLong(long def, int radix) {
        return parseLong(toSplit, Math.min(ia, ib), ib$q, def, radix);
    }

    @Override
    public long currentDuration() {
        return parseDuration(toSplit, Math.min(ia, ib), ib$q);
    }

    @Contract(pure = true)
    public long tryParseLong(long def, int radix) {
        return StringUtils.tryParseLong(toSplit, Math.min(ia, ib), ib$q, def, radix);
    }

    @Contract(pure = true)
    public long tryParseUnsignedLong(long def, int radix) {
        return StringUtils.tryParseUnsignedLong(toSplit, Math.min(ia, ib), ib$q, def, radix);
    }

    @Contract(pure = true)
    public boolean isEmpty() {
        return ib$q - ia == 1;
    }

    public String rest() {
        seekToNonDelimiter();
        return toSplit.substring(ib);
    }
}
