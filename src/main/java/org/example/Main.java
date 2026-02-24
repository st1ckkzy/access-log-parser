package org.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

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

                Set<String> existingPages = statistics.getExistingPages();
                System.out.println("\n1. Существующие страницы (всего " + existingPages.size() + "):");
                int pageCounter = 1;
                for (String page : existingPages) {
                    System.out.printf("%3d. %s%n", pageCounter++, page);
                    if (pageCounter > 5 && existingPages.size() > 5) {
                        System.out.printf("... и еще %d страниц%n", existingPages.size() - 5);
                        break;
                    }
                }

                Set<String> nonExistingPages = statistics.getNonExistingPages();
                System.out.println("\n2. Несуществующие страницы (код 404, всего " + nonExistingPages.size() + "):");
                pageCounter = 1;
                for (String page : nonExistingPages) {
                    System.out.printf("%3d. %s%n", pageCounter++, page);
                    if (pageCounter > 5 && nonExistingPages.size() > 5) {
                        System.out.printf("... и еще %d страниц%n", nonExistingPages.size() - 5);
                        break;
                    }
                }

                Map<String, Double> osStats = statistics.getOsStatistics();
                System.out.println("\n3. Статистика операционных систем (доли от 0 до 1):");
                if (!osStats.isEmpty()) {
                    System.out.println("ОС                Доля      Процент   Запросов");

                    for (Map.Entry<String, Double> entry : osStats.entrySet()) {
                        String os = entry.getKey();
                        double proportion = entry.getValue();
                        int count = statistics.getOsRawStatistics().get(os);

                        System.out.printf("%-16s %.3f     %.1f%%    %d%n", os, proportion, proportion * 100, count);
                    }

                    double sum = osStats.values().stream().mapToDouble(Double::doubleValue).sum();
                    System.out.printf("Сумма долей операционных систем: %.4f (должно быть 1.0000)%n", sum);
                }

                Map<String, Double> browserStats = statistics.getBrowserStatistics();
                System.out.println("\n4. Статистика браузеров (доли от 0 до 1):");
                if (!browserStats.isEmpty()) {
                    System.out.println("Браузер           Доля      Процент   Запросов");

                    for (Map.Entry<String, Double> entry : browserStats.entrySet()) {
                        String browser = entry.getKey();
                        double proportion = entry.getValue();
                        int count = statistics.getBrowserRawStatistics().get(browser);

                        System.out.printf("%-16s %.3f     %.1f%%    %d%n", browser, proportion, proportion * 100, count);
                    }

                    double sum = browserStats.values().stream().mapToDouble(Double::doubleValue).sum();
                    System.out.printf("Сумма долей браузеров: %.3f (должно быть 1.000)%n", sum);
                }

            } catch (Exception ex) {
                System.err.println("Непредвиденная ошибка: " + ex.getMessage());
                ex.printStackTrace();
                System.exit(1);
            }
        }
    }
}



