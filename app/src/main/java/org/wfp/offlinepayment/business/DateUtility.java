package org.wfp.offlinepayment.business;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by Administrator on 15/5/2017.
 */

public class DateUtility {

    static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static Date toDateTime(String dateTime) {

        Date date = null;
        if (dateTime != null) {
            try {
                date = format.parse(dateTime);
            } catch (ParseException e) {
                date = null;
            }
        }
        return date;
    }

    public static String formatDateTime(Date dateTime) {
        return format.format(dateTime);
    }
}
