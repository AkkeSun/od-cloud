package com.odcloud.fakeClass;

import com.odcloud.infrastructure.util.UserAgentUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FakeUserAgentUtil implements UserAgentUtil {

    public String mockUserAgent = "Mozilla/5.0 (Test Browser)";

    @Override
    public String getUserAgent() {
        log.info("FakeUserAgentUtil getUserAgent: {}", mockUserAgent);
        return mockUserAgent;
    }

    public void reset() {
        mockUserAgent = "Mozilla/5.0 (Test Browser)";
    }
}
