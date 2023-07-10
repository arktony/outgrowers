package com.farm_erp.statics;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

public class TimeConverter {

	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US);

	public static ZoneId zone = ZoneId.of("Africa/Kampala");// ZoneId.systemDefault()

	public static Long EpochMillis_Now() {
		return Instant.now().getEpochSecond() * 1000;
	}

	public static String Timezone_of_System() {

		return Calendar.getInstance().getTimeZone().getID();
	}

	public static String LocalDateTime_to_String(LocalDateTime date) {

		if (date != null) {

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm a");
			String text = date.format(formatter);

			return text;

		} else {
			return null;
		}

	}

	public static String LocalDate_to_String(LocalDate date) {

		if (date != null) {

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String text = date.format(formatter);

			return text;

		} else {
			return null;
		}

	}

	public static LocalDateTime EpochMils_to_LocalDateTime(Long epocMils) {

		LocalDateTime Date1 = (Instant.ofEpochMilli(epocMils).atZone(zone)).toLocalDateTime();

		return Date1;
	}

	public static String EpochMils_to_LocalDateTimeFormatted(Long epocMils) {

		LocalDateTime Date1 = (Instant.ofEpochMilli(epocMils).atZone(zone)).toLocalDateTime();

		return formatter.format(Date1);
	}

	public static LocalDate EpochMils_to_LocalDate(Long epocMils) {

		LocalDate Date1 = (Instant.ofEpochMilli(epocMils).atZone(zone)).toLocalDate();

		return Date1;
	}

	public static Long LocalDate_to_EpochMilli(LocalDate date) {

		return date.atTime(LocalTime.now()).atZone(zone).toInstant().toEpochMilli();
	}

	public static Long LocalDate_to_EpochMilli_DayStart(LocalDate date) {

		return date.atTime(LocalTime.MIN).atZone(zone).toInstant().toEpochMilli();
	}

	public static Long LocalDate_to_EpochMilli_DayEnd(LocalDate date) {

		return date.atTime(LocalTime.MAX).atZone(zone).toInstant().toEpochMilli();
	}

	public static Long LocalDateTime_to_EpochMilli(LocalDateTime date) {

		return date.atZone(zone).toInstant().toEpochMilli();
	}

	public static Long LocalTime_to_Milli(LocalTime time) {

		return (time.getHour() * 60 * 60000L) + (time.getMinute() * 60000L) + (time.getSecond() * 1000L);
	}

	public static Long Millis_To_Days(Long millis) {

		return ((millis) / (24 * 3600000));
	}

	public static Long Millis_To_Hours(Long millis) {

		return ((millis) / (3600000));
	}

	public static String getWeekOfMonth(LocalDate start) {

		Calendar cal = Calendar.getInstance();
		cal.set(start.getYear(), start.getMonthValue(), start.getDayOfMonth());
		cal.setMinimalDaysInFirstWeek(1);
		int week = cal.get(Calendar.WEEK_OF_MONTH);

		return "Week " + week;
	}

	public static String EpochMils_to_LocalDateTimeString(Long epocMils) {

		LocalDateTime Date1 = (Instant.ofEpochMilli(epocMils).atZone(zone)).toLocalDateTime();

		return formatter.format(Date1);
	}

	public static String EpochMils_to_LocalTimeString(Long epocMils) {

		LocalTime Date1 = (Instant.ofEpochMilli(epocMils).atZone(zone)).toLocalTime();

		return Date1.toString();
	}

	public static LocalDate String_to_LocalDate(String datetime) {

		if (datetime != null) {
			if (datetime.equals("")) {
				return null;

			} else {
				SimpleDateFormat dMy = new SimpleDateFormat("dd/MM/yyyy");
				SimpleDateFormat mdy = new SimpleDateFormat("MM/dd/yyyy");
				SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat dmy = new SimpleDateFormat("dd/M/yyyy");
				try {
					dmy.parse(datetime);
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/M/yyyy");
					LocalDate dateTime = LocalDate.parse(datetime, formatter);
					return dateTime;

				} catch (ParseException e) {

				}

				try {
					dMy.parse(datetime);
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
					LocalDate dateTime = LocalDate.parse(datetime, formatter);
					return dateTime;

				} catch (ParseException e) {

				}

				try {
					mdy.parse(datetime);
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
					LocalDate dateTime = LocalDate.parse(datetime, formatter);
					return dateTime;

				} catch (ParseException e) {

				}

				try {
					ymd.parse(datetime);
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

					LocalDate dateTime = LocalDate.parse(datetime, formatter);

					return dateTime;

				} catch (ParseException e) {

				}

				return LocalDate.ofEpochDay(0);

			}

		} else {
			return null;
		}

	}

}
