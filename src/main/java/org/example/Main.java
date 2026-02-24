package org.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

class LineTooLongException extends RuntimeException {
    public LineTooLongException(String message) {
        super(message);
    }
}

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

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                FileReader fileReader = new FileReader(path);
                String line;
                int totalLines = 0;
                int minLength = Integer.MAX_VALUE;
                int maxLength = 0;

                while ((line = reader.readLine()) != null) {
                    int length = line.length();

                    if (length > 1024) {
                        throw new LineTooLongException("Строка #" + (totalLines + 1) + " превышает 1024 символа. Длина: " + length);
                    }

                    totalLines++;
                    if (length < minLength) minLength = length;
                    if (length > maxLength) maxLength = length;
                }

                reader.close();
                fileReader.close();

                System.out.println("Количество строк: " + totalLines);
                System.out.println("Минимальная длина: " + (minLength == Integer.MAX_VALUE ? 0 : minLength));
                System.out.println("Максимальная длина: " + maxLength);

            } catch (Exception ex) {
                ex.printStackTrace();
                System.exit(1);
            }
        }
    }
}


