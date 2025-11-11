CREATE TABLE notification_task
    (
        id                     BIGSERIAL                             NOT NULL,
        chat_id                BIGINT                                NOT NULL,
        notification_text      TEXT                                  NOT NULL,
        notification_date_time TIMESTAMP WITHOUT TIME ZONE           NOT NULL,
        CONSTRAINT pk_notification_task PRIMARY KEY (id)
    );