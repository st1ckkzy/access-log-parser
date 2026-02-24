package org.example;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Statistics {
    private int totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;
    private int entryCount;

    public Statistics() {
        this.totalTraffic = 0;
        this.minTime = null;
        this.maxTime = null;
        this.entryCount = 0;
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
        }
    }
}