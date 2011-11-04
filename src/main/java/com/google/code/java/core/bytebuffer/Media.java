package com.google.code.java.core.bytebuffer;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static com.google.code.java.core.bytebuffer.ByteBuffers.*;

public class Media {
    public enum Player {
        JAVA, FLASH;
    }

    private static final Player[] PLAYERS = Player.values();

    private Player _player;
    private final AsciiText _uri = new AsciiText();
    private final AsciiText _title = new AsciiText();
    private int _width;
    private int _height;
    private final AsciiText _format = new AsciiText();
    private long _duration;
    private long _size;
    private int _bitrate;
    private final List<AsciiText> _persons = new ArrayList<AsciiText>();
    private int _personCount = 0;
    private final AsciiText _copyright = new AsciiText();

    public Media() {
    }

    public Media(String copyright,
                 String format,
                 Player player,
                 String title,
                 String uri,
                 long duration,
                 long size,
                 int height,
                 int width,
                 int bitrate) {
        setCopyright(copyright);
        _duration = duration;
        setFormat(format);
        _height = height;
        _player = player;
        _size = size;
        setTitle(title);
        setUri(uri);
        _width = width;
        _bitrate = bitrate;
    }

    public void clear() {
        _player = null;
        _uri.clear();
        _title.clear();
        _width = 0;
        _height = 0;
        _format.clear();
        _duration = 0;
        _size = 0;
        _personCount = 0;
        _copyright.clear();
    }

    public void writeTo(ByteBuffer bb) {
        write(bb, _player);
        write(bb, _uri);
        write(bb, _title);
        write(bb, _width);
        write(bb, _height);
        write(bb, _format);
        write(bb, _duration);
        write(bb, _size);
        write(bb, _bitrate);
        write(bb, _personCount);
        for (int i = 0; i < _personCount; i++) {
            write(bb, _persons.get(i));
        }
        write(bb, _copyright);
    }

    public void readFrom(ByteBuffer bb) {
        _player = PLAYERS[readOrdinal(bb)];
        _uri.readFrom(bb);
        _title.readFrom(bb);
        _width = readInt(bb);
        _height = readInt(bb);
        _format.readFrom(bb);
        _duration = readLong(bb);
        _size = readLong(bb);
        _bitrate = readInt(bb);
        _personCount = readInt(bb);
        for (int i = 0; i < _personCount; i++)
            acquirePerson(i).readFrom(bb);
        write(bb, _copyright);
    }

    private AsciiText acquirePerson(int index) {
        while (_persons.size() <= index)
            _persons.add(new AsciiText());
        return _persons.get(index);
    }

    public Player getPlayer() {
        return _player;
    }

    public void setPlayer(Player player) {
        _player = player;
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

    public String getFormat() {
        return _format.toString();
    }

    public void setFormat(CharSequence format) {
        _format.copyFrom(format);
    }

    public long getDuration() {
        return _duration;
    }

    public void setDuration(long duration) {
        _duration = duration;
    }

    public long getSize() {
        return _size;
    }

    public void setSize(long size) {
        _size = size;
    }

    public int getBitrate() {
        return _bitrate;
    }

    public void setBitrate(int bitrate) {
        this._bitrate = bitrate;
    }

    public List<CharSequence> getPersons() {
        return (List) _persons.subList(0, _personCount);
    }

    public void setPersons(List<String> p) {
        _personCount = p.size();
        for (int i = 0; i < p.size(); i++)
            setPerson(i, p.get(i));
    }

    private void setPerson(int index, CharSequence name) {
        while (_persons.size() <= index) _persons.add(new AsciiText());
        _persons.get(index).copyFrom(name);
    }

    public void addToPerson(CharSequence person) {
        setPerson(_personCount++, person);
    }

    public String getCopyright() {
        return _copyright.toString();
    }

    public void setCopyright(CharSequence copyright) {
        _copyright.copyFrom(copyright);
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + _bitrate;
        result = prime * result + _copyright.hashCode();
        result = prime * result + (int) (_duration ^ (_duration >>> 32));
        result = prime * result + _format.hashCode();
        result = prime * result + _height;
        result = prime * result + getPersons().hashCode();
        result = prime * result + _player.hashCode();
        result = prime * result + (int) (_size ^ (_size >>> 32));
        result = prime * result + _title.hashCode();
        result = prime * result + _uri.hashCode();
        result = prime * result + _width;
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Media other = (Media) obj;
        if (_bitrate != other._bitrate) return false;
        if (_copyright == null) {
            if (other._copyright != null) return false;
        } else if (!_copyright.equals(other._copyright)) return false;
        if (_duration != other._duration) return false;
        if (_format == null) {
            if (other._format != null) return false;
        } else if (!_format.equals(other._format)) return false;
        if (_height != other._height) return false;
        if (_persons == null) {
            if (other._persons != null) return false;
        } else if (!getPersons().equals(other.getPersons())) return false;
        if (_player == null) {
            if (other._player != null) return false;
        } else if (!_player.equals(other._player)) return false;
        if (_size != other._size) return false;
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
        sb.append("[Media ");
        sb.append("width=").append(_width);
        sb.append(", height=").append(_height);
        sb.append(", duration=").append(_duration);
        sb.append(", size=").append(_size);
        sb.append(", bitrate=").append(_bitrate);
        sb.append(", player=").append(_player);
        sb.append(", uri=").append(_uri);
        sb.append(", title=").append(_title);
        sb.append(", format=").append(_format);
        sb.append(", persons=").append(getPersons());
        sb.append(", copyright=").append(_copyright.toString());
        sb.append("]");
        return sb.toString();
    }
}
