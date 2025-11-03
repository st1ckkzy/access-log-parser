package org.example;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введите первое число:");
        int x = scanner.nextInt();

        System.out.println("Введите второе число:");
        int y = scanner.nextInt();

        int add = x + y;
        int subtr = x - y;
        int multipl = x * y;
        double div = (double) x / y;

        System.out.println(x + " + " + y + " = " + add);
        System.out.println(x + " - " + y + " = " + subtr);
        System.out.println(x + " * " + y + " = " + multipl);
        System.out.println(x + " / " + y + " = " + div);
    }
}