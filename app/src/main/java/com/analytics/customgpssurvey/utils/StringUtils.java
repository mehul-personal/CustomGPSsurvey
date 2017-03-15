package com.analytics.customgpssurvey.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;

public abstract class StringUtils {

    // Use StringUtils.EMPTY for any comparison or assignment of empty string.
    public static final String EMPTY = "";
    // Use StringUtils.SPACE for any comparison or assignment of space string.
	public static final String SPACE = " ";

    public static final String FONT_NAME =  "helvetica_heavy.ttf";

    /**
     * This is format of time like 18:55
     */
    public static final String PATTERN_HH_mm = "HH:mm";
    public static final String PATTERN_EEEE_dd_MMM_yyyy_HH_mm = "EEEE dd MMMM yyyy HH:mm";
    public static final String PATTERN_EEEE_dd_MMM_yyyy = "EEEE dd MMMM yyyy";
    public static final String PATTERN_yyyyMMddHHmmss = "yyyyMMddHHmmss";

    /*
     * String Messages to be used in the app
     */

    // Use StringUtils.CRASH_TITLE for showing this predefined crash title. For example in CrashReportActivity its used for alert Dialog title.
    public static final String CRASH_TITLE = "App Crashed";
    // Use StringUtils.CRASH_MESSAGE for showing this predefined crash message. For example in CrashReportActivity its used for alert Dialog message.
    public static final String CRASH_MESSAGE = "Crash file is generated in sd card -> Demo";
    // Use StringUtils.NO_INTERNET_MESSAGE for showing the message to user about the internet connection.
    public static final String NO_INTERNET_MESSAGE = "No internet connection found. Please connect with mobile data or WiFi.";


    /**
     * Converts the given calendar to string format after addig the provided minutes as following
     *    Ex: Time 18:00, difference is -30 Output is 17:30
     * @param c    calendar object for the time
     * @param diference give time to manipulate with calender object
     * @return String result as 18:00
     */
    public static String getTime(Calendar c, int diference){
        Calendar newCal = Calendar.getInstance();
        newCal.setTime(c.getTime());
        newCal.add(Calendar.MINUTE, diference);
        SimpleDateFormat simpleDateFormat;
        simpleDateFormat = new SimpleDateFormat(PATTERN_HH_mm, Locale.getDefault());
        return simpleDateFormat.format(newCal.getTime());
    }

    /**
     * Converts the given calendar to yyyyMMddHHmmss format as following
     *    Ex: 20151208161830
     * @return String result as 20151208161830
     */
    public static String getDate_yyyyMMddHHmmss(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat;
        if(Locale.getDefault() == Locale.ENGLISH){
            simpleDateFormat = new SimpleDateFormat(PATTERN_yyyyMMddHHmmss, Locale.ENGLISH);
        }else{
            simpleDateFormat = new SimpleDateFormat(PATTERN_yyyyMMddHHmmss, new Locale("nl"));
        }
        return simpleDateFormat.format(c.getTime());
    }

    /**
     * This will return number in 2 digits i.e 9 will be 09 and 11 will be 11
     * @param number to convert in two digit
     * @return string value with two digit
     */
    public static String getInTwoDigits(int number){
        return String.format("%02d", number);
    }

    //TODO new datetime stamp implementations

    /**
     * This method will convert timeStamp to calendar object
     * @param date long timestamp
     * @return calendar object of the given date
     */
    public static Calendar getCalenderFromTimestamp(long date){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        return cal;
    }

    /**
     * This method will convert timeStamp to EEEE dd MMM yyyy HH mm format date object
     * @param date long timestamp
     * @return String formatted date EEEE_dd_MMM_yyyy_HH_mm i.e : Saturday 17 December 2015 17:48
     */
    public static String getEEEE_dd_MMM_yyyy_HH_mm(long date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(PATTERN_EEEE_dd_MMM_yyyy_HH_mm, Locale.ENGLISH);
        if(!Locale.getDefault().getLanguage().equalsIgnoreCase("en")) {
            simpleDateFormat = new SimpleDateFormat(PATTERN_EEEE_dd_MMM_yyyy_HH_mm, new Locale("nl", "Netherlands"));
        }
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        return simpleDateFormat.format(cal.getTime());
    }

    /**
     * This method will convert timeStamp to EEEE dd MMM yyyy format date object
     * @param date long timestamp
     * @return String formatted date EEEE_dd_MMM_yyyy i.e : Saturday 17 December 2015
     */
    public static String getEEEE_dd_MMM_yyyy(long date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(PATTERN_EEEE_dd_MMM_yyyy, Locale.ENGLISH);
        if(!Locale.getDefault().getLanguage().equalsIgnoreCase("en")) {
            simpleDateFormat = new SimpleDateFormat(PATTERN_EEEE_dd_MMM_yyyy, new Locale("nl", "Netherlands"));
        }
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        return simpleDateFormat.format(cal.getTime());
    }

    /**
     * This method will convert timeStamp to HH:mm format date object
     * @param timeInMilies long timestamp
     * @return String formatted date EEEE_dd_MMM_yyyy i.e : Saturday 17 December 2015
     */
    public static String getHH_mm(long timeInMilies) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(PATTERN_HH_mm, Locale.ENGLISH);
        if(!Locale.getDefault().getLanguage().equalsIgnoreCase("en")) {
            simpleDateFormat = new SimpleDateFormat(PATTERN_HH_mm, new Locale("nl", "Netherlands"));
        }
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeInMilies);
        return simpleDateFormat.format(cal.getTime());
    }

    /**
     * This method will prepare Calendar object from given milis as date in string format and Time is HH:mm format
     * @param tripDateInMilies String object containing trip start date in milies
     * @param time in string form and in HH:mm format
     * @return Calendar object
     */
    public static Calendar getEEEE_dd_MMM_yyyy_HH_mm(String tripDateInMilies, String time) {
        StringTokenizer st = new StringTokenizer(time, ":");
        Calendar cal = getCalenderFromTimestamp(Long.parseLong(tripDateInMilies));
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(st.nextToken()));
        cal.set(Calendar.MINUTE, Integer.parseInt(st.nextToken()));
        return cal;
    }

    /**
     * This method will convert given time to GMT time in millies
     * @param timeInMilies local time in millies
     * @return long value of GMT converted time in millies.
     */
    public static long getGMTTimeFromLocal(long timeInMilies) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(PATTERN_EEEE_dd_MMM_yyyy_HH_mm, Locale.ENGLISH);
        if(!Locale.getDefault().getLanguage().equalsIgnoreCase("en")) {
            simpleDateFormat = new SimpleDateFormat(PATTERN_EEEE_dd_MMM_yyyy_HH_mm, new Locale("nl", "Netherlands"));
        }
        Date localTime = new Date(timeInMilies);
        // Convert Local Time to GMT
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date gmtTime = new Date(simpleDateFormat.format(localTime));
        return gmtTime.getTime();

//        simpleDateFormat.setTimeZone(TimeZone.getDefault());
//        Date localDateTime = new Date(simpleDateFormat.format(localTime));
//        TimeZone gmtTimeZone = TimeZone.getTimeZone("GMT");
//        simpleDateFormat.setTimeZone(gmtTimeZone);
//        Date gmtDateTime = new Date(simpleDateFormat.format(localDateTime));
//        return gmtDateTime.getTime();
    }

    /**
     * This method will convert given GMT time to default time in millies
     * @param timeInMilies GMT time in millies
     * @return long value of default converted time in millies.
     */
    public static long getDefaultTimeFromGMT(long timeInMilies) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("gmt"));
        cal.setTimeInMillis(timeInMilies);
        Date fromGmt = new Date(cal.getTimeInMillis() + TimeZone.getDefault().getOffset(cal.getTimeInMillis()));
        return fromGmt.getTime();
    }

    /**
     * Converts the given yyyyMMddHHmmss format to Calender object
     *    Ex: 20151208161830
     * @return Calendar result of 20151208161830
     */
    public static Calendar getDate_yyyyMMddHHmmss(String dateTime){
        DateFormat df = new SimpleDateFormat(PATTERN_yyyyMMddHHmmss);
        Calendar cal  = Calendar.getInstance();
        try {
            cal.setTime(df.parse(dateTime));
        } catch (ParseException e) {
            e.printStackTrace();
            cal.set(Calendar.YEAR, Integer.parseInt(dateTime.substring(0, 2)));
            cal.set(Calendar.MONTH, Integer.parseInt(dateTime.substring(2, 4))-1);
            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateTime.substring(4, 6)));
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(dateTime.substring(6, 8)));
            cal.set(Calendar.MINUTE, Integer.parseInt(dateTime.substring(8, 10)));
            cal.set(Calendar.SECOND, Integer.parseInt(dateTime.substring(10, 12)));
            cal.set(Calendar.MILLISECOND, 0);
        }
        return cal;
    }
}
