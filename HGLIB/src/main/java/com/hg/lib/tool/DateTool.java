package com.hg.lib.tool;


import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * 时间处理工具类
 */
@SuppressLint("SimpleDateFormat")
public class DateTool {
    private static SimpleDateFormat msFormat = new SimpleDateFormat("mm:ss");

    /**
     * 获取现在时间
     *
     * @return返回字符串格式 yyyyMMddHHmmss
     */
    public static String getStrAllDate() {
        SimpleDateFormat formatter = new SimpleDateFormat(Objects.requireNonNull(getDateType("4")), Locale.getDefault());
        return formatter.format(new Date());
    }

    /**
     * 获取系统时间 格式为 yyyy-MM-dd HH:mm
     */
    public static String getSysData(String type) {
        return new SimpleDateFormat(Objects.requireNonNull(getDateType(type)), Locale.CHINA).format(new Date(System.currentTimeMillis()));
    }

    /**
     * 获取现在时间 返回long格式
     */
    public static Long getLongData(String type) {
        long datas = 0;
        String str = getSysData(type);
        SimpleDateFormat format = new SimpleDateFormat(Objects.requireNonNull(getDateType(type)), Locale.getDefault());
        try {
            Date date = format.parse(str);
            datas = Objects.requireNonNull(date).getTime();
        } catch (Exception ignored) {
        }
        return datas;
    }

    public static Date getData(long mseconds) {
        return new Date(mseconds * 1000);
    }

    /**
     * 获取当前时间 yyyy-MM-dd 并转为String返回
     *
     * @return
     */
    public static String getTodayShort() {
        SimpleDateFormat formatter = new SimpleDateFormat(Objects.requireNonNull(getDateType("1")), Locale.getDefault());
        return formatter.format(new Date());
    }

    /**
     * long转为String
     *
     * @param time
     * @return
     */
    public static String longToStr(long time, String type) {
        SimpleDateFormat format = new SimpleDateFormat(Objects.requireNonNull(getDateType(type)), Locale.getDefault());
        return format.format(new Date(time));
    }

    /**
     * 将时间格式为String转为long
     *
     * @param str
     * @return
     */
    public static long strToLong(String str, String type) {
        try {
            return Objects.requireNonNull(new SimpleDateFormat(Objects.requireNonNull(getDateType(type)), Locale.CHINA).parse(str)).getTime();
        } catch (Throwable ignored) {
            return 0;
        }
    }


    private static String getHourAndMin(long time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return format.format(new Date(time));
    }

    public static String getChatTime(long timesamp) {
        String result;
        SimpleDateFormat format = new SimpleDateFormat("dd", Locale.getDefault());
        Date today = new Date(System.currentTimeMillis());
        Date otherDay = new Date(timesamp);
        int temp = Integer.parseInt(format.format(today)) - Integer.parseInt(format.format(otherDay));

        switch (temp) {
            case 0:
                result = "今天 " + getHourAndMin(timesamp);
                break;
            case 1:
                result = "昨天 " + getHourAndMin(timesamp);
                break;
            case 2:
                result = "前天 " + getHourAndMin(timesamp);
                break;
            default:
                result = longToStr(timesamp, "2");
                break;
        }
        return result;
    }

    /**
     * 获取星期
     */
    public static String getWeek() {
        long time = System.currentTimeMillis();
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("EEEE", Locale.getDefault());
        return format.format(date);
    }

    /**
     * 获取昨天日期
     *
     * @return
     */
    public static String getYesterDay() {
        Date d = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24);
        SimpleDateFormat sp = new SimpleDateFormat("yyyy-MM-dd");
        return sp.format(d);
    }

    private static String getDateType(String type) {
        switch (type) {
            case "1":
                return "yyyy-MM-dd";
            case "2":
                return "yyyy-MM-dd HH:mm";
            case "3":
                return "yyyy-MM-dd HH:mm:ss";
            case "4":
                return "yyyyMMddHHmmssSSS";
        }
        return null;
    }

    /**
     * 当前时间往前推N天
     *
     * @param n
     * @return
     */

    public static String DatePushAhead(int n) {
        try {
            Calendar cl = Calendar.getInstance();
            cl.setTime(new Date());
            cl.add(Calendar.DAY_OF_YEAR, n);
            Date xx = cl.getTime();
            SimpleDateFormat formatter = new SimpleDateFormat(Objects.requireNonNull(getDateType("3")), Locale.getDefault());
            return formatter.format(xx);
        } catch (Throwable e) {
            return "";
        }
    }

    /**
     * 当前时间往前推N天
     *
     * @param n
     * @return
     */

    public static String DatePushAhead(String dateType, int n) {
        try {
            Calendar cl = Calendar.getInstance();
            cl.setTime(new Date());
            cl.add(Calendar.DAY_OF_YEAR, n);
            Date xx = cl.getTime();
            SimpleDateFormat formatter = new SimpleDateFormat(Objects.requireNonNull(getDateType(dateType)), Locale.getDefault());
            return formatter.format(xx);
        } catch (Throwable e) {
            return "";
        }
    }

    /**
     * MS turn every minute
     *
     * @param duration Millisecond
     * @return Every minute
     */
    public static String timeParse(long duration) {
        String time = "";
        if (duration > 1000) {
            time = timeParseMinute(duration);
        } else {
            long minute = duration / 60000;
            long seconds = duration % 60000;
            long second = Math.round((float) seconds / 1000);
            if (minute < 10) {
                time += "0";
            }
            time += minute + ":";
            if (second < 10) {
                time += "0";
            }
            time += second;
        }
        return time;
    }

    /**
     * MS turn every minute
     *
     * @param duration Millisecond
     * @return Every minute
     */
    public static String timeParseMinute(long duration) {
        try {
            return msFormat.format(duration);
        } catch (Exception e) {
            e.printStackTrace();
            return "0:00";
        }
    }

    /**
     * 判断两个时间戳相差多少秒
     *
     * @param d
     * @return
     */
    public static int dateDiffer(long d) {
        try {
            long l1 = Long.parseLong(String.valueOf(System.currentTimeMillis()).substring(0, 10));
            long interval = l1 - d;
            return (int) Math.abs(interval);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 计算两个时间间隔
     *
     * @param sTime
     * @param eTime
     * @return
     */
    public static String cdTime(long sTime, long eTime) {
        long diff = eTime - sTime;
        return diff > 1000 ? diff / 1000 + "秒" : diff + "毫秒";
    }

    public static String getTimeFormatByS(int time) {
        String hhS;
        String fenS;
        String miaoS;
        int hh = time / 3600;
        int fen = (time - hh * 3600) / 60;
        int miao = time % 60;
        if (hh < 10) {
            hhS = "" + hh;
        } else {
            hhS = "" + hh;
        }
        if (fen < 10) {
            fenS = "" + fen;
        } else {
            fenS = "" + fen;
        }
        if (miao < 10) {
            miaoS = "" + miao;
        } else {
            miaoS = "" + miao;
        }
        if (hh == 0) {
            if (fen == 0) {
                return miaoS + "秒";
            } else {
                return fenS + "分" + miaoS + "秒";
            }
        } else {
            return hhS + "小时" + fenS + "分钟" + miaoS + "秒";
        }
    }
}
