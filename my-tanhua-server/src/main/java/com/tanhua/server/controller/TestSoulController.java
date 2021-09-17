package com.tanhua.server.controller;

import com.tanhua.server.pojo.Answers;
import com.tanhua.server.service.TestSoulService;
import com.tanhua.server.utils.UserThreadLocal;
import com.tanhua.server.vo.ConclusionVo;
import com.tanhua.server.vo.PaperListVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author ZJWzxy
 */
@SuppressWarnings("all")
@RestController
@RequestMapping("testSoul")
@Slf4j
public class TestSoulController {

    @Autowired
    private TestSoulService testSoulService;

    /**
     * 测灵魂---问卷列表
     *
     * @return 问卷
     */

    @GetMapping
    public ResponseEntity<List<PaperListVo>> paperList() {
        try {
            List<PaperListVo> list = this.testSoulService.queryPaperList();
            String msg = UserThreadLocal.get().getMobile() + "查询问卷列表成功";
            log.info(msg);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            String msg = UserThreadLocal.get().getMobile() + "查询问卷列表失败";
            log.info(msg, e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 测灵魂---查看报告
     *
     * @param reportId 报告id
     * @return 鉴定结果
     */
    @GetMapping("/report/{id}")
    public ResponseEntity<ConclusionVo> getReport(@PathVariable("id") String reportId) {
        try {
            //调用TestSoulService中查看报告的方法查看报告
            ConclusionVo conclusionVo = this.testSoulService.getReport(reportId);
            if (null != conclusionVo) {
                //查看报告成功
                String msg = UserThreadLocal.get().getMobile() + "查看报告成功";
                log.info(msg);
                return ResponseEntity.ok(conclusionVo);
            }
        } catch (Exception e) {
            //查看报告失败
            String msg = UserThreadLocal.get().getMobile() + "查看报告失败";
            log.info(msg, e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


    /**
     * 测灵魂----提交问卷
     *
     * @param map 参数--问题id和选项id
     * @return 报告id
     */
    @PostMapping
    public ResponseEntity<String> submitTestPaper(@RequestBody Map<String, List<Answers>> map) {
        try {
            //调用TestSoulService中的方法提交报告
            String reportId = this.testSoulService.submitTestPaper(map);
            if (null!=reportId){
                //提交报告成功
                String msg=UserThreadLocal.get().getMobile()+"提交问卷成功";
                log.info(msg);
                return ResponseEntity.ok(reportId);
            }
        } catch (Exception e) {
            //提交报告失败
            String msg=UserThreadLocal.get().getMobile()+"提交问卷失败";
            log.info(msg,e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
