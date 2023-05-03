package com.example.demo.models;

import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.TimeEntryManager;
import org.javatuples.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TimeEntry {
    private String user_id;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    public TimeEntry()
    {
        this.user_id = "";
        this.startDate = new Date();
    }

    public TimeEntry(String user_id, Date startDate, Date endDate)
    {
        this.user_id = user_id;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    public TimeEntry(String user_id, String month, String year) throws ParseException {
        this.user_id = user_id;
        this.startDate = inputDate(Integer.parseInt(year), Integer.parseInt(month), true);
        this.endDate = inputDate(Integer.parseInt(year), Integer.parseInt(month), false);
    }

    public Date getStartDate(){ return this.startDate; }
    public Date getEndDate() { return this.endDate; }
    public String getUser_id() { return this.user_id; }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public List<String> LessThan8Hours(@NotNull RedmineManager mgr) {
        TimeEntryManager timeEntryManager = mgr.getTimeEntryManager();

        List<String> lessThan8Hours = new ArrayList<>();

        final Map<String, String> params = new HashMap<>();
        params.put("user_id", (user_id));
        params.put("from", startDate.toString());
        params.put("to", endDate.toString());
        List<com.taskadapter.redmineapi.bean.TimeEntry> elements;
        try {
            elements = timeEntryManager.getTimeEntries(params).getResults();
        } catch (RedmineException e) {
            throw new RuntimeException(e);
        }
        elements = elements.stream()
            .sorted(Comparator.comparing(com.taskadapter.redmineapi.bean.TimeEntry::getSpentOn))
            .toList();

        Map<Date, Float> dateHoursMap = new HashMap<>();

        for (com.taskadapter.redmineapi.bean.TimeEntry element : elements) {
            if (dateHoursMap.containsKey(element.getSpentOn())) {
                float sumWorkTime = element.getHours() +
                    dateHoursMap.get(element.getSpentOn());
                dateHoursMap.put(element.getSpentOn(), sumWorkTime);
            } else {
                dateHoursMap.put(element.getSpentOn(), element.getHours());
            }
        }

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy ");

        Date date = startDate;
        endDate = addDay(endDate);
        while (!date.equals(endDate)) {
            if (dateHoursMap.containsKey(date)) {
                if (dateHoursMap.get(date) < 8) {
                    lessThan8Hours.add("Дата: " + dateFormat.format(date)
                        + "\tКол-во часов: " + dateHoursMap.get(date));
                }
            } else {
                lessThan8Hours.add("Дата: " + dateFormat.format(date) + "\tКол-во часов: " + 0);
            }
            date = addDay(date);
        }
        return lessThan8Hours;
    }



    public List<String> LessThan40InWeek(@NotNull RedmineManager mgr) {
        TimeEntryManager timeEntryManager = mgr.getTimeEntryManager();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        startDate = java.sql.Date.valueOf(LocalDate.parse(dateFormat.format(startDate), formatter));
        endDate = java.sql.Date.valueOf(LocalDate.parse(dateFormat.format(endDate), formatter));

        List<String> lessThan40InWeek = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();

        final Map<String, String> params = new HashMap<>();

        String[] ymd = endDate.toString().split("-");

        calendar.set(Integer.parseInt(ymd[0]),
            Integer.parseInt(ymd[1]),
            Integer.parseInt(ymd[2]));

        int countOfWeeks = calendar.get(Calendar.WEEK_OF_MONTH);

        params.put("user_id", (user_id));
        params.put("from", startDate.toString());
        params.put("to", endDate.toString());

        List<com.taskadapter.redmineapi.bean.TimeEntry> elements;
        try {
            elements = timeEntryManager.getTimeEntries(params).getResults();
        } catch (RedmineException e) {
            throw new RuntimeException(e);
        }
        elements = elements.stream()
            .sorted(Comparator.comparing(com.taskadapter.redmineapi.bean.TimeEntry::getSpentOn))
            .toList();

        float sumOfHoursInWeek = 0;

        ymd = startDate.toString().split("-");

        calendar.set(Integer.parseInt(ymd[0]),
            Integer.parseInt(ymd[1]),
            Integer.parseInt(ymd[2]));
        int weekInMonth = calendar.get(Calendar.WEEK_OF_MONTH);

        List<Float> hoursInWeek = new ArrayList<>();
        for (int i = 0; i < countOfWeeks; i++) {
            hoursInWeek.add(i, (float) 0);
        }

        for (com.taskadapter.redmineapi.bean.TimeEntry element : elements) {
            calendar.setTime(element.getSpentOn());

            if (calendar.get(Calendar.WEEK_OF_MONTH) == weekInMonth) {
                sumOfHoursInWeek += element.getHours();
            } else {
                hoursInWeek.set(weekInMonth - 1, sumOfHoursInWeek);
                sumOfHoursInWeek = element.getHours();
                weekInMonth = calendar.get(Calendar.WEEK_OF_MONTH);
            }
        }

        for (int i = 1; i <= hoursInWeek.size(); i++) {
            if (hoursInWeek.get(i - 1) < 40) {
                lessThan40InWeek.add("Неделя " + i + ": " + hoursInWeek.get(i - 1));
            }
        }
        return lessThan40InWeek;
    }

    public List<String> LessThenInCalendar(@NotNull RedmineManager mgr) {
        float[] weeks = {
            0,
            0, 40, 40, 40,
            40, 40, 40, 23, 40,
            31, 40, 40, 40,
            40, 40, 40, 40,
            32, 24, 40, 40, 40,
            40, 32, 40, 40,
            40, 40, 40, 40, 40,
            40, 40, 40, 40,
            40, 40, 40, 40,
            40, 40, 40, 40, 39,
            32, 40, 40, 40,
            40, 40, 40, 40
        };

        TimeEntryManager timeEntryManager = mgr.getTimeEntryManager();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        startDate = java.sql.Date.valueOf(LocalDate.parse(dateFormat.format(startDate), formatter));
        endDate = java.sql.Date.valueOf(LocalDate.parse(dateFormat.format(endDate), formatter));

        List<String> lessThenInCalendar = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();

        final Map<String, String> params = new HashMap<>();

        String[] ymd = endDate.toString().split("-");

        calendar.set(Integer.parseInt(ymd[0]),
            Integer.parseInt(ymd[1]),
            Integer.parseInt(ymd[2]));

        int countOfWeeks = calendar.get(Calendar.WEEK_OF_MONTH);

        params.put("user_id", (user_id));
        params.put("from", startDate.toString());
        params.put("to", endDate.toString());

        List<com.taskadapter.redmineapi.bean.TimeEntry> elements;
        try {
            elements = timeEntryManager.getTimeEntries(params).getResults();
        } catch (RedmineException e) {
            throw new RuntimeException(e);
        }
        elements = elements.stream()
            .sorted(Comparator.comparing(com.taskadapter.redmineapi.bean.TimeEntry::getSpentOn))
            .toList();

        float sumOfHoursInWeek = 0;

        ymd = startDate.toString().split("-");

        calendar.set(Integer.parseInt(ymd[0]),
            Integer.parseInt(ymd[1]),
            Integer.parseInt(ymd[2]));
        int weekInYear = calendar.get(Calendar.WEEK_OF_YEAR);

        List<Pair<Integer, Float>> hoursInWeek = new ArrayList<>();

        for (int i = 0; i < countOfWeeks; i++) {
            hoursInWeek.add(new Pair<>(weekInYear + i, (float) 0));
        }

        for (com.taskadapter.redmineapi.bean.TimeEntry element : elements) {
            calendar.setTime(element.getSpentOn());

            if (calendar.get(Calendar.WEEK_OF_YEAR) == weekInYear) {
                sumOfHoursInWeek += element.getHours();
            } else {
                int index = hoursInWeek.indexOf(new Pair<>(weekInYear, (float) 0));
                hoursInWeek.set(index, new Pair<>(weekInYear, sumOfHoursInWeek));
                sumOfHoursInWeek = element.getHours();
                weekInYear = calendar.get(Calendar.WEEK_OF_YEAR);
            }
        }
        int index = hoursInWeek.indexOf(new Pair<>(weekInYear, (float) 0));
        hoursInWeek.set(index, new Pair<>(weekInYear, sumOfHoursInWeek));

        for (int i = 1; i <= hoursInWeek.size(); i++) {
            if (hoursInWeek.get(i - 1).getValue1() < weeks[hoursInWeek.get(i - 1).getValue0()]) {
                lessThenInCalendar.add("Неделя " + hoursInWeek.get(i - 1).getValue0() + ": " + hoursInWeek.get(i - 1).getValue1());
            }
        }
        return lessThenInCalendar;
    }

    private static Date addDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, 1);
        return cal.getTime();
    }
    private static Date inputDate(int year, int month, boolean startOrEnd) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);

        if(!startOrEnd) {
            calendar.set(year, month, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        }

        Date date = calendar.getTime();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        return  dateFormat.parse(dateFormat.format(date));
    }
}
