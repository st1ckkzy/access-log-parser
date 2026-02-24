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
    private Set<String> nonExistingPages;
    private Map<String, Integer> osCounts;
    private Map<String, Integer> browserCounts;
    private int realUserVisits;
    private int errorRequests;
    private Set<String> uniqueRealUserIps;

    public Statistics() {
        this.totalTraffic = 0;
        this.minTime = null;
        this.maxTime = null;
        this.entryCount = 0;
        this.existingPages = new HashSet<>();
        this.nonExistingPages = new HashSet<>();
        this.osCounts = new HashMap<>();
        this.browserCounts = new HashMap<>();
        this.realUserVisits = 0;
        this.errorRequests = 0;
        this.uniqueRealUserIps = new HashSet<>();
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

        if (entry.getResponseCode() == 404) {
            nonExistingPages.add(entry.getPath());
        }

        String os = entry.getUserAgent().getOsType();
        osCounts.put(os, osCounts.getOrDefault(os, 0) + 1);

        String browser = entry.getUserAgent().getBrowser();
        browserCounts.put(browser, browserCounts.getOrDefault(browser, 0) + 1);

        boolean isBot = entry.getUserAgent().isBot();
        int responseCode = entry.getResponseCode();

        if (responseCode >= 400 && responseCode < 600) {
            errorRequests++;
        }

        if (!isBot) {
            realUserVisits++;
            uniqueRealUserIps.add(entry.getIpAddress());
        }
    }

    public double getAverageVisitsPerHour() {
        if (minTime == null || maxTime == null || realUserVisits == 0) {
            return 0.0;
        }

        long hoursBetween = ChronoUnit.HOURS.between(minTime, maxTime);
        if (hoursBetween < 1) {
            hoursBetween = 1;
        }

        return (double) realUserVisits / hoursBetween;
    }

    public double getAverageErrorsPerHour() {
        if (minTime == null || maxTime == null || errorRequests == 0) {
            return 0.0;
        }

        long hoursBetween = ChronoUnit.HOURS.between(minTime, maxTime);
        if (hoursBetween < 1) {
            hoursBetween = 1;
        }

        return (double) errorRequests / hoursBetween;
    }

    public double getAverageVisitsPerUser() {
        if (uniqueRealUserIps.isEmpty() || realUserVisits == 0) {
            return 0.0;
        }

        return (double) realUserVisits / uniqueRealUserIps.size();
    }

    public int getRealUserVisits() {
        return realUserVisits;
    }

    public int getErrorRequests() {
        return errorRequests;
    }

    public int getUniqueRealUsersCount() {
        return uniqueRealUserIps.size();
    }

    public Set<String> getExistingPages() {
        return new HashSet<>(existingPages);
    }

    public Set<String> getNonExistingPages() {
        return new HashSet<>(nonExistingPages);
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

    public Map<String, Double> getBrowserStatistics() {
        Map<String, Double> result = new HashMap<>();

        if (browserCounts.isEmpty()) {
            return result;
        }

        int totalBrowserEntries = 0;
        for (int count : browserCounts.values()) {
            totalBrowserEntries += count;
        }

        for (Map.Entry<String, Integer> entry : browserCounts.entrySet()) {
            double proportion = (double) entry.getValue() / totalBrowserEntries;
            result.put(entry.getKey(), proportion);
        }

        return result;
    }

    public Map<String, Integer> getOsRawStatistics() {
        return new HashMap<>(osCounts);
    }

    public Map<String, Integer> getBrowserRawStatistics() {
        return new HashMap<>(browserCounts);
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

    public int getNonExistingPagesCount() {
        return nonExistingPages.size();
    }

    public boolean pageExists(String path) {
        return existingPages.contains(path);
    }

    public boolean pageIsNonExisting(String path) {
        return nonExistingPages.contains(path);
    }

    public void printResponseCodeStatistics() {
        System.out.println("Существующие страницы (200): " + getExistingPagesCount());
        System.out.println("Несуществующие страницы (404): " + getNonExistingPagesCount());

        System.out.println("Всего уникальных страниц в логах: " + (existingPages.size() + nonExistingPages.size()));
    }

    public void printStatistics() {
        System.out.printf("Всего записей: %,d%n", entryCount);
        System.out.printf("Период: %s - %s%n", minTime != null ? minTime : "N/A", maxTime != null ? maxTime : "N/A");
        long unsignedTotal = totalTraffic & 0xFFFFFFFFL;

        if (unsignedTotal != totalTraffic) {
//            System.out.println("Произошло переполнение int. Отображаемое значение: " + totalTraffic + " байт"); //По условиям задачи totalTraffic обязательно должен быть int. Для корректного подсчета используем long unsignedTotal
            System.out.println("Общий трафик: " + unsignedTotal + " байт");
        } else {
            System.out.println("Общий трафик: " + totalTraffic + " байт");
        }

        if (minTime != null && maxTime != null) {
            long hours = ChronoUnit.HOURS.between(minTime, maxTime);
            System.out.printf("Продолжительность: %d часов%n", hours);
        }

        System.out.println("Посещения реальными пользователями (не боты): " + realUserVisits);
        System.out.println("Уникальных реальных пользователей: " + uniqueRealUserIps.size());
        System.out.println("Запросы с ошибками (4xx, 5xx): " + errorRequests);
        System.out.println("\nСреднее количество посещений в час (реальные пользователи): " + String.format("%.2f", getAverageVisitsPerHour()));
        System.out.println("Среднее количество ошибок в час: " + String.format("%.2f", getAverageErrorsPerHour()));
        System.out.println("Средняя посещаемость одним пользователем: " + String.format("%.2f", getAverageVisitsPerUser()));
    }
}
