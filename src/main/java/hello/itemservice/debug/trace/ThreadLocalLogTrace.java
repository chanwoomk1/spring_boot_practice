package hello.itemservice.debug.trace;

import org.springframework.stereotype.Component;

import hello.itemservice.debug.trace.traceStatus.TraceId;
import hello.itemservice.debug.trace.traceStatus.TraceStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ThreadLocalLogTrace implements LogTrace {

    private static final String START_PREFIX = "-->";
    private static final String COMPLETE_PREFIX = "<--";
    private static final String EX_PREFIX = "<X-";

    private final ThreadLocal<TraceId> traceIdHolder = new ThreadLocal<>();

    @Override
    public TraceStatus begin(String message) {
        TraceId traceId = syncTraceId();
        Long startTimeMs = System.currentTimeMillis();
        
        log.info("[{}] {}{}", traceId.getId(), addSpace(START_PREFIX,
                traceId.getLevel()), message);
        
        return new TraceStatus(traceId, startTimeMs, message);
    }

    @Override
    public void end(TraceStatus status) {
        complete(status, null);
    }

    @Override
    public void exception(TraceStatus status, Exception e) {
        complete(status, e);
    }

    private void complete(TraceStatus status, Exception e) {
        Long stopTimeMs = System.currentTimeMillis();
        long resultTimeMs = stopTimeMs - status.getStartTimeMs();
        TraceId traceId = status.getTraceId();
        
        if (e == null) {
            log.info("[{}] {}{} time={}ms", traceId.getId(),
                    addSpace(COMPLETE_PREFIX, traceId.getLevel()), status.getMessage(),
                    resultTimeMs);
        } else {
            log.info("[{}] {}{} time={}ms ex={}", traceId.getId(),
                    addSpace(EX_PREFIX, traceId.getLevel()), status.getMessage(), resultTimeMs,
                    e.toString());
        }
        
        releaseTraceId();
    }

    private TraceId syncTraceId() {
        TraceId traceId = traceIdHolder.get();
        
        if (traceId == null) {
            traceId = new TraceId();
        } else {
            traceId = traceId.createNextId();
        }
        
        traceIdHolder.set(traceId);
        return traceId;
    }

    private void releaseTraceId() {
        TraceId traceId = traceIdHolder.get();
        
        if (traceId.isFirstLevel()) {
            traceIdHolder.remove(); 
        } else {
            traceIdHolder.set(traceId.createPreviousId());
        }
    }

    private static String addSpace(String prefix, int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append( (i == level - 1) ? "|" + prefix : "| ");
        }
        return sb.toString();
    }
}
