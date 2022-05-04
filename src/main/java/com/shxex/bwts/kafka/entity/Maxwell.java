package com.shxex.bwts.kafka.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author mta09
 */
@Data
public class Maxwell implements Serializable {

    private static final long serialVersionUID = 1L;
    public String database;
    public String table;
    public String type;
    public String ts;
    public String xid;
    public String commit;
    public Object data;
    public Object old;
}
