/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.farm_erp.utilities;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class Quarter {

    public String quarter;
    public String startMonth;
    public String endMonth;
    public LocalDate startDate;
    public LocalDate endDate;

    public Quarter(LocalDate date) {
        LocalDate loop = null;

        loop = LocalDate.of(date.getYear(), Month.JANUARY, 1);
        
        List<Data> data = new ArrayList<>();

        LocalDate stt = loop;
        int i = 1;
        while (i < 5) {
            LocalDate pp = stt.plusMonths(3).minusDays(1);
            data.add(new Data(stt, pp, "Q" + i));
            stt = pp.plusDays(1);
            i++;
        }

        for (Data d : data) {
            if ((date.isEqual(d.startDate) || date.isAfter(d.startDate))
                    && (date.isEqual(d.endDate) || date.isBefore(d.endDate))) {
                this.quarter = d.name;
                this.startDate = d.startDate;
                this.endDate = d.endDate;
            }
        }

    }

}

class Data {
    public LocalDate startDate;
    public LocalDate endDate;
    public String name;

    public Data() {
    }

    public Data(LocalDate startDate, LocalDate endDate, String name) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.name = name;
    }

}