package com.earldouglas.springremoting;

import java.util.Date;

import org.springframework.remoting.Remote;

@Remote
public interface DateService {
    public Date getDate();
}
