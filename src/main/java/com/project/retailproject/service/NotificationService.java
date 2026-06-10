package com.project.retailproject.service;

import com.project.retailproject.clients.UserClient;
import com.project.retailproject.db.NotificationRepository;
import com.project.retailproject.dto.NotificationRequestDTO;
import com.project.retailproject.dto.NotificationResponseDTO;
import com.project.retailproject.dto.UserResponseDTO;
import com.project.retailproject.exception.ResourceNotFoundException;
import com.project.retailproject.model.Notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserClient userClient;

    public NotificationResponseDTO insertNotification(NotificationRequestDTO ndto) {
        UserResponseDTO user = userClient.findById(ndto.getUserId());

        Notification notification = new Notification();
        notification.setUserId(user.getUserId());
        notification.setMessage(ndto.getMessage());
        notification.setCategory(ndto.getCategory());
        notification.setStatus("UNREAD");
        notification.setCreatedDate(LocalDateTime.now());

        Notification saved = notificationRepository.save(notification);

        NotificationResponseDTO dto = new NotificationResponseDTO();
        dto.setNotificationId(saved.getNotificationId());
        dto.setMessage(saved.getMessage());
        dto.setCategory(saved.getCategory());
        dto.setStatus(saved.getStatus());
        dto.setCreatedDate(saved.getCreatedDate());
        dto.setUserId(saved.getUserId());
        dto.setUserName(user.getUserName());

        return dto;
    }




    public void deleteNotification(Long id) {
        if (!notificationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Notification not found with ID: " + id);
        }
        notificationRepository.deleteById(id);
    }

    public NotificationResponseDTO markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Notification not found with ID: " + id));
        notification.setStatus("READ");
        return mapToDTO(notificationRepository.save(notification));
    }

    public NotificationResponseDTO getNotification(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Notification not found with ID: " + id));
        return mapToDTO(notification);
    }

    public List<NotificationResponseDTO> getAllNotifications() {
        return notificationRepository.findAll()
                .stream().map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<NotificationResponseDTO> getByUser(Long userId) {
        return notificationRepository.findByUserId(userId)
                .stream().map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<NotificationResponseDTO> getUnread(Long userId) {
        return notificationRepository.findByUserId(userId)
                .stream()
                .filter(n -> n.getStatus().equals("UNREAD"))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Page<NotificationResponseDTO> getAllNotificationsPaginated(Pageable pageable) {
        return notificationRepository.findAll(pageable).map(this::mapToDTO);
    }


    private NotificationResponseDTO mapToDTO(Notification n) {
        NotificationResponseDTO dto = new NotificationResponseDTO();
        dto.setNotificationId(n.getNotificationId());
        dto.setMessage(n.getMessage());
        dto.setCategory(n.getCategory());
        dto.setStatus(n.getStatus());
        dto.setCreatedDate(n.getCreatedDate());
        dto.setUserId(n.getUserId());

        return dto;
    }
}