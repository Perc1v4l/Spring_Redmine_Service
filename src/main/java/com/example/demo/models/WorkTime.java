package com.example.demo.models;

import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import com.taskadapter.redmineapi.TimeEntryManager;
import com.taskadapter.redmineapi.bean.TimeEntry;
import org.javatuples.Pair;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class WorkTime {
    private String URL;
    private String api;
    private int user_id;
    private RedmineManager mgr;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    public WorkTime()
    {
        this.URL = "http://localhost:8080/";
        this.api = "9af5b8f742ef1fa0427d2d9b764a8d3c870ec11a";
        this.user_id = 1;
        this.mgr = RedmineManagerFactory.createWithApiKey(URL, api);
        this.startDate = new Date();
    }

    public WorkTime(String api, String URL, int user_id, Date startDate, Date endDate)
    {
        this.URL = URL;
        this.api = api;
        this.user_id = user_id;
        this.mgr = RedmineManagerFactory.createWithApiKey(URL, api);
        this.startDate = startDate;
        this.endDate = endDate;
    }
    public WorkTime(String api, String URL, int user_id, int month, int year) throws ParseException {
        this.URL = URL;
        this.api = api;
        this.user_id = user_id;
        this.mgr = RedmineManagerFactory.createWithApiKey(URL, api);
        this.startDate = inputDate(year, month, true);
        this.endDate = inputDate(year, month, false);
    }

    public List<String> LessThan8Hours() {
        TimeEntryManager timeEntryManager = mgr.getTimeEntryManager();

        List<String> lessThan8Hours = new ArrayList<>();

        final Map<String, String> params = new HashMap<>();
        params.put("user_id", Integer.toString(user_id));
        params.put("from", startDate.toString());
        params.put("to", endDate.toString());
        List<TimeEntry> elements;
        try {
            elements = timeEntryManager.getTimeEntries(params).getResults();
        } catch (RedmineException e) {
            throw new RuntimeException(e);
        }
        elements = elements.stream()
            .sorted(Comparator.comparing(TimeEntry::getSpentOn))
            .toList();

        Map<Date, Float> dateHoursMap = new HashMap<>();

        for (TimeEntry element : elements) {
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



    public List<String> LessThan40InWeek() {
        TimeEntryManager timeEntryManager = mgr.getTimeEntryManager();

        List<String> lessThan40InWeek = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();

        final Map<String, String> params = new HashMap<>();

        String[] ymd = endDate.toString().split("-");

        calendar.set(Integer.parseInt(ymd[0]),
            Integer.parseInt(ymd[1]),
            Integer.parseInt(ymd[2]));

        int countOfWeeks = calendar.get(Calendar.WEEK_OF_MONTH);

        params.put("user_id", Integer.toString(user_id));
        params.put("from", startDate.toString());
        params.put("to", endDate.toString());

        List<TimeEntry> elements;
        try {
            elements = timeEntryManager.getTimeEntries(params).getResults();
        } catch (RedmineException e) {
            throw new RuntimeException(e);
        }
        elements = elements.stream()
            .sorted(Comparator.comparing(TimeEntry::getSpentOn))
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

        for (TimeEntry element : elements) {
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

    public List<String> LessThenInCalendar() {
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

        List<String> lessThenInCalendar = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();

        final Map<String, String> params = new HashMap<>();

        String[] ymd = endDate.toString().split("-");

        calendar.set(Integer.parseInt(ymd[0]),
            Integer.parseInt(ymd[1]),
            Integer.parseInt(ymd[2]));

        int countOfWeeks = calendar.get(Calendar.WEEK_OF_MONTH);

        params.put("user_id", Integer.toString(user_id));
        params.put("from", startDate.toString());
        params.put("to", endDate.toString());

        List<TimeEntry> elements;
        try {
            elements = timeEntryManager.getTimeEntries(params).getResults();
        } catch (RedmineException e) {
            throw new RuntimeException(e);
        }
        elements = elements.stream()
            .sorted(Comparator.comparing(TimeEntry::getSpentOn))
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

        for (TimeEntry element : elements) {
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
