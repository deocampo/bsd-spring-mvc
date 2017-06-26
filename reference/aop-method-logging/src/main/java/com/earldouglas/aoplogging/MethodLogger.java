package com.earldouglas.aoplogging;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
@Aspect
public class MethodLogger {

	@Around("execution(* com.earldouglas..*.*(..))")
	public Object timeMethod(ProceedingJoinPoint joinPoint) throws Throwable {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		Object retVal = joinPoint.proceed();

		stopWatch.stop();

		StringBuffer logMessageStringBuffer = new StringBuffer();
		logMessageStringBuffer.append(joinPoint.getTarget().getClass().getName());
		logMessageStringBuffer.append(".");
		logMessageStringBuffer.append(joinPoint.getSignature().getName());
		logMessageStringBuffer.append("(");
		logMessageStringBuffer.append(joinPoint.getArgs());
		logMessageStringBuffer.append(")");
		logMessageStringBuffer.append(" execution time: ");
		logMessageStringBuffer.append(stopWatch.getTotalTimeMillis());
		logMessageStringBuffer.append(" ms");

		Logger.getLogger(this.getClass()).info(logMessageStringBuffer.toString());

		return retVal;
	}

}
