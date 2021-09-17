package com.tanhua.manage.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.dubbo.server.api.QuanZiApi;
import com.tanhua.dubbo.server.pojo.Publish;
import com.tanhua.dubbo.server.vo.PageInfo;
import com.tanhua.manage.pojo.User;
import com.tanhua.manage.pojo.UserInfo;
import com.tanhua.manage.util.UserThreadLocal;
import com.tanhua.manage.vo.PageResult;
import com.tanhua.manage.vo.PublishVo;
import com.tanhua.manage.vo.TotalsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;

/**
 * @author ZJWzxy 陈亮
 */
@Service
public class MessageService {

    @Reference(version = "1.0.0")
    private QuanZiApi quanZiApi;


    @Autowired
    private UserInfoService userInfoService;

    /**
     * 消息管理--消息翻页
     *
     * @param pagesize  页大小
     * @param page      页码
     * @param sortProp  排序字段
     * @param sortOrder ascending 升序 descending 降序
     * @param publishId 消息id
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @param state     审核状态
     * @return 分页结果
     */
    public PageResult queryMessageList(Integer page, Integer pagesize, String sortProp, String sortOrder, Integer publishId, String startDate, String endDate, String state) {
        //校验
        User user = UserThreadLocal.get();
        //设置分页信息---基本
        PageResult pageResult = new PageResult();
        pageResult.setPage(page);
        pageResult.setPagesize(pagesize);
        //调用QuanZiApi查询所有人的动态
        PageInfo<Publish> pageInfo = this.quanZiApi.queryAllPublish(page, pagesize);
        List<Publish> publishList = pageInfo.getRecords();
        //判断是否为空
        if (null==publishList){
            //空
            return null;
        }
        //记录总条数
        Long countPublish = this.quanZiApi.queryCountPublish();
        pageResult.setCounts(Integer.valueOf(countPublish.toString()));
        //填充总共的页数
        pageResult.setPages(Integer.valueOf(countPublish.toString()) / pagesize + 1);



        //填充total信息
        //创建一个集合用来保存所有的total信息
        List<TotalsVo> totalsVoList = new ArrayList<>();
        //设置审核的相关信息
        //全部
        TotalsVo totalsVo = new TotalsVo();
        totalsVo.setTitle("全部");
        totalsVo.setCode("all");
        Long count = this.quanZiApi.queryCountPublish();
        totalsVo.setValue(Integer.valueOf(count.toString()));
        //添加到集合中
        totalsVoList.add(totalsVo);
        //待审核
        TotalsVo totalsVo1 = new TotalsVo();
        totalsVo1.setTitle("待审核");
        totalsVo1.setCode("3");
        Long count1 = this.quanZiApi.queryCountWait(3);
        totalsVo1.setValue(Integer.valueOf(count1.toString()));
        //添加到集合
        totalsVoList.add(totalsVo1);
        //已通过
        TotalsVo totalsVo2 = new TotalsVo();
        totalsVo2.setTitle("已通过");
        totalsVo2.setCode("4");
        Long count2 = this.quanZiApi.queryCountWait(5);
        totalsVo2.setValue(Integer.valueOf(count2.toString()));
        //添加到集合
        totalsVoList.add(totalsVo2);
        //已驳回
        TotalsVo totalsVo3 = new TotalsVo();
        totalsVo3.setTitle("已驳回");
        totalsVo3.setCode("5");
        Long count3 = this.quanZiApi.queryCountWait(4);
        totalsVo3.setValue(Integer.valueOf(count3.toString()));
        //添加到集合
        totalsVoList.add(totalsVo3);


        //添加到PageResult中
        pageResult.setTotals(totalsVoList);


        //填充publishVo
        //创建一个集合用来保存publish信息
        List<PublishVo> publishVoList=new ArrayList<>();
        //得到所有的用户信息
        QueryWrapper<UserInfo> queryWrapper=new QueryWrapper<>();
        List<UserInfo> userInfoList = this.userInfoService.queryUserInfoList(queryWrapper);
        for (Publish publish : publishList) {
            for (UserInfo userInfo : userInfoList) {
                //匹配动态发布者的id
                if (userInfo.getUserId().longValue()==publish.getUserId().longValue()){
                    PublishVo publishVo=new PublishVo();
                    //得到动态编号
                    publishVo.setId(publish.getId().toHexString());
                    //添加昵称
                    publishVo.setNickname(userInfo.getNickName());
                    //添加作者id
                    publishVo.setUserId(Integer.valueOf(publish.getUserId().toString()));
                    //添加头像
                    publishVo.setUserLogo(userInfo.getLogo());
                    //得到发布日期
                    publishVo.setCreateDate(publish.getCreated());
                    //得到发布内容
                    publishVo.setText(publish.getText());
                    //得到审核状态
                    publishVo.setState(publish.getState());
                    //得到举报数
                    publishVo.setReportCount(0);
                    //得到点赞数
                    Long count4 = this.quanZiApi.queryCommentCount(publish.getId().toHexString(), 1);
                    publishVo.setLikeCount(Integer.valueOf(count4.toString()));
                    //得到评论数
                    Long count5 = this.quanZiApi.queryCommentCount(publish.getId().toHexString(), 2);
                    publishVo.setCommentCount(Integer.valueOf(count5.toString()));
                    //设置转发数
                    publishVo.setForwardingCount(0);
                    //得到动态图片
                    List<String> medias = publish.getMedias();
                    publishVo.setMedias(medias);
                    //添加到动态信息
                    publishVoList.add(publishVo);

                }
            }
        }

        //添加到pageResult中
        pageResult.setItems(publishVoList);
        return pageResult;
    }
}
