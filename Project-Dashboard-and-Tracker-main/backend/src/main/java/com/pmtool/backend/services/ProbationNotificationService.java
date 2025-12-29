package com.pmtool.backend.services;

import java.time.LocalDate;

public interface ProbationNotificationService {
	public void processProbationReminders(LocalDate targetDate);
}
