package net.kjp12.hachimitsu.utilities;// Created 2021-13-12T16:12:25

import static net.kjp12.hachimitsu.utilities.StringUtils.*;

/**
 * @author KJP12
 * @since ${version}
 **/
public class ByteUtils {
    static final byte[]
            ARRAY_DEFAULT_DELIMITERS = {' ', '\t', '\n', 0, '\f', '\r', 0, 0},
            ARRAY_QUOTES = {'`', 0, '"', '\''},
            ARRAY_HEX_DIGITS = {-1, 'A', 'B', 'C', 'D', 'E', 'F', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 'a', 'b', 'c', 'd', 'e', 'f', 0, 0, 0, 0, 0, 0, 0, 0, 0, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 0, 0, 0, 0, 0, 0};
    public static final String QUOTES_GYPPED = "\"'`";

    public static boolean equals(final byte[] toSplit, final byte[] delimiters, final int lim, int ib) {
        final int mask = delimiters.length - 1;
        byte c;
        while (ib < lim) {
            if (delimiters[(c = toSplit[ib++]) & mask] != c) return false;
        }
        return true;
    }

    public static int seekToDelimiter(final byte[] toSplit, final byte[] delimiters, final int lim, int ib) {
        final int mask = delimiters.length - 1;
        byte c;
        while (ib < lim && delimiters[(c = toSplit[ib]) & mask] != c)
            ib++;
        return ib;
    }

    public static int seekToNonDelimiter(final byte[] toSplit, final byte[] delimiters, final int lim, int ib) {
        final int mask = delimiters.length - 1;
        byte c;
        while (ib < lim && delimiters[(c = toSplit[ib]) & mask] == c)
            ib++;
        return ib;
    }

    public static int seekToEndQuote(final byte[] toSplit, final byte[] delimiters, final byte quote, final int lim, int ib) {
        final int mask = delimiters.length - 1;
        byte c;
        while (ib < lim && (toSplit[ib] != quote || (ib + 1 < lim && delimiters[(c = toSplit[ib + 1]) & mask] != c)))
            ib++;
        return ib;
    }

    // public static boolean regionMatches(final byte[] split, final int splitIndex, final int splitLimit,
    //                                     final String match, final int matchIndex, final int length,
    //                                     final boolean ignoreCase) {
    //     if(length > splitLimit - splitIndex) return false;
    //     // warning: ia can be incremented by n in a single round while ib will only increment by 1.
    //     for(int ia = splitIndex, ib = matchIndex, bl = ib + length; ib < bl; ib++ /*ia can't be auto incremented*/) {
    //         if(ia >= splitLimit) return false;
    //         if((split[ia] & 128) == 0) {
    //             if(split[ia++] != match.charAt(ib)) return false;
    //         } else {
    //             // slow path
    //             long utf = decodeUtf8(split, ia, splitLimit);
    //             if(utf == -1L) return false; // we literally cannot match.
    //             ia += utf >>> 32;
    //             int codepoint = (int) utf;
    //             if (!Character.isValidCodePoint(codepoint)) return false; // we literally cannot match.
    //             if (Character.isSupplementaryCodePoint(codepoint)) {
    //                 if(match.charAt(ib++) != Character.highSurrogate(codepoint) || match.charAt(ib) != Character.lowSurrogate(codepoint)) return false;
    //             } else {
    //                 if(match.charAt(ib) != codepoint) return false;
    //             }
    //         }
    //     }
    //     return true;
    // }

    public static boolean regionMatches(final byte[] s, int si, final int sl,
                                        final String m, int mi, final int ml,
                                        final boolean isStart) {
        if (ml > sl - si) return false;

        for (; mi < ml; mi++) {
            if (si >= sl) return false;
            if ((s[si] & 128) == 0) {
                if (s[si++] != m.charAt(mi)) return false;
            } else {
                long utf = decodeUtf8(s, si, sl);
                if (utf == -1L) return false;
                si += utf >>> 32; // ?
                int codepoint = (int) utf;
                if (!Character.isValidCodePoint(codepoint)) return false;
                if (Character.isSupplementaryCodePoint(codepoint) ?
                        m.charAt(mi++) != Character.highSurrogate(codepoint) || m.charAt(mi) != Character.lowSurrogate(codepoint) :
                        m.charAt(mi) != codepoint) {
                    return false;
                }
            }
        }
        return isStart || si == sl;
    }

    public static boolean regionMatchesIgnoreCase(final byte[] s, int si, final int sl,
                                                  final String m, int mi, final int ml,
                                                  final boolean isStart) {
        if (ml > sl - si) return false;

        for (; mi < ml; mi++) {
            if (si >= sl) return false;
            if ((s[si] & 128) == 0) {
                char c1 = (char) s[si++];
                char c2 = m.charAt(mi);
                if (Character.toUpperCase(c1) != Character.toUpperCase(c2) && Character.toLowerCase(c1) != Character.toLowerCase(c2))
                    return false;
            } else {
                long utf = decodeUtf8(s, si, sl);
                if (utf == -1L) return false;
                si += utf >>> 32; // ?
                int c1 = (int) utf;
                if (!Character.isValidCodePoint(c1)) return false;
                int c2 = Character.isSupplementaryCodePoint(c1) ? m.codePointAt(mi++) : m.charAt(mi);
                if (Character.toUpperCase(c1) != Character.toUpperCase(c2) && Character.toLowerCase(c1) != Character.toLowerCase(c2))
                    return false;
            }
        }
        return isStart || si == sl;
    }

    public static int parseInt(final byte[] s, int si, final int sl, final int def, final int radix) {
        boolean negative = s[si] == '-';
        int ret = Integer.MIN_VALUE;
        if (negative || s[si] == '+') {
            si++;
        }
        for (; si < sl; si++) {
            int i = Character.digit(s[si], radix);
            if (i < 0) return def;
            ret = ret * radix + i;
        }
        return negative ? ret : -ret;
    }

    public static long parseLong(final byte[] s, int si, final int sl, final long def, final int radix) {
        boolean negative = s[si] == '-';
        long ret = Long.MIN_VALUE;
        if (negative || s[si] == '+') {
            si++;
        }
        for (; si < sl; si++) {
            int i = Character.digit(s[si], radix);
            if (i < 0) return def;
            ret = ret * radix + i;
        }
        return negative ? ret : -ret;
    }

    /**
     * @param toSplit The byte array to decode off of.
     * @param ib      Index of the UTF-8 character.
     * @return Length &amp; codepoint encoded at the high &amp; low 32 bits respectively
     */
    public static long decodeUtf8(final byte[] toSplit, final int ib, final int il) {
        // we inflated into an int, so 24 bits didn't exist prior.
        int len = Integer.numberOfLeadingZeros(~toSplit[ib]) - 24 + 1;
        if (ib + len > il) return -1L; // We cannot continue.
        int codepoint = toSplit[ib] & (Integer.highestOneBit(~toSplit[ib]) - 1);
        for (int i = ib + 1, l = ib + len; i < l; i++) {
            if ((toSplit[i] & 0b11000000) != 0b10000000) {
                len = i - ib;
                break;
            }
            codepoint <<= 6;
            codepoint |= toSplit[i] & 0b00111111;
        }
        return ((long) len << 32L) | codepoint;
    }

    /**
     * Creates a hash char array for use with the char array versions of the seek methods.
     *
     * @param in The input string to create the hash char array.
     * @return The hash char array.
     */
    public static byte[] createByteHashArray(String in) {
        // Short-circuit for pre-made hash arrays.
        switch (in) {
            case DEFAULT_DELIMITERS:
                return ARRAY_DEFAULT_DELIMITERS.clone();
            case QUOTES:
            case QUOTES_GYPPED:
                return ARRAY_QUOTES.clone();
            case QUOTE_PAIRED:
                throw new IllegalArgumentException("Quote paired cannot be used for byte hash arrays.");
        }
        int len = Integer.highestOneBit(in.length());
        if (len != in.length()) len <<= 1;
        int m = len - 1;
        var out = new byte[len];
        out[0] = -1;
        for (int i = 0, il = in.length(); i < il; i++) {
            var c = in.charAt(i);
            if (c >>> 7 != 0) throw new IllegalArgumentException("Invalid character at " + i + ": " + c + "; " + in);
            int f = c & m;
            while (out[f] != (f == 0 ? -1 : 0) && out[f] != c) {
                f = c & (m = (len <<= 1) - 1);
                var arr = out;
                out = new byte[len];
                out[0] = -1;
                if (arr[0] != -1) out[arr[0] & m] = arr[0];
                for (int b = 1, bl = arr.length; b < bl; b++) {
                    if (arr[b] != 0) out[arr[b] & m] = arr[b];
                }
            }
            out[f] = (byte) c;
        }
        return out;
    }
}
