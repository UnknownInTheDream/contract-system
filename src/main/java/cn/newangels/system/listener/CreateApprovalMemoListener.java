package cn.newangels.system.listener;

import cn.newangels.common.util.SpringContextUtils;
import cn.newangels.system.service.ApprovalMemoService;
import cn.newangels.system.service.BaseService;
import cn.newangels.system.service.impl.ApprovalMemoServiceImpl;
import cn.newangels.system.service.impl.BaseServiceImpl;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class CreateApprovalMemoListener implements TaskListener {
    private Expression APPROVAL_MEMO_TYPE_;

    /**
     * 为新增的待办任务添加空白审批意见. 其中人员岗位默认选取该人员的唯一岗位. 如该人员有多个岗位, 则获取category表达式获得节点定义岗位列表, 并从中选取该人的岗位.
     *
     * @param delegateTask 代理任务实例
     */
    @Override
    public void notify(DelegateTask delegateTask) {
        BaseService baseService = SpringContextUtils.getBean(BaseServiceImpl.class);
        ApprovalMemoService approvalMemoService = SpringContextUtils.getBean(ApprovalMemoServiceImpl.class);
        String TASK_ID_ = delegateTask.getId();
        String PARENT_TASK_ID_ = null;
        String EXECUTION_ID_ = delegateTask.getExecutionId();
        String PROC_INST_ID_ = delegateTask.getProcessInstanceId();
        String TASK_NAME_ = delegateTask.getName();
        String BIZ_ID_ = (String) delegateTask.getVariable("bizId");

        String assigneeString = delegateTask.getAssignee();
        String ASSIGNEE_CODE_ = assigneeString.substring(assigneeString.indexOf("[") + 1, assigneeString.indexOf("]"));
        //String ROLE_ID_ = assigneeString.substring(assigneeString.lastIndexOf("[") + 1, assigneeString.lastIndexOf("]"));
        Map<String, Object> emp = baseService.loadEmpByEmpCode(ASSIGNEE_CODE_);   //TODO 查询人员
        /*List<Map<String, Object>> posiEmpList = baseService.selectRoleByEmpCode(ASSIGNEE_CODE_);   //TODO 查询人员角色

        Map<String, Object> posiEmp = null;
        for (Map<String, Object> _posiEmp : posiEmpList) {
            if (ROLE_ID_.equals(_posiEmp.get("V_ROLEID"))) {
                posiEmp = _posiEmp;
                break;
            }
        }*/

        // String ROLE_NAME_ = (posiEmp != null ? (String) posiEmp.get("ROLE_NAME_") : null);
        String ROLE_NAME_ = null;
        String ROLE_ID_ = null;
        String ASSIGNEE_NAME_ = (emp != null ? (String) emp.get("V_PERNAME") : null);
        String EMP_CODE_ = (emp != null ? (String) emp.get("V_PERCODE") : null);
        String ORG_CODE_ = (emp != null ? (String) emp.get("V_DEPTCODE") : null);
        String ORG_NAME_ = (emp != null ? (String) emp.get("V_DEPTNAME") : null);
        String COM_CODE_ = (emp != null ? (String) emp.get("V_ORGNAME") : null);
        String COM_NAME_ = (emp != null ? (String) emp.get("V_ORGCODE") : null);
        String EMAIL_ = null;
        String DELEGATE_CODE_ = null;
        String DELEGATE_NAME_ = null;
        Date CREATION_DATE_ = delegateTask.getCreateTime();
        Date DUE_DATE_ = delegateTask.getDueDate();
        Integer approvalType = 0;
        String APPROVAL_MEMO_ = null;
        String APPROVAL_MEMO_STATUS_ = "0";
        if (this.APPROVAL_MEMO_TYPE_ != null) {
            approvalType = new Integer((String) this.APPROVAL_MEMO_TYPE_.getValue(delegateTask)).intValue();
        }

        //TODO 审批意见
        approvalMemoService.insertApprovalMemo(TASK_ID_, PARENT_TASK_ID_, EXECUTION_ID_, PROC_INST_ID_, TASK_NAME_, BIZ_ID_, ASSIGNEE_CODE_, ASSIGNEE_NAME_, ROLE_ID_, ROLE_NAME_, ORG_CODE_, ORG_NAME_, COM_CODE_, COM_NAME_, EMAIL_, DELEGATE_CODE_, DELEGATE_NAME_, CREATION_DATE_, DUE_DATE_, approvalType, APPROVAL_MEMO_, APPROVAL_MEMO_STATUS_, null);

        //推送消息
        /*ProcessInstance processInstance = activitiService.loadProcessInstance(PROC_INST_ID_, null);
        if (processInstance != null) {
            String startUser = processInstance.getStartUser();//流程发起人编码
            Map<String, Object> startUserMap = wxCommonClientService.loadUser(startUser);
            String START_EMP_NAME_ = (startUserMap != null ? (String) ((Map) startUserMap.get("emp")).get("USER_NAME") : "");//流程发起人名称
            String processDefinitionName = processInstance.getProcessDefinitionName();//流程名称

            Map<String, Object> userMap = wxCommonClientService.loadUser(EMP_CODE_);
            String EMP_NAME_ = (userMap != null ? (String) ((Map) userMap.get("emp")).get("USER_NAME") : "");//推送人名称

            Map<String, Object> wxOauthByEmpCodeMap = wxCommonClientService.loadWxOauthByEmpCode(EMP_CODE_);
            if (wxOauthByEmpCodeMap != null && wxOauthByEmpCodeMap.get("wxOauth") != null) {
                //获取微信号加密后的openId
                String OPEN_ID_ = ((Map<String, Object>) wxOauthByEmpCodeMap.get("wxOauth")).get("OPEN_ID_").toString();
                //推送路径
                String WX_URL_ = getWxUrl() + "?openid=" + OPEN_ID_ + "&origin=" + START_EMP_NAME_ + "&subject=" + processDefinitionName + "&state=待审批&msg=点击卡片进入审批页面&taskid=" + TASK_ID_ + "&sys=mw";
                //发送
                String resultBuffer = sendRequest(WX_URL_, "POST");
                wxCommonClientService.insertWxLog(OPEN_ID_, EMP_CODE_, EMP_NAME_, WX_URL_, "mw", resultBuffer);
            } else {
                wxCommonClientService.insertWxLog("无微信,用户微信未绑定", EMP_CODE_, EMP_NAME_, "无微信,用户微信未绑定", "mw", "无微信,用户微信未绑定");
            }
        }*/
    }
}
