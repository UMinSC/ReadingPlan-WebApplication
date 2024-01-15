package ca.sheridancollege.liu198.Beans;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class MonthlyCalendar {
	private LocalDate setDate;
	private List<LocalDate> calendarSundays;

	public MonthlyCalendar() {
		this.setDate = LocalDate.now();
		this.calendarSundays = CalendarSundays(setDate);
	}

	public MonthlyCalendar(LocalDate setDate) {
		this.setDate = setDate;
		this.calendarSundays = CalendarSundays(setDate);
	}

	private List<LocalDate> CalendarSundays(LocalDate setDate) {
		List<LocalDate> calendarSundays = new ArrayList<>();

		LocalDate startDate = setDate.withDayOfMonth(1); // firstDate of the month
		int dayOfWeek = startDate.getDayOfWeek().getValue();
		int dayBeforeMonth = (dayOfWeek == 7) ? 0 : dayOfWeek;
		LocalDate firstDate = startDate.minusDays(dayOfWeek); // firstDate of the Calendar

		int daysInMonth = setDate.lengthOfMonth();
		LocalDate sunday = firstDate;
		for (int i = 0; i <= dayBeforeMonth + daysInMonth; i++) {
			if (i % 7 == 0) {
				sunday = firstDate.plusDays(i);
				calendarSundays.add(sunday);
				
		}}

		return calendarSundays;
	}

}
