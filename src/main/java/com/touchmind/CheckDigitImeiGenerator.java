package com.touchmind;

import java.util.Scanner;

public class CheckDigitImeiGenerator {

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        Scanner aScanner = new Scanner(System.in);

        System.out.println("IMEI CHECK DIGIT GENERATOR");

        System.out.println("Enter a 14 Digit IMEI number to generate the Check DIGIT");

        String input = aScanner.nextLine();

        if (input.length() > 14 || input.length() < 14) {
            System.out.println("Input Length not 14 ,Please re-run application");
        } else {

            int generatedCheckDigit = getCheckDigit(input);

            System.out.println("Check Digit " + generatedCheckDigit);

            System.out.println("COMPLETE IMEI NUMBER " + input + generatedCheckDigit);
        }
        aScanner.close();
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
