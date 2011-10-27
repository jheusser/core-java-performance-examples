package com.google.code.java.core.bytebuffer;

import java.nio.ByteBuffer;

/**
 * @author peter.lawrey
 */
public class AsciiText implements CharSequence {
    private final byte[] values = new byte[128];
    private byte length = 0;

    public AsciiText() {
    }

    public int length() {
        return length;
    }

    public char charAt(int index) {
        if (index >= length) throw new IndexOutOfBoundsException();
        return (char) (values[index] & 0xFF);
    }

    public CharSequence subSequence(int start, int end) {
        return toString().subSequence(start, end);
    }

    @Override
    public String toString() {
        return length < 0 ? null : new String(values, 0, length);
    }

    public void copyFrom(CharSequence s) {
        if (s instanceof AsciiText) {
            copyFrom((AsciiText) s);
            return;
        }
        if (s == null) {
            length = -1;
            return;
        }
        if (s.length() > values.length) throw new IllegalStateException("String too long.");
        length = (byte) s.length();
        for (int i = 0; i < length; i++)
            values[i] = (byte) s.charAt(i);
    }

    public void copyFrom(AsciiText s) {
        length = s.length;
        if (length > 0)
            System.arraycopy(s.values, 0, values, 0, length);
    }

    public void readFrom(ByteBuffer bb) {
        length = bb.get();
        if (length > 0)
            bb.get(values, 0, length);
    }

    public void writeTo(ByteBuffer bb) {
        bb.put(length);
        if (length > 0)
            bb.put(values, 0, length);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        for (int i = 0; i < length; i++)
            hash = 31 * hash + (values[i] & 0xFF);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AsciiText)) return false;
        if (obj == this) return true;
        AsciiText other = (AsciiText) obj;
        if (length != other.length) return false;
        for (int i = 0; i < length; i++)
            if (values[i] != other.values[i]) return false;
        return true;
    }

    public void clear() {
        length = -1;
    }

    public static AsciiText of(CharSequence s) {
        AsciiText text = new AsciiText();
        text.copyFrom(s);
        return text;
    }
}
