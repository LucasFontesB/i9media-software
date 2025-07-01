package com.i9media.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtils {

    public static String formatarDataParaBrasileiro(Date data) {
        if (data == null) return "";
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(data);
    }
    
    public static String formatarDataParaBrasileiroLD(LocalDate data) {
        if (data == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return data.format(formatter);
    }
}