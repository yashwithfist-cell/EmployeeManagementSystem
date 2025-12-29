package com.pmtool.backend.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.pmtool.backend.DTO.NotificationDTO;
import com.pmtool.backend.entity.Employee;
import com.pmtool.backend.entity.Notification;
import com.pmtool.backend.enums.TrainingStatus;
import com.pmtool.backend.exception.EmployeeNotFoundException;
import com.pmtool.backend.exception.NotificationNotFoundException;
import com.pmtool.backend.exception.NotificationSendingFailedException;
import com.pmtool.backend.exception.ResourceNotFoundException;
import com.pmtool.backend.repository.EmployeeRepository;
import com.pmtool.backend.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

	private final NotificationRepository notificationRepository;

	private final EmployeeRepository empRepository;

	public List<NotificationDTO> getNotifications(Authentication authentication) {
		boolean isHr = authentication.getAuthorities().stream()
				.anyMatch(a -> a.getAuthority().equals("ROLE_HUMAN_RESOURCE"));
		List<NotificationDTO> notificationList = (isHr)
				? notificationRepository.findAllProbationNotifications().stream()
						.map(n -> new NotificationDTO(n.getId(), n.getTitle(), n.getMessage(), n.isReadStatus(),
								n.getCreatedAt(), n.isProbNotif(), n.getPrStatus(), n.getComment(),
								n.getTrainingStatus(), n.getLogComment()))
						.toList()
				: notificationRepository.findByUsernameOrderByCreatedAtDesc(authentication.getName()).stream()
						.map(n -> new NotificationDTO(n.getId(), n.getTitle(), n.getMessage(), n.isReadStatus(),
								n.getCreatedAt(), n.isProbNotif(), n.getPrStatus(), n.getComment(),
								n.getTrainingStatus(), n.getLogComment()))
						.toList();
		return notificationList;
	}

	public void markAsRead(Long id) {
		Notification n = notificationRepository.findById(id)
				.orElseThrow(() -> new NotificationNotFoundException("Notification not found with id : " + id));
		n.setReadStatus(true);
		notificationRepository.save(n);
	}

	public void markAllAsRead(String username) {
		List<Notification> list = notificationRepository.findByUsernameOrderByCreatedAtDesc(username);
		list.forEach(n -> n.setReadStatus(true));
		notificationRepository.saveAll(list);
	}

	@Override
	public void addProbStatusAndComment(Long id, String prStatus, String comment) {
		Notification notification = notificationRepository.findById(id)
				.orElseThrow(() -> new NotificationNotFoundException("Notification not found with id : " + id));
		notification.setPrStatus(prStatus);
		notification.setComment(comment);
		notificationRepository.save(notification);
	}

	@Override
	public Long addSystemLogNotification(String logComment, Authentication authentication) {
		String username = authentication.getName();
		Employee employee = empRepository.findByUsername(username)
				.orElseThrow(() -> new EmployeeNotFoundException("Employee not found with username : " + username));
		Notification notification = new Notification();
		notification.setCreatedAt(LocalDateTime.now());
		notification.setTitle("Meeting Request");
		notification.setMessage(authentication.getName() + " sent a meeting request");
		notification.setEmployee(employee);
		notification.setLogComment(logComment);
		notification.setTrainingStatus(TrainingStatus.PENDING);
		notification.setUsername(employee.getMgrName());
		try {
			notification = notificationRepository.save(notification);
//			notification = notificationRepository.save(Notification.builder().createdAt(LocalDateTime.now())
//					.title("Meeting Request").message(authentication.getName() + " sent a meeting request")
//					.employee(employee).logComment(logComment).username(employee.getMgrName())
//					.trainingStatus(TrainingStatus.PENDING).build());
		} catch (NotificationSendingFailedException e) {
			throw new NotificationSendingFailedException("Notification sending failed with username : " + username);
		}
		return notification.getId();
	}

	@Override
	public Boolean getApprovedLogNotification(Long notifId) {
		return notificationRepository.isTrainingApproved(notifId);

	}

	@Override
	public void updateSystemLogNotification(Long id) {
		Notification notification = notificationRepository.findById(id)
				.orElseThrow(() -> new NotificationNotFoundException("Notification not found with id : " + id));
		notification.setTrainingStatus(TrainingStatus.APPROVED);
		notificationRepository.save(notification);
	}
}
