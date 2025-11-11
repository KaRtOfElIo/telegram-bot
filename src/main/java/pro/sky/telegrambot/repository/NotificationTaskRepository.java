package pro.sky.telegrambot.repository;

import pro.sky.telegrambot.entity.NotificationTask; // Ваш пакет
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationTaskRepository extends JpaRepository<NotificationTask, Long> {

    List<NotificationTask> findByNotificationDateTime(LocalDateTime notificationDateTime);
}