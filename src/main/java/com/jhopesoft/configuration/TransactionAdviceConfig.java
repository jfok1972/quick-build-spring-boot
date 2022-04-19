package com.jhopesoft.configuration;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;

/**
 * 
 * @author 蒋锋 jfok1972@qq.com
 * 
 */

@Aspect
@Configuration
public class TransactionAdviceConfig {

	private static final String SERVICE_POINTCUT_EXPRESSION = "execution(* com.jhopesoft..*.service.*Service.*(..))";

	@Autowired
	private TransactionManager transactionManager;

	@Bean(name = "serviceTransactionInterceptor")
	public TransactionInterceptor txAdvice() {

		DefaultTransactionAttribute requiredTran = new DefaultTransactionAttribute();
		requiredTran.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

		DefaultTransactionAttribute readonlyTran = new DefaultTransactionAttribute();
		readonlyTran.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		readonlyTran.setReadOnly(true);

		NameMatchTransactionAttributeSource source = new NameMatchTransactionAttributeSource();
		source.addTransactionalMethod("add*", requiredTran);
		source.addTransactionalMethod("save*", requiredTran);
		source.addTransactionalMethod("delete*", requiredTran);
		source.addTransactionalMethod("update*", requiredTran);
		source.addTransactionalMethod("exec*", requiredTran);
		source.addTransactionalMethod("set*", requiredTran);
		source.addTransactionalMethod("get*", readonlyTran);
		source.addTransactionalMethod("query*", readonlyTran);
		source.addTransactionalMethod("find*", readonlyTran);
		source.addTransactionalMethod("list*", readonlyTran);
		source.addTransactionalMethod("count*", readonlyTran);
		source.addTransactionalMethod("is*", readonlyTran);
		source.addTransactionalMethod("*", requiredTran);
		return new TransactionInterceptor(transactionManager, source);
	}

	@Bean(name = "serviceAdvisor")
	public Advisor txAdviceAdvisor() {
		AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
		pointcut.setExpression(SERVICE_POINTCUT_EXPRESSION);
		return new DefaultPointcutAdvisor(pointcut, txAdvice());
	}

}
