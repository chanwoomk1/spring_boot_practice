package hello.itemservice.debug.trace.traceStatus;


import java.util.UUID;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class TraceId {
    private final String id;
    private final int level;
    public TraceId() {
        this.id = createId();
        this.level = 0;
    }
    private String createId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
    public TraceId createNextId() {
        return new TraceId(id, level + 1);
    }
    public TraceId createPreviousId() {
        return new TraceId(id, level - 1);
    }
    public boolean isFirstLevel() {
        return level == 0;
    }
}