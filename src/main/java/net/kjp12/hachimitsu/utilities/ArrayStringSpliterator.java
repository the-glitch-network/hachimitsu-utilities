package net.kjp12.hachimitsu.utilities;// Created 2021-03-18T01:47:48

/**
 * @author KJP12
 * @since 0.0.0
 */
public final class ArrayStringSpliterator implements IStringSpliterator {
	private final String[] array;
	private int ia;

	public ArrayStringSpliterator(String[] array) {
		this.array = array;
	}

	@Override
	public boolean contentEquals(String str) {
		return array[ia].equals(str);
	}

	@Override
	public boolean contentEquals(String str, boolean ignoreCase) {
		return ignoreCase ? array[ia].equalsIgnoreCase(str) : array[ia].equals(str);
	}

	@Override
	public boolean startsWith(String str) {
		return array[ia].startsWith(str);
	}

	@Override
	public boolean startsWith(String str, boolean ignoreCase) {
		var tmp = array[ia];
		return str.length() <= tmp.length() && tmp.regionMatches(ignoreCase, 0, str, 0, str.length());
	}

	@Override
	public int currentLength() {
		return array[ia].length();
	}

	@Override
	public char charAt(int index) {
		return array[ia].charAt(index);
	}

	@Override
	public boolean isEmpty() {
		return array[ia].isEmpty();
	}

	@Override
	public void backtrack() {
		if (ia != 0) ia--;
	}

	@Override
	public boolean hasNext() {
		return ia < array.length;
	}

	@Override
	public void next() {
		if (hasNext()) ia++;
	}

	@Override
	public String current() {
		return array[ia];
	}

	@Override
	public int currentHash() {
		return array[ia].hashCode();
	}

	@Override
	public int currentHashScreamingSnake() {
		var tmp = array[ia];
		int h = 0;
		for (int i = 0, l = tmp.length(); i < l; i++) {
			char c = tmp.charAt(i);
			h = 31 * h + (c == '-' ? '_' : Character.toUpperCase(c));
		}
		return h;
	}

	@Override
	public int currentInt() {
		return Integer.parseInt(array[ia]);
	}

	@Override
	public int currentInt(int def, int radix) {
		var tmp = array[ia];
		return StringUtils.parseInt(tmp, 0, tmp.length(), def, radix);
	}

	@Override
	public long currentLong() {
		return Long.parseLong(array[ia]);
	}

	@Override
	public long currentLong(long def, int radix) {
		var tmp = array[ia];
		return StringUtils.parseLong(tmp, 0, tmp.length(), def, radix);
	}

	@Override
	public long currentDuration() {
		return StringUtils.parseDuration(array[ia]);
	}

	@Override
	public long tryParseLong(long def, int radix) {
		var tmp = array[ia];
		return StringUtils.tryParseLong(tmp, 0, tmp.length(), def, radix);
	}

	@Override
	public long tryParseUnsignedLong(long def, int radix) {
		var tmp = array[ia];
		return StringUtils.tryParseUnsignedLong(tmp, 0, tmp.length(), def, radix);
	}

	@Override
	public String rest() {
		var builder = new StringBuilder();
		for (int i = ia, l = array.length; i < l; i++) builder.append(array[i]).append(' ');
		return builder.substring(0, builder.length() - 1);
	}
}
