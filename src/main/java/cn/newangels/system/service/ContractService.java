package cn.newangels.system.service;

import cn.newangels.system.dto.Task;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 合同
 *
 * @author: ll
 * @date: 2022/02/16 09:01
 * @since: 1.0
 */
public interface ContractService {

    /**
     * 加载合同
     *
     * @param I_ID id
     */
    Map<String, Object> loadContract(String I_ID);

    /**
     * 查询合同
     *
     * @param V_SPONSORNAME    定作方名称
     * @param V_CONTRACTORNAME 承揽方名称
     * @param V_MAJORID        专业id
     * @param V_BEGIN_DATE     开始时间
     * @param V_END_DATE       结束时间
     * @param V_PROJECTNAME    项目名称
     * @param current          当前页数
     * @param pageSize         每次显示数量
     */
    List<Map<String, Object>> selectContract(String V_SPONSORNAME, String V_CONTRACTORNAME, String V_MAJORID, String V_INST_STATUS, Date V_BEGIN_DATE, Date V_END_DATE, String V_PROJECTNAME, String V_PER_EDIT, String permissionType, String V_DEPTCODE, String V_ORGCODE, String V_STAMPSTATUS, Integer current, Integer pageSize);

    /**
     * 查询数量
     *
     * @param V_SPONSORNAME    定作方名称
     * @param V_CONTRACTORNAME 承揽方名称
     * @param V_MAJORID        专业id
     * @param V_BEGIN_DATE     开始时间
     * @param V_END_DATE       结束时间
     * @param V_PROJECTNAME    项目名称
     */
    int countContract(String V_SPONSORNAME, String V_CONTRACTORNAME, String V_MAJORID, String V_INST_STATUS, Date V_BEGIN_DATE, Date V_END_DATE, String V_PROJECTNAME, String V_PER_EDIT, String permissionType, String V_DEPTCODE, String V_ORGCODE, String V_STAMPSTATUS);

    /**
     * 新增合同
     *
     * @param I_ID             id
     * @param V_CONTRACTCODE   合同编码
     * @param V_PROJECTCODE    项目编码
     * @param V_PROJECTNAME    项目名称
     * @param V_SPONSORID      定作方id
     * @param V_SPONSORCODE    定作方编码
     * @param V_SPONSORNAME    定作方名称
     * @param V_OFFICER        定作方法人
     * @param V_CONTRACTORID   承揽方id
     * @param V_CONTRACTORNAME 承揽方名称
     * @param V_CREDIT         承揽方信用等级
     * @param V_MONEY          项目金额
     * @param V_YEAR           年份
     * @param I_COPIES         合同份数
     * @param I_REVIEWCOPIES   签审表份数
     * @param V_MAJORID        专业id
     * @param V_TEMPLATEID     模版id
     * @param V_ACCORDANCE     签订类型
     * @param V_STARTDATE      履行起始时间
     * @param V_ENDDATE        履行终止时间
     * @param V_PER_EDIT       最后修改人
     * @param V_JSON           json
     * @param V_SPONSORABBR    定作方简称
     * @param V_PROJECTTYPE    合同项目类型
     * @param V_CONTRACTTYPE   合同履行类型
     */
    int insertContract(String I_ID, String V_CONTRACTCODE, String V_PROJECTCODE, String V_PROJECTNAME, String V_SPONSORID, String V_SPONSORCODE, String V_SPONSORNAME, String V_OFFICER, String V_CONTRACTORID, String V_CONTRACTORNAME, String V_CREDIT, String V_MONEY, String V_YEAR, Integer I_COPIES, Integer I_REVIEWCOPIES, String V_MAJORID, String V_TEMPLATEID, String V_ACCORDANCE, Date V_STARTDATE, Date V_ENDDATE, MultipartFile[] multipartFiles, String V_PER_EDIT, String V_JSON, String V_SPONSORABBR, String V_PROJECTTYPE, String V_CONTRACTTYPE, String V_CONTRACTUPID) throws IOException;

    /**
     * 修改合同
     *
     * @param I_ID             id
     * @param V_CONTRACTCODE   合同编码
     * @param V_PROJECTCODE    项目编码
     * @param V_PROJECTNAME    项目名称
     * @param V_SPONSORID      定作方id
     * @param V_SPONSORCODE    定作方编码
     * @param V_SPONSORNAME    定作方名称
     * @param V_OFFICER        定作方法人
     * @param V_CONTRACTORID   承揽方id
     * @param V_CONTRACTORNAME 承揽方名称
     * @param V_CREDIT         承揽方信用等级
     * @param V_MONEY          项目金额
     * @param V_YEAR           年份
     * @param I_COPIES         合同份数
     * @param I_REVIEWCOPIES   签审表份数
     * @param V_MAJORID        专业id
     * @param V_TEMPLATEID     模版id
     * @param V_ACCORDANCE     签订类型
     * @param V_STARTDATE      履行起始时间
     * @param V_ENDDATE        履行终止时间
     * @param V_PER_EDIT       最后修改人
     * @param V_JSON           json
     * @param V_SPONSORABBR    定作方简称
     * @param V_PROJECTTYPE    合同项目类型
     * @param V_CONTRACTTYPE   合同履行类型
     */
    int updateContract(String I_ID, String V_CONTRACTCODE, String V_PROJECTCODE, String V_PROJECTNAME, String V_SPONSORID, String V_SPONSORCODE, String V_SPONSORNAME, String V_OFFICER, String V_CONTRACTORID, String V_CONTRACTORNAME, String V_CREDIT, String V_MONEY, String V_YEAR, Integer I_COPIES, Integer I_REVIEWCOPIES, String V_MAJORID, String V_TEMPLATEID, String V_ACCORDANCE, Date V_STARTDATE, Date V_ENDDATE, MultipartFile[] multipartFiles, String V_PER_EDIT, String V_JSON, String V_SPONSORABBR, String V_PROJECTTYPE, String V_CONTRACTTYPE, String V_CONTRACTUPID) throws IOException;

    /**
     * 删除合同
     *
     * @param I_ID      id
     * @param V_PERCODE 人员编码
     */
    int deleteContract(String I_ID, String V_PERCODE);

    /**
     * 待办页
     * 根据合同ID查询合同信息
     *
     * @param I_ID id
     */
    List<Map<String, Object>> selectContractInTask(String I_ID, Date V_BEGIN_DATE, Date V_END_DATE);

    /**
     * 合同上报
     *
     * @param I_ID                    合同ID
     * @param ASSIGNEE_               候选人
     * @param PROCESS_DEFINITION_KEY_ 流程定义key
     * @param operator                操作人
     */
    String startContractProcess(String I_ID, String ASSIGNEE_, String PROCESS_DEFINITION_KEY_, Map<String, Object> operator);

    /**
     * 修改合同审批状态
     *
     * @param I_ID                  合同ID
     * @param V_INST_STATUS         审批状态
     * @param V_PROCESS_INSTANCE_ID 流程实例ID
     */
    int updateContractApprovalStatus(String I_ID, Integer V_INST_STATUS, String V_PROCESS_INSTANCE_ID);

    /**
     * 合同审批
     *
     * @param TASK_ID_       子流程ID
     * @param ASSIGNEE_      候选人
     * @param APPROVAL_MEMO_ 审批语
     * @param operator       操作人
     */
    int completeContractInTask(String TASK_ID_, String APPROVAL_, String ASSIGNEE_, ArrayList<String> ASSIGNEE_LIST, String APPROVAL_MEMO_, Map<String, Object> operator, Task task);

    /**
     * 合同撤审
     *
     * @param TASK_ID_       子流程ID
     * @param ASSIGNEE_      候选人
     * @param APPROVAL_MEMO_ 审批语
     * @param operator       操作人
     * @return
     */
    public int withdrawContractInTask(String TASK_ID_, String APPROVAL_, String ASSIGNEE_, ArrayList<String> ASSIGNEE_LIST, String APPROVAL_MEMO_, Map<String, Object> operator, Task task);

    /**
     * 修改合同盖章状态
     *
     * @param I_ID          合同ID
     * @param V_STAMPSTATUS 盖章状态
     */
    int updateContractStampStatus(String I_ID, Integer V_STAMPSTATUS);
}