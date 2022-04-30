package cn.newangels.system.controller;

import cn.newangels.system.service.ActivitiService;
import lombok.RequiredArgsConstructor;
import org.activiti.engine.TaskService;
import org.springframework.web.bind.annotation.RestController;

/**
 * todo 待删除
 *
 * @author: XuQiang
 * @date: 2021/12/1 10:57
 * @since: 1.0
 */
@RestController
@RequiredArgsConstructor
public class HastenWorkController {
    private final TaskService taskService;
    private final ActivitiService activitiService;
    /*@Autowired
    private WxCommonClientService wxCommonClientService;*/

    /**
     * 查询审批情况
     * (cron = "0 0 8 * * ?") 表示每天的8点触发
     */
    //@Scheduled(cron = "0 0 8 * * ?")
    ////@Scheduled(cron = "0/30 * * * * *")//每两秒执行一次,测试使用
    //public Map selectHastenWork() {
    //    try {
    //        //查询当前已过期的任务
    //        List<Task> taskDueBeforeList = taskService.createTaskQuery().taskDueBefore(new Date()).list();
    //        if (taskDueBeforeList != null) {
    //            for (Task task : taskDueBeforeList) {
    //                //获得审批人编码
    //                String assignee = task.getAssignee();
    //                String EMP_CODE_ = assignee.substring(assignee.indexOf("EMP[") + 4, assignee.indexOf("]POSI["));
    //                //推送消息
    //                /*ProcessInstance processInstance = activitiService.loadProcessInstance(task.getProcessInstanceId(), null);
    //                if (processInstance != null) {
    //                    String startUser = processInstance.getStartUser();//流程发起人编码
    //                    Map<String, Object> startUserMap = wxCommonClientService.loadUser(startUser);
    //                    String START_EMP_NAME_ = (startUserMap != null ? (String) ((Map) startUserMap.get("emp")).get("USER_NAME") : "");//流程发起人名称
    //                    String processDefinitionName = processInstance.getProcessDefinitionName();//流程名称
    //
    //                    Map<String, Object> userMap = wxCommonClientService.loadUser(EMP_CODE_);
    //                    String EMP_NAME_ = (userMap != null ? (String) ((Map) userMap.get("emp")).get("USER_NAME") : "");//推送人名称
    //
    //                    Map<String, Object> wxOauthByEmpCodeMap = wxCommonClientService.loadWxOauthByEmpCode(EMP_CODE_);
    //                    if (wxOauthByEmpCodeMap != null && wxOauthByEmpCodeMap.get("wxOauth") != null) {
    //                        //获取微信号加密后的openId
    //                        String OPEN_ID_ = ((Map<String, Object>) wxOauthByEmpCodeMap.get("wxOauth")).get("OPEN_ID_").toString();
    //                        //推送路径
    //                        String WX_URL_ = getWxUrl() + "?openid=" + OPEN_ID_ + "&origin=" + START_EMP_NAME_ + "&subject=" + processDefinitionName + "&state=待审批&msg=点击卡片进入审批页面&taskid=" + task.getParentTaskId() + "&sys=vendor";
    //                        //发送
    //                        String resultBuffer = sendRequest(WX_URL_, "POST");
    //                        wxCommonClientService.insertWxLog(OPEN_ID_, EMP_CODE_, EMP_NAME_, WX_URL_, "vendor催办", resultBuffer);
    //                    } else {
    //                        wxCommonClientService.insertWxLog("无微信,用户微信未绑定", EMP_CODE_, EMP_NAME_, "无微信,用户微信未绑定", "vendor催办", "无微信,用户微信未绑定");
    //                    }
    //                }*/
    //            }
    //        }
    //    } catch (Exception e) {
    //        e.printStackTrace();
    //    }
    //    Map<String, Object> result = new HashMap<>();
    //    result.put("success", true);
    //    return result;
    //}
}
