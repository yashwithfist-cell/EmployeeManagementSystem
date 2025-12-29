package com.pmtool.backend.services;

import java.time.LocalDate;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
	private final JavaMailSender mailSender;

	@Override
	public void sendHtmlMail(String to, String subject, String htmlContent) throws MessagingException {
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(htmlContent, true); // true = HTML

		mailSender.send(mimeMessage);
	}

	@Override
	public String buildLeaveEmail(String receiverName, String employeeName, String leaveType, LocalDate start,
			LocalDate end, boolean isApprovalMail) {
		String actionText = isApprovalMail ? "Please log in to the portal to review the request."
				: "You will be notified once the request is approved.";

		return """
				<div style="font-family: Arial, sans-serif; padding: 20px; background:#f5f7fa;">
				    <div style="max-width:600px; margin:auto; background:#ffffff; border-radius:10px; padding:25px; border:1px solid #e1e1e1;">
				        <h2 style="color:#0a5eb7; margin-bottom:10px;">Leave Application Notification</h2>
				        <p style="font-size:15px; color:#333;">Hello <b>%s</b>,</p>

				        <p style="font-size:15px; color:#333;">
				            <b>%s</b> has applied for leave.
				        </p>

				        <table style="border-collapse: collapse; width:100%%; margin-top:15px;">
				            <tr>
				                <td style="padding:10px; border:1px solid #ddd;"><b>Leave Type</b></td>
				                <td style="padding:10px; border:1px solid #ddd;">%s</td>
				            </tr>
				            <tr>
				                <td style="padding:10px; border:1px solid #ddd;"><b>Start Date</b></td>
				                <td style="padding:10px; border:1px; border:1px solid #ddd;">%s</td>
				            </tr>
				            <tr>
				                <td style="padding:10px; border:1px solid #ddd;"><b>End Date</b></td>
				                <td style="padding:10px; border:1px solid #ddd;">%s</td>
				            </tr>
				        </table>

				        <p style="margin-top:20px; font-size:15px; color:#333;">%s</p>

				        <div style="text-align:center; margin-top:30px;">
				            <a href="http://192.168.1.23:3000"
				               style="background:#0a5eb7; text-decoration:none; padding:12px 20px; border-radius:6px; color:white; font-size:15px; font-weight:bold;">
				               Open Portal
				            </a>
				        </div>

				        <p style="margin-top:35px; font-size:13px; color:#777;">
				            Regards,<br/>
				            <b>HR Management System</b>
				        </p>
				    </div>
				</div>
				"""
				.formatted(receiverName, employeeName, leaveType, start, end, actionText);
	}

}
