package com.hg.lib.treeview.base.util;



import com.hg.lib.treeview.TreeBean;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryUtils {
    public static List<TreeBean> fuzzyQuery(String name, List<TreeBean> lists) {
        // 大小写不敏感的时候，多加一个条件
        List<TreeBean> fuzzyQuery = new ArrayList<>();
        Pattern pattern = Pattern.compile(name, Pattern.CASE_INSENSITIVE);
        for (int i = 0; i < lists.size(); i++) {
            Matcher matcher = pattern.matcher((lists.get(i)).getName());
            if ( matcher.find() ) {
                fuzzyQuery.add(lists.get(i));
            }
        }
        return fuzzyQuery;
    }
}
