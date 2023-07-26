package com.ksyun.trade.util.date;

/**
 * 计时器
 * <p>
 * 计算某个过程花费的时间，精确到毫秒
 *
 * @author yangpengfei@kingsoft.com
 */
public class TimeInterval {

    private long time;

    /**
     * @return 开始计时并返回当前时间
     */
    public long start() {
        time = System.currentTimeMillis();
        return time;
    }

    /**
     * @return 重新计时并返回从开始到当前的持续时间
     */
    public long intervalRestart() {
        long now = System.currentTimeMillis();
        long d = now - time;
        time = now;
        return d;
    }

    /**
     * 重新开始计算时间（重置开始时间）
     *
     * @return this
     * @since 3.0.1
     */
    public TimeInterval restart() {
        time = System.currentTimeMillis();
        return this;
    }

    //----------------------- 输出函数 -----------------------

    /**
     * 从开始到当前的间隔时间（毫秒数）<br>
     * 如果使用纳秒计时，返回纳秒差，否则返回毫秒差
     *
     * @return 从开始到当前的间隔时间（毫秒数）
     */
    public long interval() {
        return System.currentTimeMillis() - time;
    }

    /**
     * 从开始到当前的间隔时间（毫秒数），返回XX天XX小时XX分XX秒XX毫秒
     *
     * @return 从开始到当前的间隔时间（毫秒数）
     */
    public String intervalPretty() {
        return new BetweenFormater(intervalMs(), BetweenFormater.Level.MILLSECOND).format();
    }

    /**
     * 从开始到当前的间隔时间（毫秒数）
     *
     * @return 从开始到当前的间隔时间（毫秒数）
     */
    public long intervalMs() {
        return interval();
    }

    /**
     * 从开始到当前的间隔秒数，取绝对值
     *
     * @return 从开始到当前的间隔秒数，取绝对值
     */
    public long intervalSecond() {
        return intervalMs() / DateUnit.SECOND.getMillis();
    }

    /**
     * 从开始到当前的间隔分钟数，取绝对值
     *
     * @return 从开始到当前的间隔分钟数，取绝对值
     */
    public long intervalMinute() {
        return intervalMs() / DateUnit.MINUTE.getMillis();
    }

    /**
     * 从开始到当前的间隔小时数，取绝对值
     *
     * @return 从开始到当前的间隔小时数，取绝对值
     */
    public long intervalHour() {
        return intervalMs() / DateUnit.HOUR.getMillis();
    }

    /**
     * 从开始到当前的间隔天数，取绝对值
     *
     * @return 从开始到当前的间隔天数，取绝对值
     */
    public long intervalDay() {
        return intervalMs() / DateUnit.DAY.getMillis();
    }

    /**
     * 从开始到当前的间隔周数，取绝对值
     *
     * @return 从开始到当前的间隔周数，取绝对值
     */
    public long intervalWeek() {
        return intervalMs() / DateUnit.WEEK.getMillis();
    }

}
