package com.farm_erp.statics;

import com.farm_erp.utilities.Quarter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Category_Scheduler {

    public List<Category_Period> schedule(Long start, Long end, _CategoryEnums category) {

        long periods = scheduleTerm(start, end, category);

        LocalDate startDate = TimeConverter.EpochMils_to_LocalDate(start);
        switch (category.toString()) {
            case "ANNUALLY":
                startDate = LocalDate.of(startDate.getYear(), Month.DECEMBER, 1);
                break;
            case "MONTHLY":
                startDate = LocalDate.of(startDate.getYear(), startDate.getMonth(), 1);
                break;
            case "QUARTERLY":
                Quarter q = new Quarter(startDate);
                startDate = q.startDate;
                break;
            case "DAILY":
                startDate = startDate;
                break;
            case "WEEKLY":
                startDate = startDate.minusWeeks(1).with(DayOfWeek.SUNDAY);
                break;
            default:
                startDate = startDate;
                break;
        }

        List<Category_Period> data = new ArrayList<>();

        for (int i = 0; i < periods; i++) {

            DateData endDate = endDate(category, startDate, i);

            data.add(new Category_Period(TimeConverter.LocalDate_to_EpochMilli_DayStart(startDate),
                    TimeConverter.LocalDate_to_EpochMilli_DayEnd(endDate.date), endDate.label));

            startDate = endDate.date.plusDays(1);
        }

        return data;
    }

    private long scheduleTerm(Long start, Long end, _CategoryEnums category) {
        if (start == null)
            start = TimeConverter.LocalDate_to_EpochMilli_DayStart(LocalDate.now().withDayOfMonth(1));

        if (end == null)
            end = TimeConverter
                    .LocalDate_to_EpochMilli_DayEnd(LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()));

        LocalDate startDate = TimeConverter.EpochMils_to_LocalDate(start);
        LocalDate endDate = TimeConverter.EpochMils_to_LocalDate(end);
        long sheduledTerm = 0;

        switch (category.label) {
            case "Daily":
                sheduledTerm = ChronoUnit.DAYS.between(startDate, endDate);
                break;

            case "Weekly":
                sheduledTerm = ChronoUnit.WEEKS.between(startDate, endDate);
                break;

            case "Monthly":
                sheduledTerm = ChronoUnit.MONTHS.between(startDate, endDate);
                break;

            case "Quarterly":
                if (ChronoUnit.MONTHS.between(startDate, endDate) <= 3
                        && ChronoUnit.MONTHS.between(startDate, endDate) > 0) {
                    sheduledTerm = 1;
                    break;
                } else {
                    sheduledTerm = ChronoUnit.MONTHS.between(startDate, endDate) / 3;
                    break;
                }

            case "Annually":
                sheduledTerm = ChronoUnit.YEARS.between(startDate, endDate);
                break;

            default:
                break;
        }

        return sheduledTerm;
    }

    private DateData endDate(_CategoryEnums category, LocalDate repaymentDate, int i) {
        String label = "";
        switch (category.label) {
            case "Daily":
                label = repaymentDate.toString();
                break;
            case "Weekly":
                label = "Week " + i;
                repaymentDate = repaymentDate.with(DayOfWeek.SUNDAY);
                break;
            case "Monthly":
                label = repaymentDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " "
                        + repaymentDate.getYear();
                repaymentDate = LocalDate.of(repaymentDate.getYear(), repaymentDate.getMonth(),
                        repaymentDate.lengthOfMonth());
                break;
            case "Quarterly":
                Quarter qtr = new Quarter(repaymentDate);
                label = qtr.quarter;
                repaymentDate = LocalDate.of(repaymentDate.getYear(), repaymentDate.getMonth(),
                        repaymentDate.lengthOfMonth());
                break;
            case "Annually":
                label = String.valueOf(repaymentDate.getYear());
                repaymentDate = LocalDate.of(repaymentDate.getYear(), repaymentDate.getMonth(),
                        repaymentDate.lengthOfMonth());
                break;
            default:
                label = repaymentDate.toString();
                break;
        }

        return new DateData(repaymentDate, label);
    }

    class DateData {
        public LocalDate date;
        public String label;

        public DateData() {
        }

        public DateData(LocalDate date, String label) {
            this.date = date;
            this.label = label;
        }
    }
}
