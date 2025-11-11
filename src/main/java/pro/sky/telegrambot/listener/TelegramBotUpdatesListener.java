package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Service
public class TelegramBotUpdatesListener implements UpdatesListener {
    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private static final Pattern NOTIFICATION_PATTERN = Pattern.compile("(\\d{2}\\.\\d{2}\\.\\d{4}\\s\\d{2}:\\d{2})(\\s+)(.+)");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private final TelegramBot telegramBot;
    private final NotificationTaskRepository notificationTaskRepository;
    public TelegramBotUpdatesListener(TelegramBot telegramBot, NotificationTaskRepository notificationTaskRepository) {
        this.telegramBot = telegramBot;
        this.notificationTaskRepository = notificationTaskRepository;
    }
    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);

            if (update.message() != null && update.message().text() != null) {
                Long chatId = update.message().chat().id();
                String messageText = update.message().text();

                // 1. Обработка команды /start
                if (messageText.equals("/start")) {
                    String welcomeMessage = "👋 Привет! Я бот-планировщик. \n\n" +
                            "Для создания напоминания отправь сообщение в формате:\n" +
                            "**ДД.ММ.ГГГГ ЧЧ:ММ Текст напоминания**\n" +
                            "Пример: 01.01.2026 10:30 Сделать домашнюю работу";

                    sendMessage(chatId, welcomeMessage);
                    return;
                }
                Matcher matcher = NOTIFICATION_PATTERN.matcher(messageText);

                if (matcher.matches()) {
                    String dateTimeString = matcher.group(1);
                    String notificationText = matcher.group(3);

                    try {
                        LocalDateTime notificationDateTime = LocalDateTime.parse(dateTimeString, DATE_TIME_FORMATTER);

                        if (notificationDateTime.isBefore(LocalDateTime.now())) {
                            sendMessage(chatId, " Нельзя установить напоминание на прошедшее время.");
                            return;
                        }


                        NotificationTask task = new NotificationTask();
                        task.setChatId(chatId);
                        task.setNotificationText(notificationText);
                        task.setNotificationDateTime(notificationDateTime);

                        notificationTaskRepository.save(task);

                        sendMessage(chatId, "Напоминание **«" + notificationText + "»** запланировано на **" + dateTimeString + "**!");

                    } catch (DateTimeParseException e) {
                        logger.error("Ошибка парсинга даты: {}", dateTimeString, e);
                        sendMessage(chatId, " Некорректный формат даты/времени. Используйте **ДД.ММ.ГГГГ ЧЧ:ММ**.");
                    }
                } else {
                    sendMessage(chatId, "Не удалось распознать формат. Используйте: **ДД.ММ.ГГГГ ЧЧ:ММ Текст**.");
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage(chatId, text).parseMode(ParseMode.Markdown);
        telegramBot.execute(message);
    }
}
