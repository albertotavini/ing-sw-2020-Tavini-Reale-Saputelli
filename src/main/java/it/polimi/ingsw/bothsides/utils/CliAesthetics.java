package it.polimi.ingsw.bothsides.utils;

import java.util.ArrayList;
import java.util.Random;

public class CliAesthetics {


    private final static int choice = new Random().nextInt(3);


    public static void printBox(String[] strings) {


        switch (choice){

            case 0:
                printBox1(strings);
                break;

            case 1:
                printBox2(strings);
                break;

            case 2:
                printBox3(strings);
                break;

            case 3:
                printBox4(strings);
                break;

            default:
                printBox1(strings);
                break;


        }

    }


    private static void printBox1(String[] strings) {
        int maxBoxWidth = getMaxLength(strings);
        String line = "+" + fill('+', maxBoxWidth + 2) + "+";
        System.out.println(line);
        for (String str : strings) {
            System.out.printf("| %s |%n", padString(str, maxBoxWidth));
        }
        System.out.println(line);
    }


    private static void printBox2(String[] strings) {
        int maxBoxWidth = getMaxLength(strings);
        String line = "O" + fill('-', maxBoxWidth + 2) + "O";
        System.out.println(line);
        for (String str : strings) {
            System.out.printf("| %s |%n", padString(str, maxBoxWidth));
        }
        System.out.println(line);
    }


    private static void printBox3(String[] strings) {
        int maxBoxWidth = getMaxLength(strings);
        String line = "S" + fill('=', maxBoxWidth + 2) + "S";
        System.out.println(line);
        for (String str : strings) {
            System.out.printf("| %s |%n", padString(str, maxBoxWidth));
        }
        System.out.println(line);
    }


    private static void printBox4(String[] strings) {
        int maxBoxWidth = getMaxLength(strings);
        String line = "x" + fill('=', maxBoxWidth + 2) + "x";
        System.out.println(line);
        for (String str : strings) {
            System.out.printf("| %s |%n", padString(str, maxBoxWidth));
        }
        System.out.println(line);
    }



    private static int getMaxLength(String[] strings) {
        int len = Integer.MIN_VALUE;
        for (String str : strings) {
            len = Math.max(str.length(), len);
        }
        return len;
    }
    private static String padString(String str, int len) {
        StringBuilder sb = new StringBuilder(str);
        return sb.append(fill(' ', len - str.length())).toString();
    }
    private static String fill(char ch, int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(ch);
        }
        return sb.toString();
    }
    private static String[] splitString(String splittable) {

        ArrayList<String> arrayList = new ArrayList<>();

        for (String s : splittable.split("\\r\\n|\\n|\\r")){

            arrayList.add(s);

        }

        String[] result = new String[arrayList.size()];

        arrayList.toArray(result);

        return result;



    }


}
