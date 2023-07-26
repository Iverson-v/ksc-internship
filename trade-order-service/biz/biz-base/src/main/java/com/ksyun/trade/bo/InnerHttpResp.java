package com.ksyun.trade.bo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ksc
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InnerHttpResp {

    private int statusCode;

    private String body;

    private String exceptionMsg;

    @JsonIgnore
    private Exception e;

    public InnerHttpResp(int statusCode, String body){
        this.statusCode = statusCode;
        this.body = body;
        this.exceptionMsg = "";
        this.e = null;
    }

    public boolean isSucc(){
        return this.statusCode >= 200 && this.statusCode < 300;
    }

}
