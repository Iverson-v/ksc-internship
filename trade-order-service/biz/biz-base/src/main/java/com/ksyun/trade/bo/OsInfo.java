package com.ksyun.trade.bo;

import lombok.Data;


/**
 * @author ksc
 */
@Data
public class OsInfo {

    private String name;

    private String ver;

    private String arch;

    private int availableProcessors;

}