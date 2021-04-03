package net.kjp12.hachimitsu.utilities;// Created Feb. 07, 2021 @ 04:17

import java.util.concurrent.TimeUnit;

/**
 * General string utility class.
 *
 * @author KJP12
 * @since 0.0.0
 **/
public final class StringUtils {
    public static final String DEFAULT_DELIMITERS = " \t\n\r\f", QUOTES = "\"'`”’", QUOTE_PAIRED = "“‘「『｢";

    private StringUtils() {
    }

    public static int seekToDelimiter(final String toSplit, final String delimiters, final int lim, int ib) {
        while (ib < lim && delimiters.indexOf(toSplit.charAt(ib)) == -1)
            ib++;
        return ib;
    }

    public static int seekToNonDelimiter(final String toSplit, final String delimiters, final int lim, int ib) {
        while (ib < lim && delimiters.indexOf(toSplit.charAt(ib)) != -1)
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
        return switch (unit) {
            case 'D', 'd' -> TimeUnit.DAYS;
            case 'H', 'h' -> TimeUnit.HOURS;
            case 'M', 'm' -> TimeUnit.MINUTES;
            case 'S', 's' -> TimeUnit.SECONDS;
            case 'N', 'n' -> TimeUnit.NANOSECONDS;
            default -> TimeUnit.MILLISECONDS;
        };
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
}
