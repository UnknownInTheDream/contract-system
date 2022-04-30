package cn.newangels.system.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 审批意见
 */
public interface ApprovalMemoService {
    /**
     * 根据ID查询审批意见
     *
     * @param TASK_ID_
     * @param operator
     * @return
     */
    Map<String, Object> loadApprovalMemo(String TASK_ID_, Map<String, Object> operator);

    /**
     * 查询审批意见
     *
     * @param PROC_INST_ID_
     * @param BIZ_ID_
     * @param ASSIGNEE_CODE_
     * @param ignoreEmptyDispatch
     * @param current
     * @param pageSize
     * @param operator
     * @return
     */
    List<Map<String, Object>> selectApprovalMemo(String PROC_INST_ID_, String BIZ_ID_, String ASSIGNEE_CODE_, String APPROVAL_MEMO_STATUS_, Boolean ignoreEmptyDispatch, Integer current, Integer pageSize, Map<String, Object> operator);

    /**
     * 统计审批意见
     *
     * @param PROC_INST_ID_
     * @param BIZ_ID_
     * @param ASSIGNEE_CODE_
     * @param ignoreEmptyDispatch
     * @param operator
     * @return
     */
    int countApprovalMemo(String PROC_INST_ID_, String BIZ_ID_, String ASSIGNEE_CODE_, String APPROVAL_MEMO_STATUS_, Boolean ignoreEmptyDispatch, Map<String, Object> operator);

    /**
     * 查询某人的审批意见
     *
     * @param ASSIGNEE_CODE_
     * @param CODE_
     * @param TITLE_
     * @param APPROVAL_MEMO_STATUS_
     * @param current
     * @param pageSize
     * @param operator
     * @return
     */
    List<Map<String, Object>> selectOwnApprovalMemo(String ASSIGNEE_CODE_, String CODE_, String TITLE_, String APPROVAL_MEMO_STATUS_, Integer current, Integer pageSize, Map<String, Object> operator);

    /**
     * 统计某人的审批意见
     *
     * @param ASSIGNEE_CODE_
     * @param CODE_
     * @param TITLE_
     * @param APPROVAL_MEMO_STATUS_
     * @param operator
     * @return
     */
    int countOwnApprovalMemo(String ASSIGNEE_CODE_, String CODE_, String TITLE_, String APPROVAL_MEMO_STATUS_, Map<String, Object> operator);

    /**
     * 新增审批意见
     *
     * @param TASK_ID_
     * @param PARENT_TASK_ID_
     * @param EXECUTION_ID_
     * @param PROC_INST_ID_
     * @param TASK_NAME_
     * @param BIZ_ID_
     * @param ASSIGNEE_CODE_
     * @param ASSIGNEE_NAME_
     * @param ROLE_ID_
     * @param ROLE_NAME_
     * @param ORG_CODE_
     * @param ORG_NAME_
     * @param COM_CODE_
     * @param COM_NAME_
     * @param EMAIL_
     * @param DELEGATE_CODE_
     * @param DELEGATE_NAME_
     * @param CREATION_DATE_
     * @param DUE_DATE_
     * @param APPROVAL_MEMO_TYPE_
     * @param APPROVAL_MEMO_
     * @param APPROVAL_MEMO_STATUS_
     * @param operator
     * @return
     */
    int insertApprovalMemo(String TASK_ID_, String PARENT_TASK_ID_, String EXECUTION_ID_, String PROC_INST_ID_, String TASK_NAME_, String BIZ_ID_, String ASSIGNEE_CODE_, String ASSIGNEE_NAME_, String ROLE_ID_, String ROLE_NAME_, String ORG_CODE_, String ORG_NAME_, String COM_CODE_, String COM_NAME_, String EMAIL_, String DELEGATE_CODE_, String DELEGATE_NAME_, Date CREATION_DATE_, Date DUE_DATE_, Integer APPROVAL_MEMO_TYPE_, String APPROVAL_MEMO_, String APPROVAL_MEMO_STATUS_, Map<String, Object> operator);

    /**
     * 批量新增审批意见.用于并发节点
     *
     * @param TASK_ID_LIST
     * @param PARENT_TASK_ID_
     * @param BIZ_ID_
     * @param ASSIGNEE_CODE_LIST
     * @param CREATION_DATE_
     * @param DUE_DATE_
     * @param APPROVAL_MEMO_TYPE_
     * @param operator
     * @return
     */
    int insertApprovalMemo(List<String> TASK_ID_LIST, String PARENT_TASK_ID_, String BIZ_ID_, List<String> ASSIGNEE_CODE_LIST, Date CREATION_DATE_, Date DUE_DATE_, Integer APPROVAL_MEMO_TYPE_, Map<String, Object> operator);

    /**
     * 更新审批意见
     *
     * @param TASK_ID_
     * @param APPROVAL_MEMO_
     * @param APPROVAL_MEMO_STATUS_
     * @param operator
     * @return
     */
    int updateApprovalMemo(String TASK_ID_, String APPROVAL_MEMO_, Integer APPROVAL_MEMO_STATUS_, Map<String, Object> operator);

    /**
     * 删除审批意见
     *
     * @param TASK_ID_
     * @param operator
     * @return
     */
    int deleteApprovalMemo(String TASK_ID_, Map<String, Object> operator);

    /**
     * 删除审批意见
     *
     * @param BIZ_ID_
     * @param operator
     * @return
     */
    int deleteApprovalMemoByFormId(String BIZ_ID_, Map<String, Object> operator);

    /**
     * 撤回
     *
     * @param TASK_ID_
     * @param DELEGATE_CODE_
     * @param DELEGATE_NAME_
     * @param APPROVAL_MEMO_
     * @param operator
     * @return
     */
    int withdrawToTask(String TASK_ID_, String DELEGATE_CODE_, String DELEGATE_NAME_, String APPROVAL_MEMO_, Map<String, Object> operator);

    /**
     * 是否已经审批过该公文
     *
     * @param BIZ_ID_
     * @param EMP_CODE_
     * @return
     */
    boolean hasApprove(String BIZ_ID_, String EMP_CODE_);

    /**
     * 获取Approval_type
     *
     * @param taskId
     * @return
     */
    Integer getApprovalType(String taskId);
}
