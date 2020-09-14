package com.hg.lib.file.util;

import java.io.File;
import java.util.Comparator;

/**
 *
 */
public class FileComparator implements Comparator<File> {
    @Override
    public int compare(File f1, File f2) {
        if(f1 == f2) {
            return 0;
        }
        if(f1.isDirectory() && f2.isFile()) {
            // 显示上面文件的目录
            return -1;
        }
        if(f1.isFile() && f2.isDirectory()) {
            // 显示目录下的文件
            return 1;
        }
        // 将目录按字母顺序排序
        return f1.getName().compareToIgnoreCase(f2.getName());
    }
}
