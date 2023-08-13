package com.gg.myssm.util;

public class StringUtil {
    public static boolean isNoNull(String str){
        return str!=null&&!"".equals(str);
    }

    public static String fieldMontage(String... strings){
        if(strings == null)return "*";
        StringBuffer fieldStr = new StringBuffer(strings[0]);
        for (int i = 1; i < strings.length; i++)
            fieldStr.append(',' + strings[i]);
        return fieldStr.toString();
    }
}
