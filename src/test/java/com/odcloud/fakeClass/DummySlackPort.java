package com.odcloud.fakeClass;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DummySlackPort implements SlackPort {

    public SlackRequest lastSentRequest;
    public int sendCount = 0;

    @Override
    public void sendMessage(SlackRequest request) {
        this.lastSentRequest = request;
        this.sendCount++;
        log.info("DummySlackPort sent: channel={}, text={}", request.channel(), request.text());
    }
}