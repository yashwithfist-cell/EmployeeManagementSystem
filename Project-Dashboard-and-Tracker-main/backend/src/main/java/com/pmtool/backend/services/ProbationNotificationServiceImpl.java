package com.pmtool.backend.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.pmtool.backend.entity.Employee;
import com.pmtool.backend.entity.Notification;
import com.pmtool.backend.exception.ResourceNotFoundException;
import com.pmtool.backend.repository.EmployeeRepository;
import com.pmtool.backend.repository.NotificationRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProbationNotificationServiceImpl implements ProbationNotificationService {

	private final EmployeeRepository employeeRepository;

	private final NotificationRepository notificationRepo;

	@Override
	@Transactional
	public void processProbationReminders(LocalDate targetDate) {
		List<Employee> employees = employeeRepository.findByProfPeriodEndDate(targetDate).orElseThrow(
				() -> new ResourceNotFoundException("Employee not found for the Probation end date :" + targetDate));
		List<Notification> notificationList = employees.stream().map(emp -> convertToNotification(emp)).toList();
		notificationRepo.saveAll(notificationList);
	}

	private Notification convertToNotification(Employee employee) {
		Notification notification=new Notification();
		notification.setEmployee(employee);
		notification.setCreatedAt(LocalDateTime.now());
		notification.setTitle("Probation End Date");
		notification.setMessage("Probation of " + employee.getName() + " ends on " + employee.getProfPeriodEndDate());
		notification.setProbNotif(true);
		notification.setUsername(employee.getMgrName());
		return notification;
	}
}
