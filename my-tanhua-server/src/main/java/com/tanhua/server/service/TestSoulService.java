package com.tanhua.server.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.server.enums.ConclusionEnum;
import com.tanhua.server.mapper.*;
import com.tanhua.server.pojo.*;
import com.tanhua.server.utils.UserThreadLocal;
import com.tanhua.server.vo.ConclusionVo;
import com.tanhua.server.vo.OptionsVo;
import com.tanhua.server.vo.PaperListVo;
import com.tanhua.server.vo.QuestionsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author ZJWzxy
 */
@Service
@SuppressWarnings("all")
public class TestSoulService {

    @Autowired
    private SoulReportMapper soulReportMapper;

    @Autowired
    private SoulPaperMapper soulPaperMapper;

    @Autowired
    private SoulPaperQuestionMapper soulPaperQuestionMapper;

    @Autowired
    private SoulQuestionMapper soulQuestionMapper;

    @Autowired
    private SoulOptionsMapper soulOptionsMapper;

    @Autowired
    private UserInfoService userInfoService;

    /**
     * 测灵魂----问卷列表
     *
     * @return 问卷
     */
    public List<PaperListVo> queryPaperList() {
        //校验
        User user = UserThreadLocal.get();
        //由于问卷分为三个级别且是逐一解锁的,所以中级和高级应该是被锁住的,而初级是不能被锁住,应该定义三个不同的返回结果用来返回响应的问卷
        //初级问卷
        PaperListVo paperListVo1 = new PaperListVo();
        //中级问卷
        PaperListVo paperListVo2 = new PaperListVo();
        //高级问卷
        PaperListVo paperListVo3 = new PaperListVo();
        //调用fillTestSoul方法选择不同的问卷
        fillTestSoul(paperListVo1, 1);
        fillTestSoul(paperListVo2, 2);
        fillTestSoul(paperListVo3, 3);
        //根据用户的id进行查询自己相应的报告
        QueryWrapper<SoulReport> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", user.getId());
        List<SoulReport> soulReportList = this.soulReportMapper.selectList(queryWrapper);
        //判断报告是否为空
        Integer size = soulReportList.size();
        if (size == 0) {
            //报告为初级,说明此时用户还没有进行过测试,那么就要锁住中级和高级的问卷,释放初级问卷
            paperListVo1.setIsLock(0);
            paperListVo2.setIsLock(1);
            paperListVo3.setIsLock(1);
        } else if (size == 1) {
            //有一个报告,说明此时用户已经完成初级测试,那么要开放中级
            paperListVo1.setIsLock(0);
            paperListVo2.setIsLock(0);
            paperListVo3.setIsLock(1);
            //拿到最新的报告id
            for (SoulReport soulReport : soulReportList) {
                if (soulReport.getPaperId() == 1) {
                    paperListVo1.setReportId(soulReport.getId().toString());
                }
                paperListVo2.setReportId(null);
                paperListVo3.setReportId(null);

            }
        } else if (size == 2) {
            //有两个报告,说明此时用户已经完成初级,中级测试,那么要开放高级
            paperListVo1.setIsLock(0);
            paperListVo2.setIsLock(0);
            paperListVo3.setIsLock(0);
            //拿到最新的报告id
            for (SoulReport soulReport : soulReportList) {
                if (soulReport.getPaperId() == 1) {
                    paperListVo1.setReportId(soulReport.getId().toString());
                }
                if (soulReport.getPaperId() == 2) {
                    paperListVo2.setReportId(soulReport.getId().toString());
                }
                paperListVo3.setReportId(null);
            }
        } else if (size == 3) {
            for (SoulReport soulReport : soulReportList) {
                if (soulReport.getPaperId() == 1) {
                    paperListVo1.setReportId(soulReport.getId().toString());
                }
                if (soulReport.getPaperId() == 2) {
                    paperListVo2.setReportId(soulReport.getId().toString());
                }
                if (soulReport.getPaperId() == 3) {
                    paperListVo3.setReportId(soulReport.getId().toString());
                }

            }
        }
        //返回三个问卷集合
        List<PaperListVo> list = new ArrayList<>();
        list.add(paperListVo1);
        list.add(paperListVo2);
        list.add(paperListVo3);

        return list;

    }


    /**
     * 创建一个方法根据不同的级别来填充问卷问题
     */
    private void fillTestSoul(PaperListVo paperListVo, int id) {
        //1.根据SoulPaper之中的信息填充
        //添加问卷编号
        paperListVo.setId(String.valueOf(id));
        //添加问卷姓名
        paperListVo.setName(this.soulPaperMapper.selectById(id).getName());
        //添加问卷封面
        paperListVo.setCover(this.soulPaperMapper.selectById(id).getCover());
        //添加问卷级别
        paperListVo.setLevel(this.soulPaperMapper.selectById(id).getLevel());
        //设置问卷星级别
        paperListVo.setStar(this.soulPaperMapper.selectById(id).getStar());
        //2.根据SoulPaperQuestion中的信息进行填充
        //设置查询条件------>问题要根据传来的id进行判定该给哪一套信息
        QueryWrapper<SoulPaperQuestion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("paper_id", id);
        //根据soulPaperQuestionMapper可以查询到每种类型所对应的所有题目id集合
        List<SoulPaperQuestion> soulPaperQuestionList = this.soulPaperQuestionMapper.selectList(queryWrapper);
        //创建题目集合用来保存每种类型对应的题问题
        List<QuestionsVo> questionsVoList = new ArrayList<>();
        //遍历题目
        for (SoulPaperQuestion soulPaperQuestion : soulPaperQuestionList) {
            //填充问题信息
            QuestionsVo questionsVo = new QuestionsVo();
            //添加题目id
            questionsVo.setId(soulPaperQuestion.getId().toString());
            //添加题目---通过soulPaperQuestion查询到问题id,再通过soulQuestionMapper查询到对应的问题
            questionsVo.setQuestion(this.soulQuestionMapper.selectById(soulPaperQuestion.getQuestionId()).getQuestion());

            //设置查询条件-------->问题的id查询到所有的选项
            QueryWrapper<SoulOptions> optionsQueryWrapper = new QueryWrapper<>();
            optionsQueryWrapper.eq("question_id", soulPaperQuestion.getQuestionId());
            //得到选项的集合
            List<SoulOptions> soulOptionsList = this.soulOptionsMapper.selectList(optionsQueryWrapper);
            //3.填充选项的内容
            //创建一个集合得到一个问题所有的选项
            List<OptionsVo> optionsVolist = new ArrayList<>();
            for (SoulOptions soulOptions : soulOptionsList) {
                OptionsVo optionsVo = new OptionsVo();
                //添加主键id
                optionsVo.setId(soulOptions.getId().toString());
                //添加选项
                optionsVo.setOption(soulOptions.getOptions());
                //添加到选项集合
                optionsVolist.add(optionsVo);
            }
            //将选项集合添加到问题中
            questionsVo.setOptions(optionsVolist);
            //将所有信息保存到问题集合中
            questionsVoList.add(questionsVo);
        }
        //添加到PaperListVo中
        paperListVo.setQuestions(questionsVoList);
    }


    /**
     * 测灵魂----查看报告
     *
     * @param reportId 报告id
     * @return 鉴定结果
     */
    public ConclusionVo getReport(String reportId) {
        //校验
        User user = UserThreadLocal.get();
        //创建鉴定结果对象
        ConclusionVo conclusionVo = new ConclusionVo();
        //首先根据report表获取报告中的数据
        QueryWrapper<SoulReport> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", reportId);
        SoulReport soulReport = soulReportMapper.selectOne(queryWrapper);
        //判断是否为空
        if (null == soulReport) {
            //为空
            return null;
        }
        //不为空,首先获取用户的得分
        Long score = soulReport.getScore();
        //再到report表中查询到分数近似的人---和自己分数相差8分以内,且不是自己,且答的是同一份问卷
        QueryWrapper<SoulReport> queryListWrapper = new QueryWrapper<>();
        queryListWrapper.between("score", score - 10, score + 10).ne("user_id", soulReport.getUserId()).eq("paper_id", soulReport.getPaperId());
        List<SoulReport> soulReportList = soulReportMapper.selectList(queryListWrapper);
        //获取这些分数相似的id--通过一个集合
        Set<Long> userIds = new HashSet<>();
        for (SoulReport report : soulReportList) {
            userIds.add(report.getUserId());
        }
        //填充这些人的信息
        //创建一个集合用来保存这些相似的人
        List<SoulSimilarYou> soulSimilarYouList = new ArrayList<>();
        //首先根据id查询到所有人的信息
        QueryWrapper<UserInfo> infoQueryWrapper = new QueryWrapper<>();
        infoQueryWrapper.in("user_id", userIds);
        List<UserInfo> userInfoList = this.userInfoService.queryUserInfoList(infoQueryWrapper);
        //然后匹配id,id相等则可以填充
        for (SoulReport report : soulReportList) {
            for (UserInfo userInfo : userInfoList) {
                if (report.getUserId().longValue() == userInfo.getUserId().longValue()) {
                    //说明id一致,可以进行填充
                    SoulSimilarYou soulSimilarYou = new SoulSimilarYou();
                    soulSimilarYou.setId(Integer.valueOf(report.getUserId().toString()));
                    soulSimilarYou.setAvatar(userInfo.getLogo());
                    //添加到集合中,相似的人的功能结束
                    soulSimilarYouList.add(soulSimilarYou);
                }
            }
        }
        //进行结果的鉴定---根据不同的得分判断不同的类型
        if (soulReport.getScore() < 21) {
            //如果分数小于,则判定为猫头鹰
            conclusionVo.setConclusion(ConclusionEnum.MAOTOUTING.getValue());
            //设置封面
            conclusionVo.setCover("https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/test_soul/owl.png");
            //创建一个集合用来保存维度
            List<SoulDimensions> dimensionsList = new ArrayList<>();
            //定义维度
            SoulDimensions s1 = new SoulDimensions("外向", "80%");
            SoulDimensions s2 = new SoulDimensions("判断", "70%");
            SoulDimensions s3 = new SoulDimensions("抽象", "90%");
            SoulDimensions s4 = new SoulDimensions("理性", "60%");
            //添加到维度集合中
            dimensionsList.add(s1);
            dimensionsList.add(s2);
            dimensionsList.add(s3);
            dimensionsList.add(s4);
            //添加到鉴定结果中
            conclusionVo.setDimensions(dimensionsList);
            conclusionVo.setSimilarYou(soulSimilarYouList);
        } else if (soulReport.getScore() >= 21 && soulReport.getScore() < 40) {
            //如果分数小于,则判定为白兔
            conclusionVo.setConclusion(ConclusionEnum.BAITU.getValue());
            //设置封面
            conclusionVo.setCover("https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/test_soul/rabbit.png");
            //创建一个集合用来保存维度
            List<SoulDimensions> dimensionsList = new ArrayList<>();
            //定义维度
            SoulDimensions s1 = new SoulDimensions("外向", "90%");
            SoulDimensions s2 = new SoulDimensions("判断", "60%");
            SoulDimensions s3 = new SoulDimensions("抽象", "80%");
            SoulDimensions s4 = new SoulDimensions("理性", "70%");
            //添加到维度集合中
            dimensionsList.add(s1);
            dimensionsList.add(s2);
            dimensionsList.add(s3);
            dimensionsList.add(s4);
            //添加到鉴定结果中
            conclusionVo.setDimensions(dimensionsList);
            conclusionVo.setSimilarYou(soulSimilarYouList);
        } else if (soulReport.getScore() >= 41 && soulReport.getScore() < 55) {
            //如果分数小于,则判定为狐狸
            conclusionVo.setConclusion(ConclusionEnum.HULI.getValue());
            //设置封面
            conclusionVo.setCover("https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/test_soul/fox.png");
            //创建一个集合用来保存维度
            List<SoulDimensions> dimensionsList = new ArrayList<>();
            //定义维度
            SoulDimensions s1 = new SoulDimensions("外向", "70%");
            SoulDimensions s2 = new SoulDimensions("判断", "90%");
            SoulDimensions s3 = new SoulDimensions("抽象", "60%");
            SoulDimensions s4 = new SoulDimensions("理性", "80%");
            //添加到维度集合中
            dimensionsList.add(s1);
            dimensionsList.add(s2);
            dimensionsList.add(s3);
            dimensionsList.add(s4);
            //添加到鉴定结果中
            conclusionVo.setDimensions(dimensionsList);
            conclusionVo.setSimilarYou(soulSimilarYouList);
        } else if (soulReport.getScore() >= 56) {
            //如果分数小于,则判定为狮子
            conclusionVo.setConclusion(ConclusionEnum.SHIZI.getValue());
            //设置封面
            conclusionVo.setCover("https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/test_soul/lion.png");
            //创建一个集合用来保存维度
            List<SoulDimensions> dimensionsList = new ArrayList<>();
            //定义维度
            SoulDimensions s1 = new SoulDimensions("外向", "60%");
            SoulDimensions s2 = new SoulDimensions("判断", "80%");
            SoulDimensions s3 = new SoulDimensions("抽象", "70%");
            SoulDimensions s4 = new SoulDimensions("理性", "90%");
            //添加到维度集合中
            dimensionsList.add(s1);
            dimensionsList.add(s2);
            dimensionsList.add(s3);
            dimensionsList.add(s4);
            //添加到鉴定结果中
            conclusionVo.setDimensions(dimensionsList);
            conclusionVo.setSimilarYou(soulSimilarYouList);
        } else {
            return null;
        }
        return conclusionVo;
    }

    /**
     * 测灵魂--提交报告
     *
     * @param map 参数
     * @return 报告id
     */
    public String submitTestPaper(Map<String, List<Answers>> map) {
        //校验
        User user = UserThreadLocal.get();
        //设置初始化分数
        Long score = 0L;
        Long questionId = 0L;
        //遍历集合获得所有数据----计算得分
        Collection<List<Answers>> answersList = map.values();
        for (List<Answers> answers : answersList) {
            for (Answers answer : answers) {
                //拿到试题id
                questionId = Long.valueOf(answer.getQuestionId());
                //拿到选项id
                String optionId = answer.getOptionId();
                //根据试题id和选项id拿到具体的选项
                QueryWrapper<SoulOptions> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("question_id", questionId);
                queryWrapper.eq("option_id", optionId);
                SoulOptions options = this.soulOptionsMapper.selectOne(queryWrapper);
                //根据选项设置得分
                score += options.getScore();
            }
        }
        //总分已经计算得到,然后判断此时用户做的哪一套试卷---根据questionId来判断
        QueryWrapper<SoulPaperQuestion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("question_id", questionId);
        SoulPaperQuestion soulPaperQuestion = this.soulPaperQuestionMapper.selectOne(queryWrapper);
        if (null == soulPaperQuestion) {
            return null;
        }
        //判断该用户是否已经有当前问卷的报告
        QueryWrapper<SoulReport> reportQueryWrapper = new QueryWrapper<>();
        reportQueryWrapper.eq("user_id", user.getId());
        //可以得到用户作答的是哪一套问卷
        reportQueryWrapper.eq("paper_id", soulPaperQuestion.getPaperId());
        SoulReport soulReport = this.soulReportMapper.selectOne(reportQueryWrapper);
        //判断报告是否为空
        if (null != soulReport) {
            //报告不为空, 说明此用户已经有当前问卷的报告,那么更新报告并返回id
            soulReport.setPaperId(soulPaperQuestion.getPaperId());
            soulReport.setScore(score);
            soulReport.setUserId(user.getId());
            soulReport.setUpdated(new Date(System.currentTimeMillis()));
            //更新报告内容---设置查询条件----更新到数据库
            QueryWrapper<SoulReport> soulReportQueryWrapper = new QueryWrapper<>();
            soulReportQueryWrapper.eq("user_id", user.getId());
            this.soulReportMapper.update(soulReport, soulReportQueryWrapper);
            return soulReport.getId().toString();
        }
        //如果不存在就直接保存到数据库
        SoulReport report = new SoulReport();
        report.setUserId(user.getId());
        report.setScore(score);
        report.setPaperId(soulPaperQuestion.getPaperId());
        report.setCreated(new Date(System.currentTimeMillis()));
        //插入到数据库中
        this.soulReportMapper.insert(report);
        return report.getId().toString();
    }
}
