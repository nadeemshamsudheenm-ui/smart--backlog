package com.smartbacklog.service;

import com.smartbacklog.entity.Notification;
import com.smartbacklog.entity.Role;
import com.smartbacklog.entity.User;
import com.smartbacklog.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification createNotification(User recipient, Role targetRole, String title, String message, String type, String linkUrl) {
        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setTargetRole(targetRole);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type != null ? type : "ANNOUNCEMENT");
        notification.setLinkUrl(linkUrl);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        return notificationRepository.save(notification);
    }

    public void broadcastToRole(Role role, String title, String message, String linkUrl) {
        createNotification(null, role, title, message, "ANNOUNCEMENT", linkUrl);
    }

    public List<Notification> getNotificationsForUser(User user) {
        if (user == null) {
            return List.of();
        }
        return notificationRepository.findForUser(user, user.getRole());
    }

    public long getUnreadCount(User user) {
        if (user == null) {
            return 0;
        }
        return notificationRepository.countUnreadForUser(user, user.getRole());
    }

    public void markAsRead(Long id) {
        notificationRepository.findById(id).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }

    public void markAllAsReadForUser(User user) {
        List<Notification> list = getNotificationsForUser(user);
        for (Notification n : list) {
            if (!n.isRead()) {
                n.setRead(true);
                notificationRepository.save(n);
            }
        }
    }

    public List<Notification> getAllRecentNotifications() {
        return notificationRepository.findTop20ByOrderByCreatedAtDesc();
    }
}
