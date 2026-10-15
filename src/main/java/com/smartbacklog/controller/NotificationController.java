package com.smartbacklog.controller;

import com.smartbacklog.entity.Notification;
import com.smartbacklog.entity.User;
import com.smartbacklog.service.NotificationService;
import com.smartbacklog.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    public NotificationController(NotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    private User getLoggedInUser(UserDetails userDetails) {
        return userService.findByUsername(userDetails.getUsername()).orElse(null);
    }

    @GetMapping
    public String viewNotifications(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = getLoggedInUser(userDetails);
        List<Notification> notifications = notificationService.getNotificationsForUser(user);

        model.addAttribute("currentUser", user);
        model.addAttribute("notifications", notifications);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(user));
        return "common/notifications";
    }

    @GetMapping("/read/{id}")
    public String markAsReadAndRedirect(@PathVariable("id") Long id,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        notificationService.markAsRead(id);
        return "redirect:/notifications";
    }

    @PostMapping("/read-all")
    public String markAllAsRead(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getLoggedInUser(userDetails);
        notificationService.markAllAsReadForUser(user);
        return "redirect:/notifications";
    }
}
