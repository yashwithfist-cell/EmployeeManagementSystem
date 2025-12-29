package com.pmtool.backend.scheduler;

import java.time.LocalDate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.pmtool.backend.services.ProbationNotificationService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProbationNotificationScheduler {

	private final ProbationNotificationService notificationService;

	@Scheduled(cron = "0 0 9 * * ?")
	public void sendProbationReminder() {
		LocalDate targetDate = LocalDate.now().plusDays(3);
		notificationService.processProbationReminders(targetDate);

	}
}
