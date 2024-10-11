package io.jexxa.adapterapi.invocation;

import io.jexxa.adapterapi.interceptor.AfterInterceptor;
import io.jexxa.adapterapi.interceptor.AroundInterceptor;
import io.jexxa.adapterapi.interceptor.BeforeInterceptor;
import io.jexxa.adapterapi.invocation.context.ConsumerInvocationContext;
import io.jexxa.adapterapi.invocation.context.FunctionInvocationContext;
import io.jexxa.adapterapi.invocation.context.MethodInvocationContext;
import io.jexxa.adapterapi.invocation.context.RunnableInvocationContext;
import io.jexxa.adapterapi.invocation.context.SupplierInvocationContext;
import io.jexxa.adapterapi.invocation.function.SerializableConsumer;
import io.jexxa.adapterapi.invocation.function.SerializableFunction;
import io.jexxa.adapterapi.invocation.function.SerializableRunnable;
import io.jexxa.adapterapi.invocation.function.SerializableSupplier;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnusedReturnValue")
public class SharedInvocationHandler implements AroundInterceptor, BeforeInterceptor, AfterInterceptor, JexxaInvocationHandler {
    private final List<BeforeInterceptor> beforeList = new ArrayList<>();
    private final List<AfterInterceptor> afterList = new ArrayList<>();
    private final List<AroundInterceptor> aroundList = new ArrayList<>();

    @Override
    public void before(InvocationContext invocationContext)
    {
        beforeList.forEach(element -> element.before(invocationContext));
    }

    @Override
    public void after(InvocationContext invocationContext)
    {
        afterList.forEach(element -> element.after(invocationContext));
    }

    @Override
    public void around(InvocationContext invocationContext)
    {
        invocationContext.proceed();
    }

    public SharedInvocationHandler registerAround(AroundInterceptor interceptor) {
        aroundList.add(interceptor);
        return this;
    }

    public SharedInvocationHandler registerBefore(BeforeInterceptor interceptor) {
        beforeList.add(interceptor);
        return this;
    }

    public SharedInvocationHandler registerAfter(AfterInterceptor interceptor) {
        afterList.add(interceptor);
        return this;
    }


    @Override
    public Object invoke(Method method, Object object, Object[] args)
    {
        var invocationContext = new MethodInvocationContext(method, object, args, aroundList);

        invoke(invocationContext);

        return invocationContext.getReturnValue();
    }

    @Override
    public void invoke(Object targetObject, SerializableRunnable runnable)
    {
        var invocationContext = new RunnableInvocationContext(targetObject, runnable, aroundList);
        invoke(invocationContext);
    }

    @Override
    public <T> void invoke(Object targetObject,SerializableConsumer<T> consumer, T argument)
    {
        var invocationContext = new ConsumerInvocationContext<>(targetObject, consumer, argument, aroundList);
        invoke(invocationContext);
    }

    @Override
    public <T> T invoke(Object targetObject,SerializableSupplier<T> supplier) {
        var invocationContext = new SupplierInvocationContext<>(targetObject, supplier, aroundList);
        invoke(invocationContext);
        return invocationContext.getReturnValue();
    }

    @Override
    public JexxaInvocationHandler newInstance() {
        return new SharedInvocationHandler();
    }

    @Override
    public <T, R> R invoke(Object targetObject,SerializableFunction<T, R> function, T argument) {
        var invocationContext = new FunctionInvocationContext<>(targetObject, function, argument, aroundList);
        invoke(invocationContext);
        return invocationContext.getReturnValue();
    }

    protected void invoke(InvocationContext invocationContext)  {
        before(invocationContext);
        around(invocationContext);
        after(invocationContext);
    }

}
