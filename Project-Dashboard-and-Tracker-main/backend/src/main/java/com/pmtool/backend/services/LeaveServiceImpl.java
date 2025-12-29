package com.pmtool.backend.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.pmtool.backend.DTO.HolidayDto;
import com.pmtool.backend.DTO.LeaveDto;
import com.pmtool.backend.constants.LeaveConstants;
import com.pmtool.backend.entity.Employee;
import com.pmtool.backend.entity.Leave;
import com.pmtool.backend.entity.Notification;
import com.pmtool.backend.enums.LeaveStatus;
import com.pmtool.backend.enums.LeaveType;
import com.pmtool.backend.enums.Role;
import com.pmtool.backend.enums.Status;
import com.pmtool.backend.exception.EmailSendingFailedException;
import com.pmtool.backend.exception.EmployeeNotFoundException;
import com.pmtool.backend.exception.LeaveApplyException;
import com.pmtool.backend.exception.LeaveNotAllowedException;
import com.pmtool.backend.exception.LeaveNotFoundException;
import com.pmtool.backend.exception.ManagerNotFoundException;
import com.pmtool.backend.exception.TeamLeadNotFoundException;
import com.pmtool.backend.mapper.LeaveMapper;
import com.pmtool.backend.repository.EmployeeRepository;
import com.pmtool.backend.repository.HolidayRepository;
import com.pmtool.backend.repository.LeaveRepository;
import com.pmtool.backend.repository.NotificationRepository;
import com.pmtool.backend.util.AttendanceUtils;

@Service
public class LeaveServiceImpl implements LeaveService {

	@Autowired
	private LeaveRepository leaveRepository;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private HolidayRepository holidayRepository;

	@Autowired
	private EmailService emailService;

	@Autowired
	NotificationRepository notificationRepository;

	@Autowired
	LeaveMapper mapper;

	public LeaveDto applyLeave(LeaveDto leaveDto, Authentication authentication) {
		String userName = authentication.getName();

		List<LocalDate> holidayList = holidayRepository.findAllHolidayDates();
		if (leaveDto.getStartDate().equals(leaveDto.getEndDate())) {
			if (holidayList.contains(leaveDto.getStartDate())) {
				throw new LeaveNotAllowedException("Entered Date is Holiday");
			}
		}
		List<Leave> leaveList = leaveRepository.findAllLeaveDates(userName, leaveDto.getStartDate(),
				leaveDto.getEndDate());
		List<LeaveDto> leaveDtoList = leaveList.stream().map(leave -> mapper.convertToLeaveDto(leave)).toList();
		double leaveDays = AttendanceUtils.calculateLeaveDays(leaveDtoList, leaveDto.getStartDate(),
				leaveDto.getEndDate(), holidayList);
		if (leaveDto.getLeaveType() == LeaveType.CASUAL) {
			if (leaveDays == 1) {
				LocalDate monthStart = YearMonth.from(leaveDto.getStartDate()).atDay(1);
				LocalDate monthEnd = YearMonth.from(leaveDto.getEndDate()).atEndOfMonth();
				double totLeaveCounts = leaveRepository.findCurrentMonthLeaves(userName, monthStart, monthEnd);
				double totalLeaves = totLeaveCounts + leaveDays;
				if (totalLeaves > 3) {
					throw new LeaveNotAllowedException("Casual leave days count exceeded cannot take casual leaves !!");
				}
			}
			if (leaveDays > 3 && leaveDto.getLeaveType().equals(LeaveType.CASUAL)) {
				throw new LeaveNotAllowedException("Casual Leave limit is 3 but applied leave days : " + leaveDays);
			}
		} else {
			if (leaveDays >= 4 && leaveDays <= 10) {
				int year = LocalDate.now().getYear();
				LocalDate yearStart = LocalDate.of(year, 01, 01);
				LocalDate yearEnd = LocalDate.of(year, 12, 31);
				double totLeaveCounts = leaveRepository.findCurrentMonthLeaves(userName, yearStart, yearEnd);
				double totalLeaves = totLeaveCounts + leaveDays;
				if (totalLeaves > 10) {
					throw new LeaveNotAllowedException(
							"Earned Leave limit is 10 per year, Applied extra leave days : " + (totalLeaves - 10.0));
				}
			} else {
				throw new LeaveNotAllowedException(
						"Minimum Earned Leaves : 4,Maximum Earned Leaves : 10,Applied leave days : " + leaveDays);
			}
		}

		Employee employee = employeeRepository.findByUsername(userName)
				.orElseThrow(() -> new EmployeeNotFoundException("Employee Not Found with username : " + userName));
		leaveDto.setStatus(LeaveStatus.PENDING);
		leaveDto.setDays(leaveDays);
		leaveDto.setUsername(userName);
		LeaveDto unPaidLeaveDto = calculateLeaves(leaveDto);
		Leave leaveEntity = mapper.convertToLeave(unPaidLeaveDto, employee);
		Leave savedLeave = leaveRepository.save(leaveEntity);
		if (savedLeave != null) {
			String empName = savedLeave.getEmployee().getName();
			String leaveType = savedLeave.getLeaveType().toString();
			LocalDate start = savedLeave.getStartDate();
			LocalDate end = savedLeave.getEndDate();
			Employee employeeObj = savedLeave.getEmployee();
			Employee managerObj = employeeRepository.findByUsername(savedLeave.getProjectManagerName())
					.orElseThrow(() -> new ManagerNotFoundException("Manager not found with username : " + userName));
			Employee teamLeadObj = null;
			if (savedLeave.getTeamLeadName() != null) {
				teamLeadObj = employeeRepository.findByUsername(savedLeave.getTeamLeadName()).orElseThrow(
						() -> new TeamLeadNotFoundException("Team Lead not found with username : " + userName));
			}

			List<Notification> notificationList = Arrays.asList(convertToNotification(managerObj, employeeObj),
					convertToNotification(teamLeadObj, employeeObj));
			notificationRepository.saveAll(notificationList);
//
//			try {
//				String managerHtml = emailService.buildLeaveEmail(managerObj.getName(), empName, leaveType, start, end,
//						true);
//				emailService.sendHtmlMail(managerObj.getMailId(), "Leave Request Approval – " + empName, managerHtml);
//				if (teamLeadObj != null) {
//					String leadHtml = emailService.buildLeaveEmail(teamLeadObj.getName(), empName, leaveType, start,
//							end, true);
//					emailService.sendHtmlMail(teamLeadObj.getMailId(), "Leave Request Approval – " + empName, leadHtml);
//				}
//				String empHtml = emailService.buildLeaveEmail(empName, empName, leaveType, start, end, false);
//				emailService.sendHtmlMail(employeeObj.getMailId(), "Leave Application Submitted", empHtml);
//
//			} catch (Exception e) {
//				throw new EmailSendingFailedException("Unable to send mail", e);
//			}
		}
		return mapper.convertToLeaveDto(savedLeave);
	}

	@Override
	public List<LeaveDto> getAllLeaves() {
		return leaveRepository.findAll().stream().map(leave -> mapper.convertToLeaveDto(leave)).toList();
	}

	@Override
	public List<LeaveDto> getLeavesByEmployee(Authentication authentication) {
		String userName = authentication.getName();
		return leaveRepository.findByEmployeeName(userName).stream().map(mapper::convertToLeaveDto)
				.map(this::calculateLeaves).toList();
	}

	@Override
	public LeaveDto updateStatus(Long id, String status) {
		Leave leave = leaveRepository.findById(id)
				.orElseThrow(() -> new LeaveNotFoundException("Leave not found with leave id : " + id));
		leave.setStatus(LeaveStatus.valueOf(status.toUpperCase()));
		if (status.equalsIgnoreCase(LeaveConstants.APPROVED_BY_HUMAN_RESOURCE)) {
			Notification notification = new Notification();
			notification.setEmployee(leave.getEmployee());
			notification.setUsername(leave.getEmployee().getUsername());
			notification.setCreatedAt(LocalDateTime.now());
			notification.setTitle("Leave Approve Status");
			notification.setMessage("Hi " + leave.getEmployee().getUsername() + ", Your leave approved from "
					+ leave.getStartDate() + " to " + leave.getEndDate() + ".");
			notification.setStatus(Status.COMPLETED);
			notificationRepository.save(notification);

//			notificationRepository
//					.save(Notification.builder().createdAt(LocalDateTime.now()).title("Leave Approve Status")
//							.message("Hi " + leave.getEmployee().getUsername() + ", Your leave approved from "
//									+ leave.getStartDate() + " to " + leave.getEndDate() + ".")
//							.employee(leave.getEmployee()).build());
		}
		return mapper.convertToLeaveDto(leaveRepository.save(leave));
	}

	@Override
	public List<String> getEmployeesByRole(Role role, Authentication authentication) {
		String userName = authentication.getName();
		List<Employee> employeeList = new ArrayList<>();
		Employee employee = employeeRepository.findByUsername(userName)
				.orElseThrow(() -> new EmployeeNotFoundException("Employee not found with username : " + userName));
		employeeList.add(employee);
		return employeeList.stream().map(emp -> {
			if (role == Role.PROJECT_MANAGER) {
				return emp.getMgrName();
			} else {
				return emp.getTeamLeadName();
			}
		}).toList();
	}

	@Override
	public List<LeaveDto> getAllByManager(Authentication authentication) {

		String projectManagerName = authentication.getName();
		return leaveRepository.findByProjectManagerName(projectManagerName).stream()
				.map(leave -> mapper.convertToLeaveDto(leave)).toList();
	}

	@Override
	public List<LeaveDto> getAllByTeamLead(Authentication authentication) {
		String teamLeadName = authentication.getName();
		return leaveRepository.findByTeamLeadName(teamLeadName).stream().map(leave -> mapper.convertToLeaveDto(leave))
				.toList();
	}

	public LeaveDto calculateLeaves(LeaveDto leaveDto) {
		LocalDate startOfYear = LocalDate.now().withDayOfYear(1);
		LocalDate endOfYear = LocalDate.now().withMonth(12).withDayOfMonth(31);
		String userName = leaveDto.getUsername();
		double yearlyCasualLeave = 12.0;
		double earnedCasualLeave = 0.0;
		Employee emp = employeeRepository.findByUsername(userName)
				.orElseThrow(() -> new EmployeeNotFoundException("Employee Not Found with username : " + userName));

		LocalDate joiningDate = emp.getJoinDate();
		LocalDate professionalDate = emp.getProfPeriodEndDate();
		LocalDate today = LocalDate.now();

		// ================== PAID LEAVE ==================
		long monthsWorked = ChronoUnit.MONTHS.between(joiningDate, today);
		double earnedPaidLeave = monthsWorked * 1.25;

		double usedPaidLeave = leaveRepository.sumPaidLeaveByUsername(userName, professionalDate.plusDays(1), endOfYear)
				.orElse(0.0);
		double pendingPaidLeave = 0.0;
		// ================== CASUAL LEAVE ==================
		double usedCasualLeave = 0.0;
		double unPaidLeave = 0.0;

		if (!today.isBefore(professionalDate)) {
			pendingPaidLeave = earnedPaidLeave - usedPaidLeave;
			if (professionalDate.getYear() == today.getYear()) {
				yearlyCasualLeave = yearlyCasualLeave - (professionalDate.getMonthValue());
				usedCasualLeave = leaveRepository
						.sumCasualLeaveByUsername(userName, professionalDate.plusDays(1), endOfYear).orElse(0.0);
			} else {
				usedCasualLeave = leaveRepository.sumCasualLeaveByUsername(userName, startOfYear, endOfYear)
						.orElse(0.0);
			}
			earnedCasualLeave = yearlyCasualLeave;
		} else {
			unPaidLeave = leaveDto.getDays();
		}

		int unPaidLeaveCount = 0;
		for (LocalDate date = leaveDto.getStartDate(); !date.isAfter(leaveDto.getEndDate()); date = date.plusDays(1)) {
			if (date.isBefore(professionalDate)) {
				unPaidLeaveCount++;
			}
		}
		if (unPaidLeaveCount >= 0) {
			unPaidLeave = unPaidLeave + unPaidLeaveCount;
		}

		double pendingCasualLeave = earnedCasualLeave - usedCasualLeave;
		leaveDto.setYearlyCasualLeave(earnedCasualLeave);
		leaveDto.setPendingCasualLeave((pendingCasualLeave >= 0) ? pendingCasualLeave : 0.0);
		leaveDto.setYearlyPaidLeave(earnedPaidLeave);
		leaveDto.setPendingPaidLeave((pendingPaidLeave >= 0) ? pendingPaidLeave : 0.0);
		if (pendingCasualLeave == 0 || pendingPaidLeave == 0) {
			unPaidLeave = leaveDto.getDays();
		} else if (pendingCasualLeave < 0) {
			unPaidLeave = unPaidLeave - pendingCasualLeave;
		} else if (pendingPaidLeave < 0) {
			unPaidLeave = unPaidLeave - pendingPaidLeave;
		}
		leaveDto.setUnPaidLeave(unPaidLeave);
		return leaveDto;
	}

	private Notification convertToNotification(Employee head, Employee employee) {
		Notification notification = new Notification();
		notification.setEmployee(employee);
		notification.setCreatedAt(LocalDateTime.now());
		notification.setTitle("Leave Approve Status");
		notification.setMessage("Hi " + head.getUsername() + "," + employee.getUsername() + " applied for leave");
		notification.setUsername(head.getUsername());
		return notification;
//		return Notification.builder().username(head.getUsername()).createdAt(LocalDateTime.now())
//				.title("Leave Approve Request")
//				.message("Hi " + head.getUsername() + "," + employee.getUsername() + " applied for leave").build();
	}

	@Override
	public List<HolidayDto> getAllHolidays() {
		return holidayRepository.findAll().stream().map(holiday -> {
			return HolidayDto.builder().id(holiday.getId()).holidayName(holiday.getHolidayName())
					.holidayDate(holiday.getHolidayDate()).build();
		}).toList();
	}

}
