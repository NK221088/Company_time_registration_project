package dtu.timemanager.gui.helper;

import javafx.scene.control.ComboBox;

import java.time.LocalDate;

public class TimeHelper {
    public static String getFormattedCurrentDateTime() {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();

        int day = now.getDayOfMonth();
        int month = now.getMonthValue();
        int year = now.getYear();
        int hour = now.getHour();
        int minute = now.getMinute();

        String[] monthNames = {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };

        String monthName = monthNames[month - 1];

        String minuteFormatted = (minute < 10) ? "0" + minute : String.valueOf(minute);

        return day + " " + monthName + " " + year + " at " + hour + ":" + minuteFormatted;
    }

    public static LocalDate convertWeekToDate(String weekString, Integer year) {
        if (weekString == null || weekString.isEmpty() || year == null) {
            return null;
        }
        int week = Integer.parseInt(weekString.replace("Week ", ""));
        return LocalDate.ofYearDay(year, 1)
                .with(java.time.temporal.WeekFields.ISO.weekOfWeekBasedYear(), week)
                .with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
    }

    public static ComboBox<String> createWeekComboBox() {
        ComboBox<String> weekCombo = new ComboBox<>();
        for (int i = 1; i <= 53; i++) {
            weekCombo.getItems().add("Week " + i);
        }
        weekCombo.getItems().add(0, "");
        weekCombo.setPromptText("Select Week");
        return weekCombo;
    }

    public static ComboBox<Integer> createYearComboBox() {
        ComboBox<Integer> yearCombo = new ComboBox<>();
        int currentYear = LocalDate.now().getYear();
        for (int i = currentYear - 5; i <= currentYear + 10; i++) {
            yearCombo.getItems().add(i);
        }
        yearCombo.getItems().add(0, null);
        yearCombo.setPromptText("Select Year");
        return yearCombo;
    }
}
