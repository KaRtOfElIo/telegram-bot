package pro.sky.telegrambot.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification_task")
public class NotificationTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long chatId;
    private String notificationText;
    private LocalDateTime notificationDateTime;
    public NotificationTask() {}


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getChatId() { return chatId; }
    public void setChatId(Long chatId) { this.chatId = chatId; }

    public String getNotificationText() { return notificationText; }
    public void setNotificationText(String notificationText) { this.notificationText = notificationText; }

    public LocalDateTime getNotificationDateTime() { return notificationDateTime; }
    public void setNotificationDateTime(LocalDateTime notificationDateTime) { this.notificationDateTime = notificationDateTime; }
}