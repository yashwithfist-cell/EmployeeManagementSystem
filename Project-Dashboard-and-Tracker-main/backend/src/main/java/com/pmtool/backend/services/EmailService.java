package com.pmtool.backend.services;

import java.time.LocalDate;

import jakarta.mail.MessagingException;

public interface EmailService {

	public void sendHtmlMail(String to, String subject, String htmlContent) throws MessagingException;

	public String buildLeaveEmail(String receiverName, String employeeName, String leaveType, LocalDate start,
			LocalDate end, boolean isApprovalMail);
}
