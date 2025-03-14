/*
 * Copyright (C) 2019 The Turms Project
 * https://github.com/turms-im/turms
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.turms.server.common.util;

import im.turms.server.common.constant.TimeZoneConstant;
import io.netty.util.concurrent.FastThreadLocal;

import javax.annotation.Nullable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author James Chen
 */
public final class DateUtil {

    private static final FastThreadLocal<Calendar> CALENDAR_THREAD_LOCAL = new FastThreadLocal<>() {
        @Override
        protected Calendar initialValue() {
            return new GregorianCalendar(TimeZoneConstant.ZONE);
        }
    };

    // "1970-01-01 00:00:00.000"
    public static final int DATE_TIME_LENGTH = 23;
    private static final long HOURS_IN_MILLIS = 60 * 60 * 1000L;
    private static final int MAX_TWO_DIGITS_CACHE_NUMBER = 59;
    private static final int MAX_THREE_DIGITS_CACHE_NUMBER = 100;

    private static final byte[][] TWO_DIGITS = new byte[MAX_TWO_DIGITS_CACHE_NUMBER + 1][];
    private static final byte[][] THREE_DIGITS = new byte[MAX_THREE_DIGITS_CACHE_NUMBER + 1][];

    static {
        byte[] oneDigitZero = {'0'};
        byte[] twoDigitsZero = {'0', '0'};
        int i;
        for (i = 0; i <= MAX_TWO_DIGITS_CACHE_NUMBER; i++) {
            byte[] bytes = Formatter.toCharBytes(i);
            if (bytes.length == 1) {
                bytes = ArrayUtil.concat(oneDigitZero, bytes);
            }
            TWO_DIGITS[i] = bytes;
        }
        for (i = 0; i <= MAX_THREE_DIGITS_CACHE_NUMBER; i++) {
            byte[] bytes = Formatter.toCharBytes(i);
            if (bytes.length == 1) {
                bytes = ArrayUtil.concat(twoDigitsZero, bytes);
            } else if (bytes.length == 2) {
                bytes = ArrayUtil.concat(oneDigitZero, bytes);
            }
            THREE_DIGITS[i] = bytes;
        }
    }

    private DateUtil() {
    }

    public static Date addHours(long date, int hours) {
        return new Date(date + hours * HOURS_IN_MILLIS);
    }

    public static String toStr(long timeInMillis) {
        byte[] bytes = toBytes(timeInMillis);
        return StringUtil.newString(bytes, StringUtil.LATIN1);
    }

    public static byte[] toBytes(long timeInMillis) {
        Calendar calendar = CALENDAR_THREAD_LOCAL.get();
        calendar.setTimeInMillis(timeInMillis);
        byte[] year = Formatter.toCharBytes(calendar.get(Calendar.YEAR));
        byte[] month = twoDigitBytes(calendar.get(Calendar.MONTH) + 1);
        byte[] dayOfMonth = twoDigitBytes(calendar.get(Calendar.DAY_OF_MONTH));
        byte[] hourOfDay = twoDigitBytes(calendar.get(Calendar.HOUR_OF_DAY));
        byte[] minute = twoDigitBytes(calendar.get(Calendar.MINUTE));
        byte[] second = twoDigitBytes(calendar.get(Calendar.SECOND));
        byte[] millis = threeDigitBytes(calendar.get(Calendar.MILLISECOND));
        return new byte[]{
                year[0],
                year[1],
                year[2],
                year[3],
                '-',
                month[0],
                month[1],
                '-',
                dayOfMonth[0],
                dayOfMonth[1],
                ' ',
                hourOfDay[0],
                hourOfDay[1],
                ':',
                minute[0],
                minute[1],
                ':',
                second[0],
                second[1],
                '.',
                millis[0],
                millis[1],
                millis[2]
        };
    }

    public static Date max(@Nullable Date date1, @Nullable Date date2) {
        if (date1 == null) {
            if (date2 != null) {
                return date2;
            }
        } else if (date2 != null && date1.before(date2)) {
            return date2;
        }
        return date1;
    }

    public static Date min(@Nullable Date date1, @Nullable Date date2) {
        if (date1 == null) {
            if (date2 != null) {
                return date2;
            }
        } else if (date2 != null && date1.after(date2)) {
            return date2;
        }
        return date1;
    }

    public static boolean isAfterOrSame(@Nullable Date d1, Date d2) {
        return d1 != null && !d1.before(d2);
    }

    private static byte[] twoDigitBytes(int i) {
        if (i <= MAX_TWO_DIGITS_CACHE_NUMBER) {
            return TWO_DIGITS[i];
        }
        return Formatter.toCharBytes(i);
    }

    private static byte[] threeDigitBytes(int i) {
        if (i <= MAX_THREE_DIGITS_CACHE_NUMBER) {
            return THREE_DIGITS[i];
        }
        return Formatter.toCharBytes(i);
    }

}
