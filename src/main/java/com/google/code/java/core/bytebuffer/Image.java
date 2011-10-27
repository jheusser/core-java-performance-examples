package com.google.code.java.core.bytebuffer;

import java.nio.ByteBuffer;

import static com.google.code.java.core.bytebuffer.ByteBuffers.*;

public class Image {
    public enum Size {
        SMALL, LARGE
    }

    private static final Size[] SIZES = Size.values();

    private final AsciiText _uri = new AsciiText();
    private final AsciiText _title = new AsciiText();
    private int _width;
    private int _height;
    private Size _size;

    public Image() {
    }


    public Image(int height, String title, String uri, int width, Size size) {
        super();
        _height = height;
        setTitle(title);
        setUri(uri);
        _width = width;
        _size = size;
    }

    public void copyOf(Image image) {
        _uri.copyFrom(image._uri);
        _title.copyFrom(image._title);
        setWidth(image.getWidth());
        setHeight(image.getHeight());
        setSize(image.getSize());
    }

    public void clear() {
        _uri.clear();
        _title.clear();
        _width = 0;
        _height = 0;
        _size = null;
    }


    public void writeTo(ByteBuffer bb) {
        write(bb, _uri);
        write(bb, _title);
        write(bb, _width);
        write(bb, _height);
        write(bb, _size);
    }

    public void readFrom(ByteBuffer bb) {
        _uri.readFrom(bb);
        _title.readFrom(bb);
        _width = readInt(bb);
        _height = readInt(bb);
        _size = SIZES[readOrdinal(bb)];
    }

    public String getUri() {
        return _uri.toString();
    }

    public void setUri(CharSequence uri) {
        _uri.copyFrom(uri);
    }

    public String getTitle() {
        return _title.toString();
    }

    public void setTitle(CharSequence title) {
        _title.copyFrom(title);
    }

    public int getWidth() {
        return _width;
    }

    public void setWidth(int width) {
        _width = width;
    }

    public int getHeight() {
        return _height;
    }

    public void setHeight(int height) {
        _height = height;
    }

    public Size getSize() {
        return _size;
    }

    public void setSize(Size size) {
        this._size = size;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + _height;
        result = prime * result + ((_size == null) ? 0 : _size.hashCode());
        result = prime * result + _title.hashCode();
        result = prime * result + _uri.hashCode();
        result = prime * result + _width;
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Image other = (Image) obj;
        if (_height != other._height) return false;
        if (_size == null) {
            if (other._size != null) return false;
        } else if (!_size.equals(other._size)) return false;
        if (_title == null) {
            if (other._title != null) return false;
        } else if (!_title.equals(other._title)) return false;
        if (_uri == null) {
            if (other._uri != null) return false;
        } else if (!_uri.equals(other._uri)) return false;
        if (_width != other._width) return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[Image ");
        sb.append("width=").append(_width);
        sb.append(", height=").append(_height);
        sb.append(", uri=").append(_uri);
        sb.append(", title=").append(_title);
        sb.append(", size=").append(_size);
        sb.append("]");
        return sb.toString();
    }
}
