package hello.itemservice.debug.trace;

import org.springframework.context.annotation.Configuration;

@Configuration
class LogTraceConfig {
    public LogTrace logTrace() {
        return new ThreadLocalLogTrace();
    }
}