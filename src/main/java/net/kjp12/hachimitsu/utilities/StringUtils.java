package net.kjp12.hachimitsu.utilities;// Created Feb. 07, 2021 @ 04:17

import java.util.concurrent.TimeUnit;

/**
 * General string utility class.
 *
 * @author KJP12
 * @since 0.0.0
 **/
public final class StringUtils {
    static final char[]
            ARRAY_DEFAULT_DELIMITERS = {' ', '\t', '\n', 0, '\f', '\r', 0, 0},
            ARRAY_QUOTES = {'`', '’', '"', 0, 0, '”', 0, '\''},
            ARRAY_QUOTE_PAIRED = {'\uFFFF', 0, '｢', 0, 0, 0, 0, 0, 0, 0, 0, 0, '「', 0, '『', 0, 0, 0, 0, 0, 0, 0, 0, 0, '‘', 0, 0, 0, '“', 0, 0, 0};
    public static final String DEFAULT_DELIMITERS = " \t\n\r\f", QUOTES = "\"'`”’", QUOTE_PAIRED = "“‘「『｢";

    private StringUtils() {
    }

    public static int seekToDelimiter(final String toSplit, final char[] delimiters, final int lim, int ib) {
        final int mask = delimiters.length - 1;
        char c;
        while (ib < lim && delimiters[(c = toSplit.charAt(ib)) & mask] != c)
            ib++;
        return ib;
    }

    public static int seekToDelimiter(final String toSplit, final String delimiters, final int lim, int ib) {
        while (ib < lim && delimiters.indexOf(toSplit.charAt(ib)) == -1)
            ib++;
        return ib;
    }

    public static int seekToNonDelimiter(final String toSplit, final char[] delimiters, final int lim, int ib) {
        final int mask = delimiters.length - 1;
        char c;
        while (ib < lim && delimiters[(c = toSplit.charAt(ib)) & mask] == c)
            ib++;
        return ib;
    }

    public static int seekToNonDelimiter(final String toSplit, final String delimiters, final int lim, int ib) {
        while (ib < lim && delimiters.indexOf(toSplit.charAt(ib)) != -1)
            ib++;
        return ib;
    }

    public static int seekToEndQuote(final String toSplit, final char[] delimiters, final char quote, final int lim, int ib) {
        final int mask = delimiters.length - 1;
        char c;
        while (ib < lim && (toSplit.charAt(ib) != quote || (ib + 1 < lim && delimiters[(c = toSplit.charAt(ib + 1)) & mask] != c)))
            ib++;
        return ib;
    }

    public static int seekToEndQuote(final String toSplit, final String delimiters, final char quote, final int lim, int ib) {
        while (ib < lim &&
                (toSplit.charAt(ib) != quote || (ib + 1 < lim && delimiters.indexOf(toSplit.charAt(ib + 1)) == -1)))
            ib++;
        return ib;
    }

    public static int parseInt(final String str, final int s, final int e, final int def, final int radix) {
        if (s >= e) return def;
        var n = str.charAt(s) == '-';
        return (int) parseLogic(n, n ? Integer.MIN_VALUE : -Integer.MAX_VALUE, str, s, e, def, radix);
    }

    public static long parseLong(final String str, final int s, final int e, final long def, final int radix) {
        if (s >= e) return def;
        var n = str.charAt(s) == '-';
        return parseLogic(n, n ? Long.MIN_VALUE : -Long.MAX_VALUE, str, s, e, def, radix);
    }

    private static long parseLogic(final boolean n, final long lim, final String str, final int s, final int e, final long def, final int radix) {
        var mmi = lim / radix;
        var r = 0L;
        for (int i = n || str.charAt(s) == '+' ? s + 1 : s; i < e; i++) {
            var c = str.charAt(i);
            if (c == '_') continue; // We'll just ignore this.
            var d = Character.digit(c, radix);
            if (d < 0 || r < mmi) return def;
            r *= radix;
            if (r < lim + d) return def;
            r -= d;
        }
        return n ? r : -r;
    }

    public static long tryParseLong(final String str, int i, final int e, final long def, final int radix) {
        for (; i < e; i++) {
            var c = str.charAt(i);
            if (c == '+' || c == '-' || Character.digit(c, radix) >= 0) break;
        }
        if (i >= e) return def;
        var n = str.charAt(i) == '-';
        var lim = n ? Long.MIN_VALUE : -Long.MAX_VALUE;
        var mmi = lim / radix;
        var r = 0L;
        if (n || str.charAt(i) == '+') i++;
        for (; i < e; i++) {
            var c = str.charAt(i);
            if (c == '_') continue; // We'll just ignore this.
            var d = Character.digit(c, radix);
            if (d < 0 || r < mmi)
                break;
            r *= radix;
            if (r < lim + d)
                break;
            r -= d;
        }
        return n ? r : -r;
    }

    public static long tryParseUnsignedLong(final String str, int i, final int e, final long def, final int radix) {
        for (; i < e; i++) {
            var c = str.charAt(i);
            if (Character.digit(c, radix) >= 0) break;
        }
        if (i >= e) return def;
        long r = 0L, l;
        for (; i < e; i++) {
            var c = str.charAt(i);
            if (c == '_') continue; // We'll just ignore this.
            var d = Character.digit(c, radix);
            if (d < 0) break;
            l = r;
            r = r * radix + d;
            int g = radix * (int) (r >> 57);
            if (g >= 128 || r >= 0 && g >= 128 - Character.MAX_RADIX) {
                return l;
            }
        }
        return r;
    }

    public static TimeUnit getTimeUnit(char unit) {
        switch (unit) {
            case 'D':
            case 'd':
                return TimeUnit.DAYS;
            case 'H':
            case 'h':
                return TimeUnit.HOURS;
            case 'M':
            case 'm':
                return TimeUnit.MINUTES;
            case 'S':
            case 's':
                return TimeUnit.SECONDS;
            case 'N':
            case 'n':
                return TimeUnit.NANOSECONDS;
            default:
                return TimeUnit.MILLISECONDS;
        }
    }

    public static long parseDuration(String str) {
        return parseDuration(str, 0, str.length());
    }

    public static long parseDuration(String str, int i, final int l) {
        long t = 0, w = 0;
        for (; i < l; i++) {
            char c = str.charAt(i);
            if (c >= '0' && c <= '9') {
                w += c - '0';
            } else {
                t += getTimeUnit(c).toMillis(w);
                w = 0;
            }
        }
        return t;
    }

    /**
     * Creates a hash char array for use with the char array versions of the seek methods.
     *
     * @param in The input string to create the hash char array.
     * @return The hash char array.
     */
    public static char[] createCharHashArray(String in) {
        // Short-circuit for pre-made hash arrays.
        switch (in) {
            case DEFAULT_DELIMITERS:
                return ARRAY_DEFAULT_DELIMITERS.clone();
            case QUOTES:
                return ARRAY_QUOTES.clone();
            case QUOTE_PAIRED:
                return ARRAY_QUOTE_PAIRED.clone();
        }
        int len = Integer.highestOneBit(in.length());
        if (len != in.length()) len <<= 1;
        int m = len - 1;
        var out = new char[len];
        out[0] = '\uFFFF';
        for (int i = 0, il = in.length(); i < il; i++) {
            var c = in.charAt(i);
            int f = c & m;
            while (out[f] != (f == 0 ? '\uFFFF' : 0) && out[f] != c) {
                f = c & (m = (len <<= 1) - 1);
                var arr = out;
                out = new char[len];
                out[0] = '\uFFFF';
                if (arr[0] != '\uFFFF') out[arr[0] & m] = arr[0];
                for (int b = 1, bl = arr.length; b < bl; b++) {
                    if (arr[b] != 0) out[arr[b] & m] = arr[b];
                }
            }
            out[f] = c;
        }
        return out;
    }
}
