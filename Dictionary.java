package com.lzw;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Dictionary implements Runnable{
    private final String text;
    private final String outputFilename;

    public Dictionary(String text, String outputFilename) {
        this.text = text;
        this.outputFilename = outputFilename;
    }

    private HashMap<String, Integer> createASCIItable() {
        HashMap<String, Integer> table = new HashMap<>();
        for (int i = 0; i <= 255; i++) {
            String ch = "";
            ch += (char)i;
            table.put(ch, i);
        }
        return table;
    }

    @Override
    public void run() {
        String encodeText = text;

        HashMap<String, Integer> table = createASCIItable();

        StringBuilder sb = new StringBuilder();
        for (char c: encodeText.toCharArray()) {
            if (table.get(String.valueOf(c)) == null) {
                if(c == 8212) {
                    sb.append('-');
                } else {
                    sb.append('"');
                }
            } else {
                sb.append(c);
            }
        }
        String parseText = sb.toString();

        String p = "";
        StringBuilder c = new StringBuilder();
        p += parseText.toCharArray()[0];

        Integer code = 256;

        ArrayList<Integer> resultText = new ArrayList<>();
        for (int i = 0; i < parseText.length(); i++) {
            if (i != parseText.length() - 1) {
                c.append(parseText.toCharArray()[i + 1]);
            }
            if (table.get(p + c) != null) {
                p = p + c;
            } else {
                resultText.add(table.get(p));
                table.put(p + c,code);
                p = c.toString();
                code++;
            }
            c = new StringBuilder();
        }
        resultText.add(table.get(p));


        try (FileOutputStream fos = new FileOutputStream(new File(outputFilename), true);
             Writer myWriter = new OutputStreamWriter(fos, "UTF8")) {
            for (Integer i: resultText) {
                myWriter.write(i);
            }
            myWriter.write(0);
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
