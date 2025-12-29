package com.pmtool.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pmtool.backend.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	List<Notification> findByUsernameOrderByCreatedAtDesc(String username);

	@Query("SELECT n FROM Notification n WHERE n.prStatus IN ('APPROVED','REJECTED')")
	List<Notification> findAllProbationNotifications();

	@Query("""
			SELECT CASE WHEN n.trainingStatus = com.pmtool.backend.enums.TrainingStatus.APPROVED
			            THEN true ELSE false END
			FROM Notification n
			WHERE n.id = :notifId
			""")
	Boolean isTrainingApproved(@Param("notifId") Long notifId);
}
