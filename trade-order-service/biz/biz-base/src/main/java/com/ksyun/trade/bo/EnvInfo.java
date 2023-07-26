package com.ksyun.trade.bo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ksyun.trade.constant.Env;
import lombok.Data;

/**
 * @author ksc
 */
@Data
public class EnvInfo {

    private String name;

    private String ver;

    private String envStr;

    @JsonIgnore
    private Env env;

    @JsonIgnore
    private String envTagStr;

    @JsonIgnore
    public boolean isProd() {
        return env == Env.PROD;
    }

}
