package com.google.code.java.core.bytebuffer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author peter.lawrey
 */
public class ByteBufferSpecificSerializer /*implements ObjectSerializer<MediaContent>*/ {
    public static final AsciiText MPG4 = AsciiText.of("video/mpg4");
    public static final AsciiText KEYNOTE_TITLE = AsciiText.of("Javaone Keynote");
    public static final AsciiText KEYNOTE_URI = AsciiText.of("http://javaone.com/keynote.mpg");
    public static final AsciiText BILL_GATES = AsciiText.of("Bill Gates");
    public static final AsciiText STEVE_JOBS = AsciiText.of("Steve Jobs");
    public static final AsciiText KEYNOTE_LARGE = AsciiText.of("http://javaone.com/keynote_large.jpg");
    public static final AsciiText KEYNOTE_SMALL = AsciiText.of("http://javaone.com/keynote_thumbnail.jpg");
    final MediaContent created = new MediaContent();
    final MediaContent deserialized = new MediaContent();

    final ByteBuffer bb = ByteBuffer.allocateDirect(4 * 1024).order(ByteOrder.nativeOrder());
    byte[] cachedBytes = {};

    public MediaContent deserialize(byte[] array) throws Exception {
        bb.clear();
        bb.put(array);
        bb.flip();

        deserialized.clear();
        deserialized.readFrom(bb);
        return deserialized;
    }

    public byte[] serialize(MediaContent content) throws Exception {
        bb.clear();
        content.writeTo(bb);
        bb.flip();
        // if you use NIO, a byte[] isn't required.
        byte[] bytes = bb.remaining() == cachedBytes.length ? cachedBytes : (cachedBytes = new byte[bb.remaining()]);
        bb.get(bytes);
        return bytes;
    }

    public String getName() {
        return "ByteBuffer-specific";
    }

    public MediaContent create() throws Exception {
        created.clear();
        Media media = created.getMedia();
        media.setCopyright(null);
        media.setFormat(MPG4);
        media.setPlayer(Media.Player.JAVA);
        media.setTitle(KEYNOTE_TITLE);
        media.setUri(KEYNOTE_URI);
        media.setDuration(1234567);
        media.setSize(123);
        media.setHeight(0);
        media.setWidth(0);
        media.setBitrate(0);
        media.addToPerson(BILL_GATES);
        media.addToPerson(STEVE_JOBS);
        MediaContent content = new MediaContent(media);
        Image image0 = content.acquireImage(0);
        image0.setHeight(0);
        image0.setTitle(KEYNOTE_TITLE);
        image0.setUri(KEYNOTE_LARGE);
        image0.setWidth(0);
        image0.setSize(Image.Size.LARGE);
        Image image1 = content.acquireImage(1);
        image1.setHeight(0);
        image1.setTitle(KEYNOTE_TITLE);
        image1.setUri(KEYNOTE_SMALL);
        image1.setWidth(0);
        image1.setSize(Image.Size.SMALL);
        return content;
    }
}
