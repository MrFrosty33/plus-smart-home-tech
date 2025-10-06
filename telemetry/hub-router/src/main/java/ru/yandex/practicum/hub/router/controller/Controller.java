package ru.yandex.practicum.hub.router.controller;

import com.google.protobuf.Empty;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.grpc.telemetry.analyzer.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequestProto;

@GrpcService
public class Controller extends HubRouterControllerGrpc.HubRouterControllerImplBase {

    public Empty handleDeviceAction(DeviceActionRequestProto request) {
        // класс-заглушка, никакой логики не требовалось делать
        return Empty.getDefaultInstance();
    }
}
