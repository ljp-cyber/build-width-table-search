package com.shxex.bwts.processKafkaData;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;

import java.io.Serializable;

/**
 * @author ljp
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
    public ObjectNode data;
    public ObjectNode old;
}
