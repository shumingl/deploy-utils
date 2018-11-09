package com.iwill.deploy.common.utils;

import com.iwill.deploy.common.exception.BusinessException;
import org.joda.time.DateTime;

import java.lang.reflect.Method;

public class DateTimeUtil {

    /**
     * 判断某时间是否在时间段之间
     *
     * @param timeTarget 时间 HH:mm:ss
     * @param timeRange  左边界-右边界 HH:mm:ss-HH:mm:ss
     * @return
     */
    public static boolean isBetween(String timeTarget, String timeRange) {

        if (timeRange == null || timeRange.trim().length() == 0 || !timeRange.contains("-"))
            throw new IllegalArgumentException("时间区间为空或格式错误");

        String times[] = timeRange.split("-");
        long timeSeconds = getSeconds(timeTarget);
        return getSeconds(times[0]) <= timeSeconds && timeSeconds <= getSeconds(times[1]);
    }

    /**
     * 判断某时间是否在时间段之间
     *
     * @param timeTarget 时间 HH:mm:ss
     * @param timeLeft   左边界 HH:mm:ss
     * @param timeRight  右边界 HH:mm:ss
     * @return
     */
    public static boolean isBetween(String timeTarget, String timeLeft, String timeRight) {
        long timeSeconds = getSeconds(timeTarget);
        return getSeconds(timeLeft) <= timeSeconds && timeSeconds <= getSeconds(timeRight);
    }

    /**
     * 计算两个时间之间的秒差
     *
     * @param timeLeft  左边界 HH:mm:ss
     * @param timeRight 右边界 HH:mm:ss
     * @return 秒差
     */
    public static long getTimeDiff(String timeLeft, String timeRight) {
        return getSeconds(timeRight) - getSeconds(timeLeft);
    }

    /**
     * 计算时间举例零点的秒数
     *
     * @param timeString HH:mm:ss
     * @return 秒数
     */
    public static long getSeconds(String timeString) {
        if (timeString == null || timeString.trim().length() == 0) {
            throw new IllegalArgumentException("时间为空或格式错误");
        }
        StringBuilder builder = new StringBuilder(timeString.trim());
        long hour = Long.parseLong(builder.substring(0, 2));
        long minute = Long.parseLong(builder.substring(3, 5));
        long second = Long.parseLong(builder.substring(6, 8));
        return second + minute * 60 + hour * 60 * 60;
    }

    /**
     * yyyyMMdd格式的整数值
     *
     * @param dateTime DateTime
     * @return such as : 20181031
     */
    public static int getPeriod(DateTime dateTime) {
        return Integer.parseInt(dateTime.toString("yyyyMMdd"));
    }

    public static DateTime add(DateTime baseDateTime, DateTimeInterval interval, int count) {
        if (count == 0)
            return baseDateTime;
        String action = count > 0 ? "plus" : "minus";
        String methodName = action + interval.name();
        try {
            Method method = DateTime.class.getMethod(methodName, int.class);
            return (DateTime) method.invoke(baseDateTime, count);
        } catch (Exception e) {
            throw new BusinessException("日期计算失败", e);
        }
    }

    public enum DateTimeInterval {
        Years, Months, Days, Weeks, Hours, Minutes, Seconds, Millis
    }

}
