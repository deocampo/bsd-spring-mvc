package com.earldouglas.scopedbeans;

import java.util.Date;

public class MetadataImpl implements Metadata {

    private final Date date;

    public MetadataImpl(Date date) {
        this.date = date;
    }

    @Override
    public String getDate() {
        return date.toString();
    }
}
