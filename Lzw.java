package com.lzw;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Lzw {
    public Lzw() { }

    public void encodeFile(String inputFilename, String outputFilename) throws FileNotFoundException {
        StringBuilder sb = new StringBuilder();

        File inputFile = new File(inputFilename);
        if(new File(outputFilename).exists()) {
            PrintWriter writter = new PrintWriter(outputFilename);
            writter.print("");
            writter.close();
        }

        Thread []t = new Thread[(int) (inputFile.length()/20000)+1];
        int indexThread = 0;

        try(FileInputStream fis = new FileInputStream(inputFile);
            InputStreamReader isr = new InputStreamReader(fis, "UTF8");
            BufferedReader reader = new BufferedReader(isr)) {
            int c;
            char ch;
            while((c = reader.read()) != -1) {
                ch = (char)c;
                sb.append(ch);
                if (sb.toString().length() == 20000) {
                    t[indexThread] = new Thread(new Dictionary(sb.toString(), outputFilename));
                    t[indexThread].start();
                    indexThread++;
                    sb = new StringBuilder();
                }
            }
            if (sb.toString().length() > 0) {
                t[indexThread] = new Thread(new Dictionary(sb.toString(), outputFilename));
                t[indexThread].start();
            }
        } catch(IOException ex){
            System.out.println(ex.getMessage());
        }
    }

    public void decodeFile(String inputFilename, String outputFilename) {
        HashMap<Integer,String> dictionary = createDictionary();
        ArrayList<Integer> encodedText = new ArrayList<>();

        try(FileInputStream fis = new FileInputStream(inputFilename);
            InputStreamReader isr = new InputStreamReader(fis, "UTF8");
            BufferedReader reader = new BufferedReader(isr)) {
            try (FileOutputStream fos = new FileOutputStream(outputFilename);
                 Writer myWriter = new OutputStreamWriter(fos, "UTF8")) {
                int c;
                while((c = reader.read()) != -1) {
                    if (c == 0) {
                        int code = 256;
                        HashMap<Integer,String> tempTable = dictionary;
                        String s = "" + (char)(int)encodedText.remove(0);
                        StringBuilder result = new StringBuilder(s);

                        for (int k : encodedText) {
                            String tempText;
                            if (tempTable.containsKey(k)) {
                                tempText = tempTable.get(k);
                            } else if (k == code) {
                                tempText = s + s.charAt(0);
                            } else {
                                throw new IOException();
                            }
                            result.append(tempText);
                            tempTable.put(code++, s + tempText.charAt(0));
                            s = tempText;
                        }
                        for (char ch : result.toString().toCharArray()) {
                            myWriter.write(ch);
                        }
                        encodedText.clear();
                    } else {
                        encodedText.add(c);
                    }
                }
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private HashMap<Integer, String> createDictionary() {
        HashMap<Integer,String> dictionary = new HashMap<>();
        for (int i = 0; i <= 255; i++) {
            dictionary.put(i, "" + (char)i);
        }
        return dictionary;
    }
}
