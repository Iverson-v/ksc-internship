package com.ksyun.train;

import com.ksyun.train.util.ListUtil;
import org.apache.commons.collections.CollectionUtils;
import java.util.List;

public class App {
    public static void main(String[] args) {
        List<String> emptyList= ListUtil.getEmptyList();
        CollectionUtils.size(emptyList);
        System.out.println("OK");
    }
}
