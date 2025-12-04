package hello.itemservice.debug.trace.postprocess;

import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Profile;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import hello.itemservice.debug.trace.LogTrace;

@Profile("trace")
@Component
public class CustomBeanPostProcessor implements BeanPostProcessor {

    private final LogTrace logTrace;

    private final String traceBackPackage; 
    public CustomBeanPostProcessor(LogTrace logTrace, @Value("${custom.aop.target-package}") String traceBackPackage) { 
        this.logTrace = logTrace;
        this.traceBackPackage = traceBackPackage;
    }
    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        String packageName = bean.getClass().getPackageName();
        if (!packageName.startsWith(traceBackPackage)) {
        return bean;
}
        if (beanName.contains("Service")||beanName.contains("Repository")||beanName.contains("Controller")) {
            ProxyFactory proxyFactory = new ProxyFactory(bean);
            DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(Pointcut.TRUE, new TraceCustomAdviser(logTrace));
            
            proxyFactory.addAdvisor(advisor);
            Object proxy = proxyFactory.getProxy();
            return proxy;
        }
        return bean;
    }
}
