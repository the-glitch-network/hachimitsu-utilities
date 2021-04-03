package net.kjp12.hachimitsu.utilities;

import org.jetbrains.annotations.Contract;

import static net.kjp12.hachimitsu.utilities.StringUtils.*;

public final class StringSpliterator implements IStringSpliterator {
    private final boolean respectQuotes;
    private final String delimiters;
    private final String toSplit;
    private String cache;
    private int ib, ia, ib$q;

    public StringSpliterator(String toSplit) {
        this(toSplit, DEFAULT_DELIMITERS, true);
    }

    public StringSpliterator(String toSplit, String delimiters) {
        this(toSplit, delimiters, true);
    }

    public StringSpliterator(String toSplit, boolean respectQuotes) {
        this(toSplit, DEFAULT_DELIMITERS, respectQuotes);
    }

    public StringSpliterator(String toSplit, String delimiters, boolean respectQuotes) {
        this.toSplit = toSplit.strip();
        this.delimiters = delimiters;
        this.respectQuotes = respectQuotes;
    }

    public boolean hasNext() {
        return ib < toSplit.length();
    }

    private void skipToNonSpace() {
        ib = seekToNonDelimiter(toSplit, delimiters, toSplit.length(), ib);
    }

    private void skipToSpace() {
        ib = seekToDelimiter(toSplit, delimiters, toSplit.length(), ib);
    }

    private void skipToEndQuote(final char quote) {
        ib = seekToEndQuote(toSplit, delimiters, quote, toSplit.length(), ib);
    }

    public void next() {
        if (ib < ia) {
            // We backtracked, we're just going to return the cached value.
            // We will also set index to lastIndex and lastIndex to index as they're swapped anyways on backtracking.
            final int i = ia;
            ia = ib;
            ib = i;
        } else {
            skipToNonSpace();
            ia = ib;
            if (respectQuotes) {
                final char c = toSplit.charAt(ib);
                if (QUOTES.indexOf(c) != -1) {
                    ia++;
                    ib++;
                    skipToEndQuote(c);
                    ib$q = ib++;
                } else if (QUOTE_PAIRED.indexOf(c) != -1) {
                    ia++;
                    ib++;
                    skipToEndQuote((char) (c + 1));
                    ib$q = ib++;
                } else {
                    skipToSpace();
                    ib$q = ib;
                }
            } else {
                skipToSpace();
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
        return str.length() == l && str.regionMatches(ignoreCase, i, str, 0, l);
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
        skipToNonSpace();
        return toSplit.substring(ib);
    }
}
