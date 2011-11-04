package com.google.code.java.core.bytebuffer;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static com.google.code.java.core.bytebuffer.ByteBuffers.readInt;
import static com.google.code.java.core.bytebuffer.ByteBuffers.write;

public class MediaContent {
    private final List<Image> _images = new ArrayList<Image>();
    private int _imageCount = 0;
    private final Media _media;

    public MediaContent() {
        this(new Media());
    }

    public MediaContent(Media media) {
        this._media = media;
    }

    public void clear() {
        for (Image image : _images) {
            image.clear();
        }
        _imageCount = 0;
        _media.clear();
    }

    public void writeTo(ByteBuffer bb) {
        write(bb, _imageCount);
        for (int i = 0; i < _imageCount; i++)
            _images.get(i).writeTo(bb);
        _media.writeTo(bb);
    }

    public void readFrom(ByteBuffer bb) {
        _imageCount = readInt(bb);
        for (int i = 0; i < _imageCount; i++)
            acquireImage(i).readFrom(bb);
        _media.readFrom(bb);
    }

    public Image acquireImage(int index) {
        while (_images.size() <= index) {
            _images.add(new Image());
        }
        if (index >= _imageCount)
            _imageCount = index + 1;
        return _images.get(index);
    }

    public int imageCount() {
        return _imageCount;
    }

    public Media getMedia() {
        return _media;
    }

    public List<Image> getImages() {
        return _images.subList(0, _imageCount);
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getImages().hashCode();
        result = prime * result + _media.hashCode();
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        MediaContent other = (MediaContent) obj;
        if (_images == null) {
            if (other._images != null) return false;
        } else if (!getImages().equals(other.getImages())) return false;
        if (_media == null) {
            if (other._media != null) return false;
        } else if (!_media.equals(other._media)) return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[MediaContent: ");
        sb.append("media=").append(_media);
        sb.append(", images=").append(getImages());
        sb.append("]");
        return sb.toString();
    }

    public void addImage(Image image) {
        acquireImage(_imageCount++).copyOf(image);
    }
}
