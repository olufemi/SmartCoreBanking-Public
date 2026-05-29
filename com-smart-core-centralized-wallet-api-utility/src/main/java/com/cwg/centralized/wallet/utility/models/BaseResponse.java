package com.cwg.centralized.wallet.utility.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class BaseResponse implements Serializable {

    private static final long serialVersionUID = -5175606879269762371L;

    private int statusCode;

    private String description;
    private String response_message;
    private String response_code;
    private int code;

    private Map<String, Object> data = new HashMap<String, Object>();

    public BaseResponse() {
    }

    public BaseResponse(int statusCode, String description) {
        this(statusCode, description, null);
    }

    public BaseResponse(int statusCode, String description, Map<String, Object> data) {
        this.statusCode = statusCode;
        this.description = description;
        this.data = data;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public void addData(String name, Object value) {
        if (data == null) {
            data = new HashMap<String, Object>();
        }
        data.put(name, value);

    }

}
