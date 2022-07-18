package net.kjp12.hachimitsu.utilities;

import org.jetbrains.annotations.Contract;

import java.util.UUID;

import static net.kjp12.hachimitsu.utilities.StringUtils.*;

public final class StringSpliterator implements IStringSpliterator {
	private final boolean respectQuotes;
	private final String delimiters;
	private final String toSplit;
	private String cache;
	private StringBuilder anchor;
	private int ib, ia, ib$q, il, ai;

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
		this.toSplit = toSplit;
		this.delimiters = delimiters;
		this.respectQuotes = respectQuotes;
		il = toSplit.length();
	}

	public boolean hasNext() {
		seekToNonDelimiter();
		return ib < il;
	}

	/* Doesn't fast forward, leaving the space intact. */
	public boolean hasPotential() {
		return ib < il;
	}

	public void seekToNonDelimiter() {
		ib = StringUtils.seekToNonDelimiter(toSplit, delimiters, toSplit.length(), ib);
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
		} else if (hasNext()) {
			ia = ib;
			if (respectQuotes) {
				final char c = toSplit.charAt(ib);
				if (QUOTES.indexOf(c) != -1) {
					ia++;
					ib++;
					seekToEndQuote(c);
					ib$q = ib++;
				} else if (QUOTE_PAIRED.indexOf(c) != -1) {
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
		return str.length() <= l && toSplit.regionMatches(ignoreCase, i, str, 0, str.length());
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
		return str.length() == l && toSplit.regionMatches(i, str, 0, l);
	}

	@Contract(pure = true)
	public boolean contentEquals(String str, boolean ignoreCase) {
		int i = Math.min(ia, ib), l = ib$q - i;
		return str.length() == l && toSplit.regionMatches(ignoreCase, i, str, 0, l);
	}

	@Contract(pure = true)
	public boolean regionMatches(boolean ignoreCase, int to, String str, int so, int sl) {
		int i = Math.min(ia, ib), l = ib$q - i;
		return l == sl && sl <= str.length() && toSplit.regionMatches(ignoreCase, i + to, str, so, sl);
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
		if (i != 0 || toSplit.charAt(ia) == '0') return i;
		throw new NumberFormatException("For input string " + toSplit + " @ [" + ia + ", " + ib$q + "] (effectively " + current() + ')');
	}

	@Contract(pure = true)
	public int currentInt(int def, int radix) {
		return parseInt(toSplit, Math.min(ia, ib), ib$q, def, radix);
	}

	@Contract(pure = true)
	public long currentLong() throws NumberFormatException {
		var i = currentLong(0L, 10);
		if (i != 0 || toSplit.charAt(ia) == '0') return i;
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

	@Override
	public String toString() {
		return "StringSpliterator{" +
			"respectQuotes=" + respectQuotes +
			", delimiters='" + delimiters + '\'' +
			", toSplit='" + toSplit + '\'' +
			", cache='" + cache + '\'' +
			", ib=" + ib +
			", ia=" + ia +
			", ib$q=" + ib$q +
			'}';
	}

	public void anchor(int buf) {
		if (hasNext()) {
			next();
			ai = Math.min(ia, ib);
			anchor = new StringBuilder(ai + buf).append(toSplit, 0, ai);
		} else {
			ai = ib;
			anchor = new StringBuilder(ai + buf).append(toSplit);
			if (ai != 0 && delimiters.indexOf(toSplit.charAt(ai - 1)) == -1) {
				anchor.append(' ');
				ai++;
			}
		}
	}

	public boolean tryTestAnchor(String str) {
		if (ai >= ib || regionMatches(true, 0, str, 0, currentLength())) {
			anchor.replace(ai, anchor.length(), str);
			return true;
		}
		return false;
	}

	public String getAnchor() {
		return anchor.toString();
	}

	public boolean isUUID() { // 8 4 4 4 12
		var i = Math.min(ia, ib);
		return ib$q - i == 36 &&
			toSplit.charAt(i + 8) == '-' &&
			toSplit.charAt(i + 13) == '-' &&
			toSplit.charAt(i + 18) == '-' &&
			toSplit.charAt(i + 23) == '-' &&
			StringUtils.equals(toSplit, ARRAY_HEX_DIGITS, i + 8, i) &&
			StringUtils.equals(toSplit, ARRAY_HEX_DIGITS, i + 13, i + 9) &&
			StringUtils.equals(toSplit, ARRAY_HEX_DIGITS, i + 18, i + 14) &&
			StringUtils.equals(toSplit, ARRAY_HEX_DIGITS, i + 23, i + 19) &&
			StringUtils.equals(toSplit, ARRAY_HEX_DIGITS, i + 36, i + 24);
	}

	public UUID currentUUID() {
		var i = Math.min(ia, ib);
		long major = (parseLong(toSplit, i, i + 8, 0, 16) << 32) | (parseLong(toSplit, i + 9, i + 13, 0, 16) << 16) | (parseLong(toSplit, i + 14, i + 18, 0, 16));
		long minor = (parseLong(toSplit, i + 19, i + 23, 0, 16) << 48) | (parseLong(toSplit, i + 24, i + 36, 0, 16));
		return new UUID(major, minor);
	}
}
