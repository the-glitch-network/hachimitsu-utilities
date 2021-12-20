package net.kjp12.hachimitsu.utilities;// Created 2021-14-12T23:23:57

import java.io.IOException;
import java.io.InputStream;

/**
 * @author KJP12
 * @since ${version}
 **/
public final class StreamStringSpliterator extends ByteStringSpliterator {
    private InputStream toSplit;

    public StreamStringSpliterator(InputStream toSplit) {
        this(toSplit, StringUtils.DEFAULT_DELIMITERS, true);
    }

    public StreamStringSpliterator(InputStream toSplit, String delimiters) {
        this(toSplit, delimiters, true);
    }

    public StreamStringSpliterator(InputStream toSplit, boolean respectQuotes) {
        this(toSplit, StringUtils.DEFAULT_DELIMITERS, respectQuotes);
    }

    public StreamStringSpliterator(InputStream toSplit, String delimiters, boolean respectQuotes) {
        this(toSplit, ByteUtils.createByteHashArray(delimiters), respectQuotes);
    }

    public StreamStringSpliterator(InputStream toSplit, byte[] delimiters, boolean respectQuotes) {
        super(new byte[1024], delimiters, respectQuotes);
        this.toSplit = toSplit;
        il = 0;
    }

    public void seekToNonDelimiter() {
        super.seekToNonDelimiter();
        if (ib == il - 1 && delimiters[super.toSplit[ib] & delimiters.length - 1] == super.toSplit[ib]) {
            read();
            super.seekToNonDelimiter();
        }
    }

    public void seekToDelimiter() {
        super.seekToDelimiter();
        if (ib == il - 1 && delimiters[super.toSplit[ib] & delimiters.length - 1] != super.toSplit[ib]) {
            read();
            super.seekToDelimiter();
        }
    }

    public void seekToEndQuote(final byte quote) {
        super.seekToEndQuote(quote);
        if (ib == il - 1 && super.toSplit[ib] != quote) {
            read();
            super.seekToEndQuote(quote);
        }
    }

    private boolean read() {
        if (this.toSplit == null) return false;
        int i = Math.min(ia, ib), r, rl;
        System.arraycopy(super.toSplit, i, super.toSplit, 0, il - i);
        il -= i;
        ia = ib = 0;
        try {
            rl = super.toSplit.length - il;
            il += r = this.toSplit.readNBytes(super.toSplit, il, rl);
            if (r != rl) try {
                // The stream has ended.
                InputStream stream = this.toSplit;
                this.toSplit = null;
                stream.close();
            } catch (IOException ignored) {
            }
        } catch (IOException ioe) {
            // TODO:
            ioe.printStackTrace();
            try {
                InputStream stream = this.toSplit;
                this.toSplit = null;
                stream.close();
            } catch (IOException ignored) {
            }
        }
        return super.hasNext();
    }

    @Override
    public boolean hasNext() {
        return super.hasNext() || this.read();
    }
}
