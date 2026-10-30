package com.gateway.transaction;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.transaction.interceptor.MatchAlwaysTransactionAttributeSource;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionInterceptor;

@Configuration
@SuppressWarnings("java:S1118")
public class TransactionConfig {

    private static final String USECASES = "execution(public * com..*Usecase.*(..))";
    private static final String FIND_USECASES = "execution(public * com..Find*Usecase.*(..))";

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public static Advisor readOnlyUsecaseAdvisor(BeanFactory beanFactory) {
        RuleBasedTransactionAttribute attr = new RuleBasedTransactionAttribute();
        attr.setReadOnly(true);
        return advisor(beanFactory, attr, FIND_USECASES);
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public static Advisor writeUsecaseAdvisor(BeanFactory beanFactory) {
        return advisor(beanFactory, new RuleBasedTransactionAttribute(),
            USECASES + " && !" + FIND_USECASES);
    }

    private static Advisor advisor(
        BeanFactory beanFactory,
        TransactionAttribute attr,
        String expression) {
        MatchAlwaysTransactionAttributeSource source = new MatchAlwaysTransactionAttributeSource();
        source.setTransactionAttribute(attr);

        TransactionInterceptor interceptor = new TransactionInterceptor();
        interceptor.setTransactionAttributeSource(source);
        interceptor.setTransactionManagerBeanName("transactionManager");
        interceptor.setBeanFactory(beanFactory);

        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(expression);

        return new DefaultPointcutAdvisor(pointcut, interceptor);
    }
}