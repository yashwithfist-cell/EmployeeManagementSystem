package com.pmtool.backend.DTO;

import java.time.LocalDateTime;

import com.pmtool.backend.enums.TrainingStatus;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public record NotificationDTO(Long id, String title, String message, boolean readStatus, LocalDateTime createdAt,
		boolean isProbNotif, String prStatus, String comment,@Enumerated(EnumType.STRING) TrainingStatus trainingStatus,String logComment) {

}
