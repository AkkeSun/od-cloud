package com.odcloud.application.port.out;

import com.odcloud.adapter.out.client.SlackRequest;

public interface SlackPort {

    void sendMessage(SlackRequest request);
}
