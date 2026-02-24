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

            if (!file.exists()) {
                System.out.println("Указанный путь или файл не существует");
                System.out.println();
                continue;
            }
            if (file.isDirectory()) {
                System.out.println("Указанный путь ведет к папке, а не к файлу");
                System.out.println();
                continue;
            }

            fileExistsCounter++;
            System.out.println("Путь указан верно. Выполняется чтение файла " + file.getName() + "...");

            Statistics statistics = new Statistics();

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                int totalLines = 0;

                while ((line = reader.readLine()) != null) {
                    totalLines++;

                    if (line.length() > 1024) {
                        throw new LineTooLongException("Строка #" + (totalLines) + " превышает 1024 символа. Длина: " + line.length());
                    }

                    if (!line.trim().isEmpty()) {
                        LogEntry entry = new LogEntry(line);
                        statistics.addEntry(entry);
                    }
                }

                statistics.printStatistics();

            } catch (Exception ex) {
                System.err.println("Непредвиденная ошибка: " + ex.getMessage());
                ex.printStackTrace();
                System.exit(1);
            }
        }
    }
}



