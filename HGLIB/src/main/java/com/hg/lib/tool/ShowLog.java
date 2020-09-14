package com.hg.lib.tool;

/**
 * 日志打印工具类
 */
public final class ShowLog {

    private ShowLog() {
    }

    public static void sys(String log) {
        System.out.println(log);
    }

    public static void sys(int position) {
        System.out.println(position);
    }
}
