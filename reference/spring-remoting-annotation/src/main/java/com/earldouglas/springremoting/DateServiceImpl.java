package com.earldouglas.springremoting;

import java.util.Date;

import org.springframework.remoting.Service;
import org.springframework.remoting.ServiceType;

@Service(serviceInterface = DateService.class, serviceType = ServiceType.HTTP)
public class DateServiceImpl implements DateService {

    @Override
    public Date getDate() {
        return new Date();
    }
}
