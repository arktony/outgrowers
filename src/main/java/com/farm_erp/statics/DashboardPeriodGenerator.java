package com.farm_erp.statics;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Locale;

import javax.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class DashboardPeriodGenerator {

	public DatePeriod setPeriodDates(Dashboard_Card_Enum periodEnum) {

		DatePeriod period = new DatePeriod();

		LocalDate date = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1);

		switch (periodEnum) {
			case LAST_1_MONTH:
				period.end = date.with(TemporalAdjusters.firstDayOfMonth());
				period.start = period.end.minusMonths(1).with(TemporalAdjusters.firstDayOfMonth());
				period.labelRange = "WEEKS";

				break;
			case LAST_1_YEAR:
				period.end = date.with(TemporalAdjusters.lastDayOfMonth());
				period.start = period.end.minusMonths(11).with(TemporalAdjusters.firstDayOfMonth());
				period.labelRange = "MONTHS";

				break;
			case LAST_7_DAYS:
				period.end = LocalDate.now().plusDays(1);
				period.start = period.end.minusDays(6);
				period.labelRange = "DAYS";

				break;
			case START_OF_BUSINESS:
				period.end = date.with(TemporalAdjusters.lastDayOfMonth());
				period.start = LocalDate.EPOCH;

				period.labelRange = "MONTHS";
				Long periodLength = ChronoUnit.DAYS.between(period.start, period.end);

				if (periodLength < 7) {
					period.labelRange = "DAYS";

				} else if (periodLength > 7
						&& periodLength < 28) {
					period.labelRange = "WEEKS";

				}
				if (periodLength > 366) {
					period.labelRange = "YEARS";
				}

				break;

		}

		return period;

	}

	public DatePeriod setPeriodDates(Dashboard_Graph_Enum periodEnum, LocalDate startDate, LocalDate endDate) {

		DatePeriod period = new DatePeriod();

		LocalDate date = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1);

		switch (periodEnum) {
			case THIS_WEEK:
				Calendar calendardate = Calendar.getInstance();
				calendardate.set(Calendar.DAY_OF_WEEK, calendardate.getFirstDayOfWeek());

				period.start = TimeConverter.EpochMils_to_LocalDate(calendardate.getTimeInMillis());
				period.end = period.start.plusDays(7);
				period.labelRange = "DAYS";

				break;
			case LAST_7_DAYS:
				period.end = LocalDate.now().plusDays(1);
				period.start = period.end.minusDays(7);
				period.labelRange = "DAYS";

				break;
			case THIS_MONTH:
				period.start = date.with(TemporalAdjusters.firstDayOfMonth());
				period.end = period.start.with(TemporalAdjusters.lastDayOfMonth());
				period.labelRange = "WEEKS";

				break;
			case PREVIOUS_MONTH:
				period.end = date.with(TemporalAdjusters.firstDayOfMonth());
				period.start = period.end.minusMonths(1).with(TemporalAdjusters.firstDayOfMonth());
				period.labelRange = "WEEKS";

				break;
			case PREVIOUS_QUARTER: {
				Integer currquarter = LocalDate.now().get(IsoFields.QUARTER_OF_YEAR);
				Integer prevquarter = currquarter - 1;

				if (prevquarter == 1) {

					period.start = LocalDate.of(LocalDate.now().getYear(), Month.JANUARY, 1);
					period.end = period.start.plusMonths(3);

				} else if (prevquarter == 2) {

					period.start = LocalDate.of(LocalDate.now().getYear(), Month.APRIL, 1);
					period.end = period.start.plusMonths(3);

				} else if (prevquarter == 3) {

					period.start = LocalDate.of(LocalDate.now().getYear(), Month.JULY, 1);
					period.end = period.start.plusMonths(3);

				} else {

					period.start = LocalDate.of(LocalDate.now().getYear(), Month.OCTOBER, 1);
					period.end = period.start.plusMonths(3);

				}

				period.labelRange = "MONTHS";

				break;
			}
			case BUSINESS_QUARTER: {
				LocalDate business_start_qtr = null;

				business_start_qtr = LocalDate.of(LocalDate.now().getYear(), Month.JANUARY, 1);

				LocalDate fq_end = business_start_qtr.plusMonths(3);
				boolean found = false;

				while (!found) {
					if (business_start_qtr.isBefore(LocalDate.now())
							&& fq_end.isAfter(LocalDate.now())) {
						break;
					}
					business_start_qtr = business_start_qtr.plusMonths(3);
					fq_end = business_start_qtr.plusMonths(3);

				}

				// subtract 3 to get quarter before current quarter
				period.start = business_start_qtr.minusMonths(3).with(TemporalAdjusters.firstDayOfMonth());
				period.end = period.start.plusMonths(3);
				period.labelRange = "MONTHS";

				break;
			}
			case PREVIOUS_12_MONTHS:
				period.end = date.with(TemporalAdjusters.lastDayOfMonth());
				period.start = period.end.minusMonths(11).with(TemporalAdjusters.firstDayOfMonth());
				period.labelRange = "MONTHS";

				break;
			case FISCAL_YEAR:
				period.start = LocalDate.of(LocalDate.now().getYear(), Month.JANUARY, 1)
						.with(TemporalAdjusters.firstDayOfMonth());
				period.end = period.start.plusMonths(12);

				period.labelRange = "MONTHS";

				break;

			case CUSTOM:
				period.end = endDate.plusDays(1);
				period.start = startDate;

				period.labelRange = "MONTHS";
				Long periodLength = ChronoUnit.DAYS.between(period.start, period.end);

				endDate.lengthOfMonth();

				if (periodLength < 7) {
					period.labelRange = "DAYS";

				} else if (periodLength > 7
						&& periodLength <= endDate.lengthOfMonth()) {
					period.labelRange = "WEEKS";

				}
				if (periodLength > 366) {
					period.labelRange = "YEARS";
				}

				break;
			case QUARTER:
				break;

		}

		return period;
	}

	public String labelGenerator(String periodLabel, LocalDate date) {
		String label = null;

		switch (periodLabel) {
			case "DAYS":
				label = date.getDayOfMonth() + " " + date.getMonth().getDisplayName(TextStyle.SHORT, Locale.US);

				break;

			case "MONTHS":
				label = date.getMonth().getDisplayName(TextStyle.SHORT, Locale.US);

				break;

			case "WEEKS":
				label = date.getMonth().getDisplayName(TextStyle.SHORT,
						Locale.US) + " " + date.getDayOfMonth() + "-" + date.plusWeeks(1).minusDays(1).getDayOfMonth();

				break;

			case "YEARS":
				label = "" + date.getYear();

				break;

		}

		return label;

	}

}
