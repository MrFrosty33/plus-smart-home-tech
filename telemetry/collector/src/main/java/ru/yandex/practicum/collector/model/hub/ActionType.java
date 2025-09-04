package ru.yandex.practicum.collector.model.hub;

import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;

public enum ActionType {
    ACTIVATE,
    DEACTIVATE,
    INVERSE,
    SET_VALUE;

    public static ActionTypeAvro toAvro(ActionType actionType) {
        return ActionTypeAvro.valueOf(actionType.name());
    }
}
