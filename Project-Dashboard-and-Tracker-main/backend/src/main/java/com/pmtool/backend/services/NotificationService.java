package com.pmtool.backend.services;

import java.util.List;

import org.springframework.security.core.Authentication;

import com.pmtool.backend.DTO.NotificationDTO;

public interface NotificationService {

	public List<NotificationDTO> getNotifications(Authentication authentication);

	public void markAsRead(Long id);

	public void markAllAsRead(String username);

	public void addProbStatusAndComment(Long id, String prStatus, String comment);

	public Long addSystemLogNotification(String logComment, Authentication authentication);

	public Boolean getApprovedLogNotification(Long notifId);

	public void updateSystemLogNotification(Long id);

}
