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

    public static final String INSERT = "insert";
    public static final String DELETE = "delete";
    public static final String UPDATE = "update";
    public static final String UNKNOWN = "unknown";

    public static final Integer deleted = 1;
    public static final Integer notDeleted = 0;
    public static final String IS_DELETED = "is_deleted";

    private String database;
    private String table;
    private String type;
    private String ts;
    private String xid;
    private String commit;
    private ObjectNode data;
    private ObjectNode old;

    public String getType() {
        if (!UPDATE.equals(type)) {
            return type;
        }
        JsonNode newDeletedJsonNode = this.getData().get(Maxwell.IS_DELETED);
        JsonNode oldIsDeletedJsonNode = this.getOld().get(Maxwell.IS_DELETED);
        if (newDeletedJsonNode == null && oldIsDeletedJsonNode == null) {
            return type;
        }
        int newIsDeleted = newDeletedJsonNode.asInt(Maxwell.notDeleted);
        int oldIsDeleted = oldIsDeletedJsonNode.asInt(Maxwell.notDeleted);
        if (Maxwell.notDeleted.equals(oldIsDeleted) && Maxwell.deleted.equals(newIsDeleted)) {
            return DELETE;
        }
        if (Maxwell.deleted.equals(oldIsDeleted) && Maxwell.notDeleted.equals(newIsDeleted)) {
            return INSERT;
        }
        if (Maxwell.notDeleted.equals(oldIsDeleted) && Maxwell.notDeleted.equals(newIsDeleted)) {
            return UPDATE;
        }
        return UNKNOWN;
    }
}
