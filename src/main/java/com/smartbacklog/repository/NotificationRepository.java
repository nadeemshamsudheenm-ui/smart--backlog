package com.smartbacklog.repository;

import com.smartbacklog.entity.Notification;
import com.smartbacklog.entity.Role;
import com.smartbacklog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientOrderByCreatedAtDesc(User recipient);

    @Query("SELECT n FROM Notification n WHERE n.recipient = :user OR n.targetRole = :role OR (n.recipient IS NULL AND n.targetRole IS NULL) ORDER BY n.createdAt DESC")
    List<Notification> findForUser(@Param("user") User user, @Param("role") Role role);

    @Query("SELECT COUNT(n) FROM Notification n WHERE (n.recipient = :user OR n.targetRole = :role OR (n.recipient IS NULL AND n.targetRole IS NULL)) AND n.isRead = false")
    long countUnreadForUser(@Param("user") User user, @Param("role") Role role);

    List<Notification> findTop20ByOrderByCreatedAtDesc();
}
