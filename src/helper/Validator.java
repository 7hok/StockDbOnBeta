package helper;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {
    private static Scanner scanner = new Scanner(System.in);
    public static int readInt(String message){
        int value = 0;
        boolean validValue = false;
        while (!validValue){
            try {
                System.out.print(message);
                value = scanner.nextInt();
                validValue = true;
            }catch (InputMismatchException e){
                System.out.println("Error input!");
                scanner.nextLine();
            }
        }
        return value;
    }

    public static char readYesNo(String message){
        char value = ' ';
        boolean validValue = false;
        while (!validValue){
                System.out.print(message);
                value = scanner.next().toLowerCase().charAt(0);
                if (value == 'y' || value == 'n')
                    validValue = true;
        }
        return value;
    }

    public static int readInt(String message, int min, int max){
        int value = readInt(message);
        while (value < min || value > max){
            System.out.println("Value must from " + min + " to " + max);
            value = readInt(message);
        }
        return value;
    }

    public static String readAlphabets(){
        String value = scanner.nextLine();
        while(!value.matches("[a-zA-Z]+")){
            System.out.println("Please enter a valid value!");
            value = scanner.nextLine();
        }
        return value;
    }

    public static float readFloat(String message){
        float value = 0;
        boolean validValue = false;
        while (!validValue){
            try {
                System.out.print(message);
                value = scanner.nextFloat();
                validValue = true;
            }catch (InputMismatchException e){
                System.out.println("Error input!");
                scanner.nextLine();
            }
        }
        return value;
    }

    public static double readDouble(String message){
        double value = 0;
        boolean validValue = false;
        while (!validValue){
            try {
                System.out.print(message);
                value = scanner.nextDouble();
                validValue = true;
            }catch (InputMismatchException e){
                System.out.println("Error input!");
                scanner.nextLine();
            }
        }
        return value;
    }

    public static int getNumberFromShortcut(String str) {
        if(isValidStringNumber(str.substring(2, str.length()))){

        }

        int value = 0;
        try{

        }catch (Exception e){
            e.printStackTrace();
        }
        return value;
    }

    private static boolean isValidStringNumber(String substring) {
        String regex = "[0-9]+";
        return substring.matches(regex);
    }

    static Boolean stringHasChar(String regex, String text) {
        return Pattern.matches(".*" + regex + ".*", text);
    }// "\'"

    public static String readStringWithCondition(String message, String regex){
        String value = null;
        boolean validValue = false;
        while (!validValue){
            System.out.print(message);
            value = scanner.nextLine();
            validValue = !stringHasChar(regex, value);
        }
        return value;
    }
}
