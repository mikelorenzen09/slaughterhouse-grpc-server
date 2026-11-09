package dk.via.pro3;

import java.util.Scanner;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        System.out.println("Hello World!");
        System.out.println("Next lesson is something else");
        System.out.println("Welcome to Pro3, Autumn 2026");
        String choice;
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Enter your favourite semester: [Spring or Autumn] ?");
        choice=keyboard.nextLine();
        System.out.println(getReward(choice));

    }
    public static String getReward(String choice)
    {
        if (choice.equalsIgnoreCase("Autumn"))
        {
            return "A ticket for Ski tour";
        }
        else
        {
            return "You got nothing";
        }

    }
}
