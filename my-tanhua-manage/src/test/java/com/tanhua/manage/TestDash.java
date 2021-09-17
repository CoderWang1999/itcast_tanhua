package com.tanhua.manage;

import com.tanhua.manage.service.DashboardService;
import com.tanhua.manage.utils.RateUtils;
import com.tanhua.manage.vo.Summary;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


@SpringBootTest(classes = ManagerApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class TestDash {

    @Autowired
    private DashboardService dashboardService;

    @Test
    public void testQuery(){
        Summary summary = dashboardService.querySummary();
        System.out.println(summary);
    }

    @Test
    public void TestRate(){
        Integer res = RateUtils.computeRate(30, 20);
        System.out.println(res);
    }


    @Test
    public void TestTime(){

        String beginTime = "2018-07-28 14:42:32";
        String endTime = "2018-07-29 12:26:32";

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date date1 = format.parse(beginTime);
            Date date2 = format.parse(endTime);

            int compareTo = date1.compareTo(date2);

            System.out.println(compareTo);

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}
