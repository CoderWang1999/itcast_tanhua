package com.tanhua.manage.controller;

import com.tanhua.manage.service.MessageService;
import com.tanhua.manage.vo.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ZJWzxy  陈亮
 */
@RestController
@RequestMapping("management/manage/messages")
@Slf4j
public class MessageController {

    @Autowired
    private MessageService messageService;


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
    @GetMapping
    public ResponseEntity<PageResult> queryMessageList(@RequestParam(value = "pagesize", defaultValue = "10", required = false) Integer pagesize,
                                                       @RequestParam(value = "page", defaultValue = "1", required = false) Integer page,
                                                       @RequestParam("sortProp") String sortProp,
                                                       @RequestParam("sortOrder") String sortOrder,
                                                       @RequestParam(value = "id", required = false) Integer publishId,
                                                       @RequestParam(value = "sd", required = false) String startDate,
                                                       @RequestParam(value = "ed", required = false) String endDate,
                                                       @RequestParam(value = "state", defaultValue = "3") String state) {
        try {
            //调用MessageService中的查询方法得到所有的动态
            PageResult pageResult = this.messageService.queryMessageList(page, pagesize, sortProp, sortOrder, publishId, startDate, endDate, state);
            if (null!=pageResult){
                //查询成功
                String msg="查询所有动态成功";
                log.info(msg);
                return ResponseEntity.ok(pageResult);
            }
        } catch (Exception e) {
            //查询失败
            String msg="查询所有动态失败";
            log.info(msg,e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

    }

}
