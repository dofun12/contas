package org.lemanoman.contas.utils;

import org.lemanoman.contas.dto.TimePeriod;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {

    public static final SimpleDateFormat YEAR_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");

    public static Date toDate(int dia,int mes,int ano){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH,dia);
        cal.set(Calendar.MONTH,mes-1);
        cal.set(Calendar.YEAR,ano);
        return cal.getTime();
    }

    public static TimePeriod toTimePeriod(String dateStr){
        try{
            Date date = YEAR_FORMATTER.parse(dateStr);
            return toTimePeriod(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  null;
    }

    public static TimePeriod toTimePeriod(Date date){
        try{
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            TimePeriod tp = new TimePeriod();
            tp.setAno(calendar.get(Calendar.YEAR));
            tp.setMes(calendar.get(Calendar.MONTH)+1);
            tp.setDia(calendar.get(Calendar.DAY_OF_MONTH));
            return tp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;
    }

}
