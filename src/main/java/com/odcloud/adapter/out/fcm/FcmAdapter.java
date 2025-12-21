package com.odcloud.adapter.out.fcm;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.SendResponse;
import com.odcloud.application.port.in.command.PushFcmCommand;
import com.odcloud.application.port.out.FcmPort;
import com.odcloud.domain.model.AccountDevice;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class FcmAdapter implements FcmPort {

    private final int chunkSize;

    private final ApnsConfig apnsConfig;

    private final AndroidConfig androidConfig;

    FcmAdapter() {
        this.chunkSize = 500; // FCM Max Chunk Size
        this.androidConfig = AndroidConfig.builder()
            .setPriority(AndroidConfig.Priority.HIGH)
            .setNotification(AndroidNotification.builder()
                .setSound("default")
                .setChannelId("default")
                .build())
            .build();
        this.apnsConfig = ApnsConfig.builder()
            .setAps(Aps.builder()
                .setSound("default")
                .build())
            .build();
    }

    @Override
    public List<AccountDevice> push(PushFcmCommand command) {
        List<AccountDevice> invalidDevices = new ArrayList<>();

        for (List<AccountDevice> devices : chunk(command.devices(), chunkSize)) {
            try {
                log.info("[FCM] Push - title:{},  message: {}, devices: {}",
                    command.title(), command.body(), devices);

                MulticastMessage multicastMessage = MulticastMessage.builder()
                    .addAllTokens(devices.stream()
                        .map(AccountDevice::getFcmToken)
                        .toList()
                    )
                    .setNotification(Notification.builder()
                        .setTitle(command.title())
                        .setBody(command.body())
                        .build())
                    .setAndroidConfig(androidConfig)
                    .setApnsConfig(apnsConfig)
                    .putAllData(command.data())
                    .build();

                BatchResponse response = FirebaseMessaging.getInstance()
                    .sendEachForMulticast(multicastMessage);
                
                if (response.getFailureCount() > 0) {
                    for (int i = 0; i < response.getResponses().size(); i++) {
                        SendResponse sendResponse = response.getResponses().get(i);

                        if (!sendResponse.isSuccessful()) {
                            FirebaseMessagingException e = sendResponse.getException();
                            AccountDevice invalidDevice = devices.get(i);

                            log.error("[FCM] Push failed - device: {}, reason: {}",
                                invalidDevice, e.getMessagingErrorCode());

                            if (e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {
                                invalidDevices.add(invalidDevice);
                            }
                        }
                    }
                }

            } catch (Exception e) {
                log.error("[FCM] Failed to send multicast chunk", e);
            }
        }

        return invalidDevices;
    }

    private static <T> List<List<T>> chunk(List<T> list, int size) {
        List<List<T>> chunks = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            chunks.add(list.subList(i, Math.min(list.size(), i + size)));
        }
        return chunks;
    }
}
