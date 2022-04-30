package cn.newangels.system.controller;

import cn.newangels.common.annotation.Log;
import cn.newangels.common.base.BaseUtils;
import cn.newangels.system.base.BaseOrder;
import cn.newangels.system.dto.*;
import cn.newangels.system.service.ActivitiService;
import cn.newangels.system.service.BaseService;
import cn.newangels.system.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static cn.newangels.common.base.BaseUtils.DATA;

/**
 * 流程管理
 */
@RestController
@RequiredArgsConstructor
public class ActivitiController {
    private final ActivitiService activitiService;
    private final BaseService baseService;
    private final ContractService contractService;

    @GetMapping("loadLastTask")
    @Log(title = "查询流程最后一个待办任务", operateType = "查询流程最后一个待办任务")
    public Map<String, Object> loadLastTask(String V_PERCODE, String processInstanceId) {
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> operator = baseService.getOperator(V_PERCODE);
        Task task = activitiService.loadLastTask(processInstanceId, operator);
        data.put(DATA, task);
        return BaseUtils.success(data);
    }

    @GetMapping("loadPreviousUserTask")
    @Log(title = "查询上个任务", operateType = "查询上个任务")
    public Map<String, Object> loadPreviousUserTask(String V_PERCODE, String taskId) {
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> operator = baseService.getOperator(V_PERCODE);
        Task task = activitiService.loadPreviousUserTask(taskId,null, operator);
        data.put(DATA, task);
        return BaseUtils.success(data);
    }

    @GetMapping("loadProcessInstance")
    @Log(title = "流程管理", operateType = "查询流程")
    public Map<String, Object> loadProcessInstance(String processInstanceId) {
        Map<String, Object> data = new HashMap<>();
        ProcessInstance processInstance = activitiService.loadProcessInstance(processInstanceId, null);
        data.put(DATA, processInstance);
        return BaseUtils.success(data);
    }

    // 查询所有流程
    @GetMapping("selectProcessInstance")
    @Log(title = "流程管理", operateType = "查询所有流程")
    public Map<String, Object> selectProcessInstance(String V_PERCODE, ProcessVariables processVariables, String processDefinitionKey, Date startedAfter, Date startedBefore, Integer current, Integer pageSize) {
        Map<String, Object> operator = baseService.getOperator(V_PERCODE);
        ProcessInstanceFilter processInstanceFilter = new ProcessInstanceFilter();
        processInstanceFilter.setProcessVariables(processVariables.getProcessVariables());
        processInstanceFilter.setProcessDefinitionKey(processDefinitionKey);
        processInstanceFilter.setStartedAfter(startedAfter);
        processInstanceFilter.setStartedBefore(startedBefore);
        List<ProcessInstance> processInstanceList = activitiService.selectProcessInstance(processInstanceFilter, null, current, pageSize, 1, operator);
        long total = 0;
        if (pageSize != null && pageSize > 0) {
            total = activitiService.countProcessInstance(processInstanceFilter, operator);
        }
        return BaseUtils.success(processInstanceList, total);
    }

    // 已办任务
    @GetMapping("selectInvolvedProcessInstance")
    @Log(title = "流程管理", operateType = "查询已办任务")
    public Map<String, Object> selectInvolvedProcessInstance(String V_PERCODE, String V_SPONSOR, String V_CONTRACTOR, String V_MAJORID, @DateTimeFormat(pattern = "yyyy-MM-dd") Date V_BEGIN_DATE, @DateTimeFormat(pattern = "yyyy-MM-dd") Date V_END_DATE, String processDefinitionKey, Integer finished, Integer current, Integer pageSize) {
        Map<String, Object> operator = baseService.getOperator(V_PERCODE);
        V_END_DATE = BaseUtils.addOneDay(V_END_DATE);
        //已办任务  新增查询条件
        Map<String, Object> processVariables = new HashMap();
        processVariables.put("V_SPONSORNAME", V_SPONSOR);
        processVariables.put("V_CONTRACTORNAME", V_CONTRACTOR);
        processVariables.put("V_MAJORID", V_MAJORID);
        String involvedUserId = "EMP[" + operator.get("EMP_CODE_") + "]";
        ProcessInstanceFilter processInstanceFilter = new ProcessInstanceFilter();
        processInstanceFilter.setInvolvedUserId(involvedUserId);
        processInstanceFilter.setProcessDefinitionKey(processDefinitionKey);
        processInstanceFilter.setFinished(finished);
        processInstanceFilter.setProcessVariables(processVariables);
        List<ProcessInstance> processInstanceList = activitiService.selectInvolvedProcessInstance(processInstanceFilter, null, current, pageSize, 9, operator);
        Map<String, Integer> resultMap = new HashMap<>();
        List<Map<String, Object>> task = new ArrayList<>();//数据计划
        List<String> str = new ArrayList<>();//表id
        List<ProcessInstance> tempList = new ArrayList<>();
        str.add("I_ID");    //TODO 合同id
        if (processInstanceList != null && processInstanceList.size() > 0) {
            for (int i = 0; i < processInstanceList.size(); i++) {
                resultMap.put(processInstanceList.get(i).getProcessVariables().get("bizId").toString(), i);
                List<Map<String, Object>> contractInTask = contractService.selectContractInTask(processInstanceList.get(i).getProcessVariables().get("bizId").toString(), V_BEGIN_DATE, V_END_DATE);
                for (int j = 0; j < contractInTask.size(); j++) {
                    task.add(contractInTask.get(j));
                    if (processInstanceList.get(i).getProcessVariables().get("bizId").toString().equals(contractInTask.get(j).get("I_ID"))) {
                        tempList.add(processInstanceList.get(i));
                    }
                }
            }
            for (int m = 0; m < task.size(); m++) {
                for (String strKey : str) {
                    if (task.get(m).get(strKey) != null) {
                        int j = resultMap.get(task.get(m).get(strKey));
                        processInstanceList.get(j).getProcessVariables().putAll(task.get(m));
                    }
                }
            }
        }
        //long total = activitiService.countInvolvedProcessInstance(processInstanceFilter, operator);
        return BaseUtils.success(tempList, tempList.size());
    }

    // 已结任务
    @GetMapping("selectInvolvedFinishedProcessInstance")
    @Log(title = "流程管理", operateType = "查询已结任务")
    public Map<String, Object> selectInvolvedFinishedProcessInstance(String V_PERCODE, ProcessVariables processVariables, String processDefinitionKey, Integer current, Integer pageSize) {
        Map<String, Object> operator = baseService.getOperator(V_PERCODE);
        ProcessInstanceFilter processInstanceFilter = new ProcessInstanceFilter();
        processInstanceFilter.setInvolvedUserId((String) operator.get("EMP_CODE_"));
        processInstanceFilter.setProcessDefinitionKey(processDefinitionKey);
        processInstanceFilter.setFinished(ProcessInstanceFilter.TRUE);
        processInstanceFilter.setProcessVariables(processVariables.getProcessVariables());
        List<ProcessInstance> processInstanceList = activitiService.selectProcessInstance(processInstanceFilter, null, current, pageSize, 9, operator);
        long total = activitiService.countProcessInstance(processInstanceFilter, operator);
        return BaseUtils.success(processInstanceList, total);
    }

    // 已结流程
    @GetMapping("selectFinishedProcessInstance")
    @Log(title = "流程管理", operateType = "查询已结流程")
    public Map<String, Object> selectFinishedProcessInstance(String V_PERCODE, ProcessVariables processVariables, Integer current, Integer pageSize) {
        Map<String, Object> operator = baseService.getOperator(V_PERCODE);
        ProcessInstanceFilter processInstanceFilter = new ProcessInstanceFilter();
        processInstanceFilter.setFinished(ProcessInstanceFilter.TRUE);
        processInstanceFilter.setProcessVariables(processVariables.getProcessVariables());
        List<ProcessInstance> processInstanceList = activitiService.selectProcessInstance(processInstanceFilter, null, current, pageSize, 9, operator);
        long total = 0;
        if (pageSize != null && pageSize > 0) {
            total = activitiService.countProcessInstance(processInstanceFilter, operator);
        }
        return BaseUtils.success(processInstanceList, total);
    }

    // 活动流程
    @GetMapping("selectActiveProcessInstance")
    @Log(title = "流程管理", operateType = "查询活动流程")
    public Map<String, Object> selectActiveProcessInstance(String V_PERCODE, String V_CONTRACTCODE, ProcessVariables processVariables, Integer suspended, Integer current, Integer pageSize, Integer threshold) {
        Map<String, Object> processVariablesMap = new HashMap();
        Map<String, Object> operator = baseService.getOperator(V_PERCODE);
        processVariablesMap.put("V_CONTRACTCODE", V_CONTRACTCODE); //合同编号
        processVariables.setProcessVariables(processVariablesMap);
        ProcessInstanceFilter processInstanceFilter = new ProcessInstanceFilter();
        processInstanceFilter.setSuspended(suspended);
        processInstanceFilter.setProcessVariables(processVariables.getProcessVariables());
        List<ProcessInstance> activeProcessInstanceList = activitiService.selectActiveProcessInstance(processInstanceFilter, null, current, pageSize, threshold, operator);
        long total = 0;
        if (pageSize != null && pageSize > 0) {
            total = activitiService.countActiveProcessInstance(processInstanceFilter, operator);
        }
        return BaseUtils.success(activeProcessInstanceList, total);
    }

    // 彻底删除流程
    @PostMapping("deleteProcessInstanceCompletely")
    @Log(title = "流程管理", operateType = "彻底删除流程")
    public Map<String, Object> deleteProcessInstanceCompletely(String V_PERCODE, String processInstanceId) {
        Map<String, Object> operator = baseService.getOperator(V_PERCODE);
        activitiService.deleteProcessInstanceCompletely(processInstanceId, operator);
        return BaseUtils.success();
    }

    // 删除流程
    @PostMapping("deleteProcessInstance")
    @Log(title = "流程管理", operateType = "删除流程")
    public Map<String, Object> deleteProcessInstance(String V_PERCODE, String processInstanceId, String deleteReason) {
        Map<String, Object> operator = baseService.getOperator(V_PERCODE);
        activitiService.deleteProcessInstance(processInstanceId, deleteReason, operator);
        return BaseUtils.success();
    }

    // 挂起流程
    @PostMapping("suspendProcessInstance")
    @Log(title = "流程管理", operateType = "挂起流程")
    public Map<String, Object> suspendProcessInstance(String V_PERCODE, String processInstanceId) {
        Map<String, Object> operator = baseService.getOperator(V_PERCODE);
        activitiService.suspendProcessInstance(processInstanceId, operator);
        return BaseUtils.success();
    }

    // 激活流程
    @PostMapping("activateProcessInstance")
    @Log(title = "流程管理", operateType = "激活流程")
    public Map<String, Object> activateProcessInstance(String V_PERCODE, String processInstanceId) {
        Map<String, Object> operator = baseService.getOperator(V_PERCODE);
        activitiService.activateProcessInstance(processInstanceId, operator);
        return BaseUtils.success();
    }

    // 正式流程（确保流程激活）
    @PostMapping("formalProcessInstance")
    @Log(title = "流程管理", operateType = "正式化流程")
    public Map<String, Object> formalProcessInstance(String V_PERCODE, String processInstanceId) {
        Map<String, Object> operator = baseService.getOperator(V_PERCODE);
        activitiService.formalProcessInstance(processInstanceId, operator);
        return BaseUtils.success();
    }

    @GetMapping("loadTask")
    @Log(title = "流程管理", operateType = "查询任务")
    public Map<String, Object> loadTask(String V_PERCODE, String taskId) {
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> operator = baseService.getOperator(V_PERCODE);
        Task task = activitiService.loadTask(taskId, operator);
        data.put(DATA, task);
        return BaseUtils.success(data);
    }

    @GetMapping("loadFirstTask")
    @Log(title = "流程管理", operateType = "查询流程第一个待办任务")
    public Map<String, Object> loadFirstTask(String V_PERCODE, String processInstanceId) {
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> operator = baseService.getOperator(V_PERCODE);
        Task task = activitiService.loadFirstTask(processInstanceId, operator);
        data.put(DATA, task);
        return BaseUtils.success(data);
    }

    @GetMapping("selectTask")
    @Log(title = "流程管理", operateType = "查询任务")
    public Map<String, Object> selectTask(String V_PERCODE, String ORG_CODE_, String CONTRACT_NAME_, Integer current, Integer pageSize) {
        Map<String, Object> operator = baseService.getOperator(V_PERCODE);
        TaskFilter taskFilter = new TaskFilter();
        taskFilter.setAssignee("EMP[" + (String) operator.get("EMP_CODE_") + "]");
        taskFilter.setSuspensionState(1);
        //待办任务  新增查询条件
        Map<String, Object> processVariables = new HashMap();
        processVariables.put("ORG_CODE_", ORG_CODE_);
        processVariables.put("CONTRACT_NAME_", CONTRACT_NAME_);
        taskFilter.setProcessVariables(processVariables);
        //待办任务  按照处理人收到文件时间倒序排列
        BaseOrder orderDto = new BaseOrder();
        orderDto.setPropertyName("taskCreateTime");
        orderDto.setAsc(false);
        List<Task> taskList = activitiService.selectTask(taskFilter, orderDto, current, pageSize, 5, operator);
        long total = activitiService.countTask(taskFilter, operator);
        return BaseUtils.success(taskList, total);
    }

    @GetMapping("selectAllRunningTask")
    @Log(title = "流程管理", operateType = "查询待办任务")
    public Map<String, Object> selectAllRunningTask(String V_PERCODE, String CODE_, String TITLE_, String EMP_CODE_, Boolean expired, Integer current, Integer pageSize) {
        Map<String, Object> operator = baseService.getOperator(V_PERCODE);
        List<Task> taskList = activitiService.selectAllRunningTask(CODE_, TITLE_, EMP_CODE_, expired, current, pageSize, operator);
        long total = activitiService.countAllRunningTask(CODE_, TITLE_, EMP_CODE_, expired, operator);
        return BaseUtils.success(taskList, total);
    }

    @GetMapping("countTask")
    @Log(title = "流程管理", operateType = "统计任务个数")
    public Map<String, Object> countTask(String V_PERCODE) {
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> operator = baseService.getOperator(V_PERCODE);
        TaskFilter taskFilter = new TaskFilter();
        taskFilter.setAssignee("EMP[" + (String) operator.get("EMP_CODE_") + "]");
        taskFilter.setSuspensionState(1);
        long taskCount = activitiService.countTask(taskFilter, operator);
        data.put(DATA, taskCount);
        return BaseUtils.success(data);
    }

    @GetMapping("selectAllTask")
    @Log(title = "流程管理", operateType = "查询任务")
    public Map<String, Object> selectAllTask(String V_PERCODE, String EMP_CODE_, Integer expired, ProcessVariables processVariables, Integer current, Integer pageSize) {
        Map<String, Object> operator = baseService.getOperator(V_PERCODE);
        TaskFilter taskFilter = new TaskFilter();
        taskFilter.setSuspensionState(1);
        if (StringUtils.isNotEmpty(EMP_CODE_)) {
            taskFilter.setAssigneeLike("EMP[" + EMP_CODE_ + "]%");
        }
        if (expired != null) {
            if (expired == 1) {
                taskFilter.setTaskDueBefore(new Date());
            } else if (expired == 0) {
                taskFilter.setTaskDueAfter(new Date());
            }
        }
        taskFilter.setProcessVariables(processVariables.getProcessVariables());

        List<Task> taskList = activitiService.selectTask(taskFilter, null, current, pageSize, 1, operator);
        long total = activitiService.countTask(taskFilter, operator);
        return BaseUtils.success(taskList, total);
    }

    //根据人员编码查询合同待办
    @GetMapping("selectAllInContractTask")
    @Log(title = "流程管理", operateType = "查询合同待办")
    public Map<String, Object> selectAllInContractTask(String V_PERCODE, String V_SPONSOR, String V_CONTRACTOR, String V_MAJORID, @DateTimeFormat(pattern = "yyyy-MM-dd") Date V_BEGIN_DATE, @DateTimeFormat(pattern = "yyyy-MM-dd") Date V_END_DATE, Integer current, Integer pageSize) {
        Map<String, Object> data = new HashMap<>();
        V_END_DATE = BaseUtils.addOneDay(V_END_DATE);
        Map<String, Object> operator = baseService.getOperator(V_PERCODE);
        //待办任务  新增查询条件
        Map<String, Object> processVariables = new HashMap();
        processVariables.put("V_SPONSORNAME", V_SPONSOR);
        processVariables.put("V_CONTRACTORNAME", V_CONTRACTOR);
        processVariables.put("V_MAJORID", V_MAJORID);

        data = getAllTaskResult(null, V_PERCODE, operator, processVariables, V_BEGIN_DATE, V_END_DATE, current, pageSize);
        //带详细数据信息的结果
        List<Task> taskList = (List<Task>) data.get("task");
        //返回数据集合
        List taskResult = new ArrayList();
        //只返回合同的待审批数据
        for (int i = 0; i < taskList.size(); i++) {
            if ("BidNegotiation".equals(taskList.get(i).getProcessDefinitionKey())) {  //TODO processDefinitionKey
                taskResult.add(taskList.get(i));
            }
        }
        data.remove("task");
        data.put(DATA, taskResult);
        return BaseUtils.success(data);
    }

    //查询待办任务结果处理
    private Map<String, Object> getAllTaskResult(TaskFilter taskFilter, String EMP_CODE_, Map<String, Object> operator, Map<String, Object> processVariables, Date V_BEGIN_DATE, Date V_END_DATE, Integer current, Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        if (taskFilter == null) {
            taskFilter = new TaskFilter();
            if (StringUtils.isNotEmpty(EMP_CODE_)) {
                taskFilter.setAssigneeLike("EMP[" + EMP_CODE_ + "]%");
            }
            taskFilter.setSuspensionState(1);
            taskFilter.setProcessVariables(processVariables);
        }
        //待办任务  按照处理人收到文件时间倒序排列
        BaseOrder orderDto = new BaseOrder();
        orderDto.setPropertyName("taskCreateTime");
        orderDto.setAsc(false);
        List<Task> taskList = activitiService.selectTask(taskFilter, orderDto, current, pageSize, 5, operator);
        long total = activitiService.countTask(taskFilter, operator);
        //获取待办任务里id集合
        Map<String, Integer> resultMap = new HashMap<>();//返回值计划
        List<Map<String, Object>> task = new ArrayList<>();//数据计划
        List<String> str = new ArrayList<>();//表id
        str.add("I_ID");    //TODO 合同id
        List<Task> tempList = new ArrayList<>();
        if (taskList != null && taskList.size() > 0) {
            for (int i = 0; i < taskList.size(); i++) {
                //待办任务里id为key,角标为值
                resultMap.put(taskList.get(i).getProcessVariables().get("bizId").toString(), i);

                if ("BidNegotiation".equals(taskList.get(i).getProcessDefinitionKey())) { //TODO processDefinitionKey
                    List<Map<String, Object>> contractInTask = contractService.selectContractInTask(taskList.get(i).getProcessVariables().get("bizId").toString(), V_BEGIN_DATE, V_END_DATE);
                    for (int j = 0; j < contractInTask.size(); j++) {
                        task.add(contractInTask.get(j));
                        if (taskList.get(i).getProcessVariables().get("bizId").toString().equals(contractInTask.get(j).get("I_ID"))) {
                            tempList.add(taskList.get(i));
                        }
                    }
                }

            }
            for (int i = 0; i < task.size(); i++) {
                for (String strKey : str) {
                    if (task.get(i).get(strKey) != null) {
                        int j = resultMap.get(task.get(i).get(strKey));
                        taskList.get(j).getProcessVariables().putAll(task.get(i));
                    }
                }
            }
        }

        result.put("task", tempList);
        result.put("total", tempList.size());
        return result;
    }

    @GetMapping("selectHistoricTask")
    @Log(title = "流程管理", operateType = "查询已完成任务")
    public Map<String, Object> selectHistoricTask(String V_PERCODE, Integer current, Integer pageSize) {
        Map<String, Object> operator = baseService.getOperator(V_PERCODE);
        String assignee = "EMP[" + (String) operator.get("EMP_CODE_") + "]";
        List<Task> taskList = activitiService.selectHistoricTask(assignee, current, pageSize, operator);
        long total = activitiService.countHistoricTask(assignee, operator);
        return BaseUtils.success(taskList, total);
    }

    // 驳回
    @PostMapping("completeToTask")
    @Log(title = "驳回", operateType = "驳回")
    public Map<String, Object> completeToTask(String V_PERCODE, String taskId, String taskDefinitionKey, String assignee) {
        Map<String, Object> operator = baseService.getOperator(V_PERCODE);
        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put("assignee", assignee);
        activitiService.completeToTask(taskId, taskDefinitionKey, processVariables, true, operator);
        return BaseUtils.success();
    }

    // 回退
    @PostMapping("withdrawTask")
    @Log(title = "流程管理", operateType = "回退")
    public Map<String, Object> withdrawTask(String V_PERCODE, String taskId) {
        Map<String, Object> operator = baseService.getOperator(V_PERCODE);
        activitiService.withdrawTask(taskId, null, operator);
        return BaseUtils.success();
    }

    // 提交
    @PostMapping("completeTask")
    @Log(title = "流程管理", operateType = "提交流程")
    public Map<String, Object> completeTask(String V_PERCODE, String taskId, String approval, String step, String assignee, @RequestParam(value = "assigneeList", required = false) ArrayList<String> assigneeList) {
        Map<String, Object> operator = baseService.getOperator(V_PERCODE);
        if (StringUtils.isNotBlank(step)) {
            Task task = activitiService.loadTask(taskId, operator);
            Map<String, Object> executionVariables = new HashMap<>();
            executionVariables.put("step", step);
            activitiService.setExecutionVariables(task.getExecutionId(), executionVariables, operator);
        }
        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put("approval", approval);
        if (StringUtils.isNotEmpty(assignee)) {
            processVariables.put("assignee", assignee);
        }
        if (assigneeList != null) {// 并签期间，assigneeList不能改变
            processVariables.put("assigneeList", assigneeList);
        }
        activitiService.completeTask(taskId, processVariables, operator);
        return BaseUtils.success();
    }

    @PostMapping("completeTaskWithoutAssignee")
    @Log(title = "流程管理", operateType = "完成任务")
    public Map<String, Object> completeTaskWithoutAssignee(String V_PERCODE, String taskId, String approval, String step) {
        Map<String, Object> operator = baseService.getOperator(V_PERCODE);
        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put("approval", approval);
        if (StringUtils.isNotBlank(step)) {
            processVariables.put("step", step);
        }
        activitiService.completeTask(taskId, processVariables, operator);
        return BaseUtils.success();
    }

    // 获得下一个用户任务节点
    @GetMapping("loadNextUserTaskDefinition")
    @Log(title = "流程管理", operateType = "查询下个任务定义")
    public Map<String, Object> loadNextUserTaskDefinition(String V_PERCODE, String taskId, String approval) {
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> operator = baseService.getOperator(V_PERCODE);
        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put("approval", approval);
        UserTaskDefinition nextUserTaskDefinition = activitiService.loadNextUserTaskDefinition(taskId, processVariables, operator);
        data.put(DATA, nextUserTaskDefinition);
        return BaseUtils.success(data);
    }

    // 获得上一个用户任务节点
    @GetMapping("loadPreviousUserTaskDefinition")
    @Log(title = "流程管理", operateType = "查询上个任务定义")
    public Map<String, Object> loadPreviousUserTaskDefinition(String V_PERCODE, String taskId) {
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> operator = baseService.getOperator(V_PERCODE);
        Map<String, Object> processVariables = new HashMap<>();
        UserTaskDefinition previousUserTaskDefinition = activitiService.loadPreviousUserTaskDefinition(taskId, processVariables, operator);
        data.put(DATA, previousUserTaskDefinition);
        return BaseUtils.success(data);
    }

    // 流程统计
    @GetMapping("selectProcessDefinition")
    @Log(title = "流程管理", operateType = "查询流程定义")
    public Map<String, Object> selectProcessDefinition(String V_PERCODE, Integer threshold) {
        Map<String, Object> operator = baseService.getOperator(V_PERCODE);
        List<ProcessDefinition> processDefinitionList = activitiService.selectProcessDefinition(threshold, operator);
        return BaseUtils.success(processDefinitionList);
    }

    // 查询流程定义KEY
    @GetMapping("selectProcessDefinitionKey")
    @Log(title = "流程管理", operateType = "查询流程定义KEY")
    public Map<String, Object> selectProcessDefinitionKey(String V_PERCODE) {
        Map<String, Object> operator = baseService.getOperator(V_PERCODE);
        List<Map<String, Object>> processDefinitionKeyList = activitiService.selectProcessDefinitionKey(operator);
        return BaseUtils.success(processDefinitionKeyList);
    }

    // 部署新流程
    @PostMapping("insertProcessDefinitionOld")
    @Log(title = "流程管理", operateType = "部署新流程")
    public Map<String, Object> insertProcessDefinitionOld(String V_PERCODE, String category, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        ProcessDefinition processDefinition = null;
        try {
            Map<String, Object> operator = baseService.getOperator(V_PERCODE);
            CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
            if (multipartResolver.isMultipart(request)) {
                MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
                Iterator<String> fileNameIterator = multiRequest.getFileNames();
                List<String> fileNameList = new ArrayList<>();
                List<InputStream> inputStreamList = new ArrayList<>();
                while (fileNameIterator.hasNext()) {
                    MultipartFile file = multiRequest.getFile(fileNameIterator.next());
                    fileNameList.add(file.getOriginalFilename());
                    inputStreamList.add(file.getInputStream());
                }
                processDefinition = activitiService.insertProcessDefinition(category, fileNameList, inputStreamList, operator);
                for (InputStream inputStream : inputStreamList) {
                    inputStream.close();
                }
                if (processDefinition == null) {
                    throw new RuntimeException("未新增任何数据");
                }
            }
            result.put("processDefinition", processDefinition);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof DuplicateKeyException) {
                return BaseUtils.failed("代码重复");
            } else {
                return BaseUtils.failed(e.getMessage());
            }
        }
        return BaseUtils.success(result);
    }

    // 部署新流程
    @PostMapping("insertProcessDefinition")
    @Log(title = "流程管理", operateType = "部署新流程")
    public Map<String, Object> insertProcessDefinition(String category, MultipartFile[] multipartFiles) throws IOException {
        Map<String, Object> data = new HashMap<>();
        ProcessDefinition processDefinition = activitiService.insertProcessDefinition(category, multipartFiles);
        data.put(DATA, processDefinition);
        return BaseUtils.success(data);
    }

    // 加载流程定义图（流程定义管理）
    @GetMapping("loadProcessDefinitionDiagram")
    @Log(title = "流程管理", operateType = "加载流程定义图")
    public void loadProcessDefinitionDiagram(String V_PERCODE, String processDefinitionKey, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> operator = baseService.getOperator(V_PERCODE);
        InputStream inputStream = activitiService.loadProcessDefinitionDiagram(processDefinitionKey, operator);
        if (inputStream == null) {
            return;
        }
        BaseUtils.download(inputStream, "流程定义图", request, response);
    }

    // 加载流程图（流转图）
    @GetMapping("loadProcessInstanceDiagram")
    @Log(title = "流程管理", operateType = "加载流程图")
    public void loadProcessInstanceDiagram(String V_PERCODE, String processInstanceId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> operator = baseService.getOperator(V_PERCODE);
        InputStream inputStream = activitiService.loadProcessInstanceDiagram(processInstanceId, operator);
        if (inputStream == null) {
            return;
        }
        BaseUtils.download(inputStream, "流程图", request, response);
    }

    // 获得第一个用户任务节点
    @GetMapping("selectFirstTaskProcCandidate")
    @Log(title = "流程管理", operateType = "查询最新流程下的第一步候选人列表")
    public Map<String, Object> selectFirstTaskProcCandidate(String V_PERCODE, String processDefinitionKey, String taskDefinitionKey, String orgCode, String approval, String step) {
        Map<String, Object> operator = baseService.getOperator(V_PERCODE);
        Map<String, Object> processVariables = new HashMap<>();
        processVariables.put("approval", approval);
        if (StringUtils.isNotBlank(orgCode)) {
            processVariables.put("initOrgCode", orgCode);
        }
        if (StringUtils.isNotBlank(step)) {
            processVariables.put("step", step);
        }
        List<Map<String, Object>> candidateList = activitiService.selectFirstTaskProcCandidate(processDefinitionKey, taskDefinitionKey, processVariables, operator);
        return BaseUtils.success(candidateList);
    }

    //查询流程定义图,根据流程部署ID
    @GetMapping("loadProcessDefinitionDiagramByDeploymentId")
    @Log(title = "流程管理", operateType = "查询流程定义图,根据流程部署ID")
    public void loadProcessDefinitionDiagramByDeploymentId(String deploymentId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        InputStream inputStream = activitiService.loadProcessDefinitionDiagramByDeploymentId(deploymentId);
        if (inputStream == null) {
            return;
        }
        BaseUtils.download(inputStream, "流程图", request, response);
    }
}