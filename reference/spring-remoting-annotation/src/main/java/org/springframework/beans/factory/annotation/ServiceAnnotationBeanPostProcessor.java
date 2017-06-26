package org.springframework.beans.factory.annotation;

import java.rmi.RemoteException;

import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.remoting.Service;
import org.springframework.remoting.ServiceType;
import org.springframework.remoting.caucho.BurlapServiceExporter;
import org.springframework.remoting.caucho.HessianServiceExporter;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;
import org.springframework.remoting.rmi.RmiServiceExporter;

public class ServiceAnnotationBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter implements PriorityOrdered {

    private int order = Ordered.LOWEST_PRECEDENCE - 1;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        Service service = AnnotationUtils.findAnnotation(bean.getClass(), Service.class);

        Object resultBean = bean;

        if (null != service) {

            if (ServiceType.HTTP == service.serviceType()) {

                HttpInvokerServiceExporter httpInvokerServiceExporter = new HttpInvokerServiceExporter();
                httpInvokerServiceExporter.setServiceInterface(service.serviceInterface());
                httpInvokerServiceExporter.setService(bean);
                httpInvokerServiceExporter.afterPropertiesSet();
                resultBean = httpInvokerServiceExporter;

            } else if (ServiceType.HESSIAN == service.serviceType()) {

                HessianServiceExporter hessianServiceExporter = new HessianServiceExporter();
                hessianServiceExporter.setServiceInterface(service.serviceInterface());
                hessianServiceExporter.setService(bean);
                hessianServiceExporter.afterPropertiesSet();
                resultBean = hessianServiceExporter;

            } else if (ServiceType.BURLAP == service.serviceType()) {

                BurlapServiceExporter burlapServiceExporter = new BurlapServiceExporter();
                burlapServiceExporter.setServiceInterface(service.serviceInterface());
                burlapServiceExporter.setService(bean);
                burlapServiceExporter.afterPropertiesSet();
                resultBean = burlapServiceExporter;

            } else if (ServiceType.RMI == service.serviceType()) {

                RmiServiceExporter rmiServiceExporter = new RmiServiceExporter();
                rmiServiceExporter.setServiceInterface(service.serviceInterface());
                rmiServiceExporter.setService(bean);
                rmiServiceExporter.setServiceName(beanName);
                try {
                    rmiServiceExporter.afterPropertiesSet();
                } catch (RemoteException remoteException) {
                    throw new FatalBeanException("Exception initializing RmiServiceExporter", remoteException);
                }
                resultBean = rmiServiceExporter;
            }
        }

        return resultBean;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return order;
    }
}
