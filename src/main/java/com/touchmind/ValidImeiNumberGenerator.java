package com.touchmind;

import java.util.Random;
import java.util.Scanner;

public class ValidImeiNumberGenerator {

    public static void main(String[] args) {
        // TODO Auto-generated method stubg

        Scanner aScanner = new Scanner(System.in);

        System.out.println("Enter 'g'/'G' for a valid IMEI number to be generated");
        System.out.println("Enter 'c'/'C' for a check number to be generated");
        char selection = aScanner.next().charAt(0);

        switch (selection) {
            case 'c':
            case 'C':

                generateCheckDigit();

                break;
            case 'g':
            case 'G':

                generateValidImeiNumber();

                break;

            default:
                System.out.println("Invalid Input");
                break;
        }

        aScanner.close();
    }

    private static void generateValidImeiNumber() {
        // TODO Auto-generated method stub
        Random myRandom = new Random();
        StringBuilder imeiNumber = new StringBuilder(14);

        imeiNumber.append("864898034");

        System.out.println("VALID IMEI NUMBER GENERATOR");

        for (int counter = 0; counter < 5; counter++) {
            imeiNumber.append(1 + myRandom.nextInt(8));
        }

        int generatedCheckDigit = getCheckDigit(imeiNumber.toString());

        System.out.println();

        System.out.println("VALID IMEI NUMBER " + imeiNumber + generatedCheckDigit);
    }

    private static void generateCheckDigit() {
        // TODO Auto-generated method stub

        System.out.println("IMEI CHECK DIGIT GENERATOR");

        System.out.println("Enter a 14 Digit IMEI number to generate the Check DIGIT");

        String input = new Scanner(System.in).nextLine();

        if (input.length() > 14 || input.length() < 14) {
            System.out.println("Input Length not 14 ,Please re-run application");
        } else {

            int generatedCheckDigit = getCheckDigit(input);

            System.out.println("Check Digit " + generatedCheckDigit);

            System.out.println("COMPLETE IMEI NUMBER " + input + generatedCheckDigit);
        }
    }

    private static int getCheckDigit(String input) {
        // TODO Auto-generated method stub

        int sum = 0;

        for (int counter = 13; counter >= 0; counter--) {
            String digitString = input.substring(counter, counter + 1);

            int digit = Integer.valueOf(digitString);

            if (counter % 2 == 0) {
                sum += digit;
            } else {
                sum += sumUpDigits(digit);
            }
        }

        sum *= 9;

        return sum % 10;
    }

    private static int sumUpDigits(int digit) {
        // TODO Auto-generated method stub
        int sum = 0;
        digit *= 2;
        while (digit > 0) {

            sum += digit % 10;
            digit /= 10;
        }

        return sum;

    }

}
