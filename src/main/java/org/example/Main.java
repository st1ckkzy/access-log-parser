package org.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                int totalLines = 0;
                int googleBotCount = 0;
                int yandexBotCount = 0;

                while ((line = reader.readLine()) != null) {
                    totalLines++;

                    if (line.length() > 1024) {
                        throw new LineTooLongException("Строка #" + (totalLines) + " превышает 1024 символа. Длина: " + line.length());
                    }

                    String userAgent = extractUserAgent(line);
                    if (userAgent != null) {
                        String botName = extractBotNameFromUserAgent(userAgent);

                        if (botName != null) {
                            if (botName.equalsIgnoreCase("GoogleBot") ||
                                    botName.equalsIgnoreCase("GoogleBot") ||
                                    botName.equalsIgnoreCase("Google")) {
                                googleBotCount++;
                            } else if (botName.equalsIgnoreCase("YandexBot") ||
                                    botName.equalsIgnoreCase("Yandex")) {
                                yandexBotCount++;
                            }
                        }
                    }
                }
                System.out.println("Количество строк: " + totalLines);
                System.out.println("Запросов от Googlebot: " + googleBotCount + ", их доля составляет " + String.format("%.2f%%", (double) googleBotCount / totalLines * 100));
                System.out.println("Запросов от YandexBot: " + yandexBotCount + ", их доля составляет " + String.format("%.2f%%", (double) yandexBotCount / totalLines * 100));

            } catch (Exception ex) {
                System.err.println("Непредвиденная ошибка: " + ex.getMessage());
                ex.printStackTrace();
                System.exit(1);
            }
        }
    }

    private static String extractUserAgent(String logLine) {

        int lastQuoteIndex = logLine.lastIndexOf('"');
        if (lastQuoteIndex == -1) return null;

        int secondLastQuoteIndex = logLine.lastIndexOf('"', lastQuoteIndex - 1);
        if (secondLastQuoteIndex == -1) return null;

        return logLine.substring(secondLastQuoteIndex + 1, lastQuoteIndex);
    }

    private static String extractBotNameFromUserAgent(String userAgent) {
        try {
            int start = userAgent.indexOf('(');
            int end = userAgent.indexOf(')', start);

            if (start == -1 || end == -1) {
                return null;
            }

            String firstBrackets = userAgent.substring(start + 1, end);
            String[] parts = firstBrackets.split(";");

            if (parts.length >= 2) {
                String fragment = parts[1].trim();

                int slashIndex = fragment.indexOf('/');
                if (slashIndex != -1) {
                    return fragment.substring(0, slashIndex).trim();
                }
                return fragment;
            }
        } catch (Exception e) {

        }
        return null;
    }
}