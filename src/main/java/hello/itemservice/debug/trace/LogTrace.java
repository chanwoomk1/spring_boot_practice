package hello.itemservice.debug.trace;


import hello.itemservice.debug.trace.traceStatus.TraceStatus;

public interface LogTrace {
    public TraceStatus begin(String message);
    public void end(TraceStatus status);
    public void exception(TraceStatus status, Exception e);
}
