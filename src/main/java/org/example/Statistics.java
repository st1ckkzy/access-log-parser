package org.example;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Statistics {
    private int totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;
    private int entryCount;
    private Set<String> existingPages;
    private Map<String, Integer> osCounts;

    public Statistics() {
        this.totalTraffic = 0;
        this.minTime = null;
        this.maxTime = null;
        this.entryCount = 0;
        this.existingPages = new HashSet<>();
        this.osCounts = new HashMap<>();
    }

    public void addEntry(LogEntry entry) {
        totalTraffic += entry.getResponseSize();
        entryCount++;

        LocalDateTime entryTime = entry.getTime();

        if (minTime == null || entryTime.isBefore(minTime)) {
            minTime = entryTime;
        }

        if (maxTime == null || entryTime.isAfter(maxTime)) {
            maxTime = entryTime;
        }

        if (entry.getResponseCode() == 200) {
            existingPages.add(entry.getPath());
        }

        String os = entry.getUserAgent().getOsType();
        osCounts.put(os, osCounts.getOrDefault(os, 0) + 1);
    }

    public Set<String> getExistingPages() {
        return new HashSet<>(existingPages);
    }

    public Map<String, Double> getOsStatistics() {
        Map<String, Double> result = new HashMap<>();

        if (osCounts.isEmpty()) {
            return result;
        }

        int totalOsEntries = 0;
        for (int count : osCounts.values()) {
            totalOsEntries += count;
        }

        for (Map.Entry<String, Integer> entry : osCounts.entrySet()) {
            double proportion = (double) entry.getValue() / totalOsEntries;
            result.put(entry.getKey(), proportion);
        }

        return result;
    }


    public Map<String, Integer> getOsRawStatistics() {
        return new HashMap<>(osCounts);
    }

    public double getTrafficRate() {
        if (minTime == null || maxTime == null || entryCount == 0) {
            return 0.0;
        }

        long hoursBetween = ChronoUnit.HOURS.between(minTime, maxTime);

        if (hoursBetween < 1) {
            hoursBetween = 1;
        }
        long unsignedTotal = totalTraffic & 0xFFFFFFFFL;
        return (double) unsignedTotal / hoursBetween;
    }

    public int getTotalTraffic() {
        return totalTraffic;
    }

    public LocalDateTime getMinTime() {
        return minTime;
    }

    public LocalDateTime getMaxTime() {
        return maxTime;
    }

    public int getEntryCount() {
        return entryCount;
    }

    public int getExistingPagesCount() {
        return existingPages.size();
    }


    public boolean pageExists(String path) {
        return existingPages.contains(path);
    }

    public void printStatistics() {
        System.out.println("Всего записей: " + entryCount);
        long unsignedTotal = totalTraffic & 0xFFFFFFFFL;

        if (unsignedTotal != totalTraffic) {
            System.out.println("Произошло переполнение int. Отображаемое значение: " + totalTraffic + " байт"); //По условиям задачи totalTraffic обязательно должен быть int. Для корректного подсчета используем long unsignedTotal
            System.out.println("Корректное значение трафика: " + unsignedTotal + " байт");
        } else {
            System.out.println("Общий трафик: " + totalTraffic + " байт");
        }

        if (minTime != null && maxTime != null) {
            System.out.println("Период: с " + minTime + " по " + maxTime);
            long hours = ChronoUnit.HOURS.between(minTime, maxTime);
            System.out.println("Продолжительность: " + hours + " часов");
            System.out.println("Средний трафик в час: " + String.format("%.2f", getTrafficRate()) + " байт/час");
            System.out.println("Количество страниц с кодом 200: " + getExistingPagesCount());

            Map<String, Double> osStats = getOsStatistics();
            if (!osStats.isEmpty()) {
                System.out.println("Доли использования ОС:");
                for (Map.Entry<String, Double> entry : osStats.entrySet()) {
                    System.out.printf("  %-10s: %.2f%% (%d запросов)%n", entry.getKey(), entry.getValue() * 100, osCounts.get(entry.getKey()));
                }
            }
            System.out.println("=".repeat(50));
        }
    }
}