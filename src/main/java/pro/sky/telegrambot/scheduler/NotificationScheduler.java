package pro.sky.telegrambot.scheduler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class NotificationScheduler {

    private final Logger logger = LoggerFactory.getLogger(NotificationScheduler.class);

    private final NotificationTaskRepository notificationTaskRepository;
    private final TelegramBot telegramBot;

    // Конструктор для внедрения зависимостей
    public NotificationScheduler(NotificationTaskRepository notificationTaskRepository, TelegramBot telegramBot) {
        this.notificationTaskRepository = notificationTaskRepository;
        this.telegramBot = telegramBot;
    }


    @Scheduled(cron = "0 0/1 * * * *")
    @Transactional
    public void checkNotificationsAndSend() {

        LocalDateTime nowTruncated = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);


        List<NotificationTask> tasks = notificationTaskRepository.findByNotificationDateTime(nowTruncated);

        if (tasks.isEmpty()) {
            return;
        }

        logger.info("Найдено {} задач для рассылки на {}", tasks.size(), nowTruncated);


        for (NotificationTask task : tasks) {


            SendMessage message = new SendMessage(
                    task.getChatId(),
                    "🔔 **Напоминание:** " + task.getNotificationText()
            ).parseMode(ParseMode.Markdown);


            telegramBot.execute(message);


            notificationTaskRepository.delete(task);

            logger.info("Уведомление отправлено и задача удалена: ID {}", task.getId());
        }
    }
}