package hello.itemservice.debug.trace.postprocess;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.stereotype.Component;

import hello.itemservice.debug.trace.LogTrace;
import hello.itemservice.debug.trace.traceStatus.TraceStatus;

@Component
public class TraceCustomAdviser implements MethodInterceptor{
    private final LogTrace logTrace;

    public TraceCustomAdviser(LogTrace logTrace) {
        this.logTrace = logTrace;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        TraceStatus status = null;

        try {
            Method method= invocation.getMethod();
            String message = method.getDeclaringClass().getSimpleName() + "."+ method.getName() + "()";
            status=logTrace.begin(message);

            Object result =invocation.proceed();

            
            logTrace.end(status);

            return result;
        } catch (Exception e) {
            logTrace.exception(status, e);
            throw e;
        }
    }
}
