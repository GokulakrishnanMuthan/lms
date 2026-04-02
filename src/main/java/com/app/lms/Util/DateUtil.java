package com.app.lms.Util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	
	public static Date addDays(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }
	
	
	public static String datetoString(Date date)
    {
		//System.out.println("date->"+date);
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");  
		String strDate = formatter.format(date);  
		//System.out.println("strDate->"+strDate);
        return strDate;
    }
	
}
