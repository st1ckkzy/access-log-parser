package org.example;

import java.io.File;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        int fileExistsCounter = 0;

        while (true) {
            System.out.print("Введите путь к файлу: ");

            String path = new Scanner(System.in).nextLine();
            File file = new File(path);
            boolean fileExists = file.exists();
            boolean isDirectory = file.isDirectory();

            if (!fileExists) {
                System.out.println("Указанный путь или файл не существует");
                System.out.println();
                continue;
            }
            if (isDirectory) {
                System.out.println("Указанный путь ведет к папке, а не к файлу");
                System.out.println();
                continue;
            }

            fileExistsCounter++;
            System.out.println("Путь указан верно");
            System.out.println("Это файл номер " + fileExistsCounter);
            System.out.println();
        }
    }
}