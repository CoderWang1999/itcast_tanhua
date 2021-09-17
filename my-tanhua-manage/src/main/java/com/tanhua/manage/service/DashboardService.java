package com.tanhua.manage.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.manage.enums.LogTypeEnum;
import com.tanhua.manage.mapper.LogMapper;
import com.tanhua.manage.mapper.UserInfoMapper;
import com.tanhua.manage.pojo.Log;
import com.tanhua.manage.pojo.UserInfo;
import com.tanhua.manage.utils.RateUtils;
import com.tanhua.manage.vo.Summary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class DashboardService {
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private LogMapper logMapper;

    public Summary querySummary() {
        //获取userInfo数据
        List<UserInfo> userInfoList = userInfoMapper.selectList(null);
        //创建返回值
        Summary summary = new Summary();
        //累计用户
        summary.setCumulativeUsers(userInfoList.size());
        //今日新增用户
        int todNew = 0;
        //昨日新增用户
        int yesNew = 0;
        Calendar cal5 = Calendar.getInstance();
        cal5.setTime(new Date());
        cal5.add(Calendar.DAY_OF_YEAR,-1);

        Date yesDate = cal5.getTime();
        for (UserInfo userInfo : userInfoList) {
            Date created = userInfo.getCreated();
            Date now = new Date();
            String today = new SimpleDateFormat("yyyy-MM-dd").format(now);
            String yestoday = new SimpleDateFormat("yyyy-MM-dd").format(yesDate);
            String before = new SimpleDateFormat("yyyy-MM-dd").format(created);
            if (today.equals(before)){
                todNew++;
            }
            if (yestoday.equals(before)){
                yesNew++;
            }
        }
        summary.setNewUsersToday(todNew);
        //30天活跃用户
        List<Log> logList = logMapper.selectList(null);
        //设置存储用户id的set集合用于去重
        Set<Integer> months = new HashSet<>();
        Set<Integer> weeks = new HashSet<>();
        Set<Integer> yesDays = new HashSet<>();
        Set<Integer> toDays = new HashSet<>();
        Set<Integer> beyesDays = new HashSet<>();
        int tdLoginNum = 0;
        int yesLoginNum = 0;

        Long dayTime = 0L;
        for (Log log : logList) {

            //获取活跃时间
            Date activeTime = log.getActiveTime();
            String activeTimeStr = new SimpleDateFormat("yyyy-MM-dd").format(activeTime);
           //30天
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_YEAR,-30);
            //7天
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(new Date());
            cal2.add(Calendar.DAY_OF_YEAR,-7);

            //3天
            Calendar cal6 = Calendar.getInstance();
            cal6.setTime(new Date());
            cal6.add(Calendar.DAY_OF_YEAR,-3);
            //2天
            Calendar cal3 = Calendar.getInstance();
            cal3.setTime(new Date());
            cal3.add(Calendar.DAY_OF_YEAR,-2);
            //1天
            Calendar cal4 = Calendar.getInstance();
            cal4.setTime(new Date());
            cal4.add(Calendar.DAY_OF_YEAR,-1);

            Date mDate = cal.getTime();
            Date wDate = cal2.getTime();
            Date bdDate = cal3.getTime();
            Date dDate = cal4.getTime();
            Date byDate = cal6.getTime();

            try {
                //30天内活跃
                if(new SimpleDateFormat("yyyy-MM-dd").parse(activeTimeStr).getTime()>mDate.getTime()){
                    months.add(log.getUserId());
                }
                //7天活跃
                if(new SimpleDateFormat("yyyy-MM-dd").parse(activeTimeStr).getTime()>wDate.getTime()){
                    weeks.add(log.getUserId());
                    //7天总使用时长
                    //执行了登录方法
                    long Time = 0L;
                    if (LogTypeEnum.LOGIN.getValue().equals(log.getMethod())){
                        //获取登录时间
                        Date logTime = log.getActiveTime();
                        //创建新时间记录登录时间用于比较
                        Date tempTime = logTime;
                        String logTimeStr = new SimpleDateFormat("yyyy-MM-dd").format(logTime);
                        Integer userId = log.getUserId();
                        //获取此用户操作的所有方法
                        QueryWrapper<Log> queryWrapper = new QueryWrapper<>();
                        queryWrapper.eq("user_id",userId);
                        List<Log> logList2 = logMapper.selectList(queryWrapper);
                        for (Log log2 : logList2) {
                            Date activeTime2 = log2.getActiveTime();
                            String activeTime2Str = new SimpleDateFormat("yyyy-MM-dd").format(activeTime2);
                          //此用户今天操作的方法
                            if (logTimeStr.equals(activeTime2Str)){
                             tempTime =   tempTime.compareTo(activeTime2) > 0 ? tempTime : activeTime2;
                            }
                        }
                         Time = tempTime.getTime() - logTime.getTime();
                    }
                        dayTime+=Time;
                }
                //今日活跃
                Date now = new Date();
                String today = new SimpleDateFormat("yyyy-MM-dd").format(now);
                String after = new SimpleDateFormat("yyyy-MM-dd").format(activeTime);
                if (today.equals(after)){
                    toDays.add(log.getUserId());
                    //今日登录次数
                    if (LogTypeEnum.LOGIN.getValue().equals(log.getMethod())){
                            tdLoginNum++;
                    }
                }
                //昨日活跃
                if(new SimpleDateFormat("yyyy-MM-dd").parse(activeTimeStr).getTime()>bdDate.getTime()
                &&new SimpleDateFormat("yyyy-MM-dd").parse(activeTimeStr).getTime()<dDate.getTime()){
                    yesDays.add(log.getUserId());
                    //昨日登录次数
                    if (LogTypeEnum.LOGIN.getValue().equals(log.getMethod())){
                        yesLoginNum++;
                    }
                }
                //前天活跃
                if(new SimpleDateFormat("yyyy-MM-dd").parse(activeTimeStr).getTime()>byDate.getTime()
                        &&new SimpleDateFormat("yyyy-MM-dd").parse(activeTimeStr).getTime()<bdDate.getTime()){
                    beyesDays.add(log.getUserId());

                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        summary.setActivePassMonth(months.size());//30天活跃
        summary.setActivePassWeek(weeks.size());//7天
        summary.setActiveUsersToday(toDays.size());//今日活跃
        summary.setActiveUsersYesterday(yesDays.size());//昨日活跃
        summary.setLoginTimesToday(tdLoginNum);//今日登录次数
        summary.setUseTimePassWeek(Integer.parseInt(dayTime.toString())/(7*1000));

        //计算涨跌率
        summary.setNewUsersTodayRate(RateUtils.computeRate(todNew, yesNew));//今日新增用户涨跌率
        summary.setActiveUsersTodayRate(RateUtils.computeRate(toDays.size(),yesDays.size()));//今日活跃用户涨跌率
        summary.setLoginTimesTodayRate(RateUtils.computeRate(tdLoginNum,yesLoginNum));//今日登录次数涨跌率
        summary.setActiveUsersYesterdayRate(RateUtils.computeRate(yesDays.size(),beyesDays.size()));//昨日活跃用户涨跌率
        return summary;
    }

}
