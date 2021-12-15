package com.github.spiotwo.AvgFlightTime;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

@SpringBootApplication
public class FlightTime {
    public static String msToHHmm(long ms)
    {
        long minute = (ms / (1000 * 60)) % 60;
        long hour = (ms / (1000 * 60 * 60)) % 24;
        return String.format("%02d:%02d", hour, minute);
    }

	public static void main(String[] args)
	{
        //SpringApplication.run(AvgFlightTimeApplication.class, args);

        Object obj = null;
        try {
            obj = new JSONParser().parse(new InputStreamReader(new BOMInputStream(new FileInputStream("src/main/resources/tickets.json"))));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        JSONObject jo = (JSONObject) obj;
        JSONArray tickets = (JSONArray) jo.get("tickets");
        SimpleDateFormat format = new SimpleDateFormat("dd:MM:yy:HH:mm");
        long TotalFlightTime = 0;
        double[] FlightTimeArr = new double[tickets.size()];
        int i = 0;

        for (Object ticket : tickets) {
            JSONObject jsonTicket = (JSONObject) ticket;

            String DepartureDate = (String) jsonTicket.get("departure_date");
            DepartureDate = DepartureDate.replaceAll("\\.",":");
            String DepartureTime = (String) jsonTicket.get("departure_time");
            String time1 = (DepartureDate + ":" + DepartureTime);
            Date date1 = null;
            try {
                date1 = format.parse(time1);
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
            //System.out.println(time1);

            String ArrivalDate = (String) jsonTicket.get("arrival_date");
            ArrivalDate = ArrivalDate.replaceAll("\\.",":");
            String ArrivalTime = (String) jsonTicket.get("arrival_time");
            String time2 = (ArrivalDate + ":" + ArrivalTime);
            Date date2 = null;
            try {
                date2 = format.parse(time2);
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
            //System.out.println(time2);

            long FlightTime = date2.getTime() - date1.getTime();
            FlightTimeArr[i] = FlightTime;
            i++;
            TotalFlightTime += FlightTime;
            //System.out.println(FlightTime);
            //System.out.println(msToHHmm(FlightTime));
        }

        Percentile FlightTimePercentile = new Percentile();
        System.out.println("Среднее время полёта = " + msToHHmm(TotalFlightTime/ tickets.size()));
        System.out.println("90-й процентиль времени полета = " + msToHHmm((long) FlightTimePercentile.evaluate(FlightTimeArr, 90)));
    }

}
