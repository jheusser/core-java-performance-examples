package com.google.code.java.core.humanreadablebinary;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ComparingHumanReadableToBinaryMain {
    public static void main(String... args) throws IOException {
        List<Long> longs = new ArrayList<Long>();
        for (long i = -1; i <= 10; i++)
            longs.add(i);
        String asText = longs.toString();
        byte[] bytes1 = asText.getBytes();
        System.out.println("As text:  " + bytes1.length + " bytes long, " + asText);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(longs);
        oos.close();
        byte[] bytes2 = baos.toByteArray();
        System.out.println("As binary: " + bytes2.length + " bytes long, "
                + new String(bytes2, 0).replaceAll("[^\\p{Graph}]", "."));
    }
}
