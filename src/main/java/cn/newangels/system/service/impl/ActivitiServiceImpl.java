package cn.newangels.system.service.impl;

import cn.newangels.common.base.BaseUtils;
import cn.newangels.system.base.BaseOrder;
import cn.newangels.system.dto.Task;
import cn.newangels.system.dto.*;
import cn.newangels.system.service.ActivitiService;
import cn.newangels.system.service.BaseService;
import cn.newangels.system.service.PersonService;
import de.odysseus.el.ExpressionFactoryImpl;
import de.odysseus.el.util.SimpleContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.*;
import org.activiti.engine.*;
import org.activiti.engine.history.*;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.*;

@Service
@Slf4j
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
@RequiredArgsConstructor
public class ActivitiServiceImpl implements ActivitiService {

    private static Map<String, Map<String, Object>> processDefinitionMap;
    private static Map<String, ProcessDefinitionEntity> processDefinitionEntityMap;
    private static Map<String, BpmnModel> bpmnModelMap;
    private static Map<String, ByteArrayOutputStream> processDefinitionDiagramMap;

    private final RepositoryService repositoryService;
    private final HistoryService historyService;
    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final IdentityService identityService;
    private final TransactionTemplate activitiTransactionTemplate;
    private final JdbcTemplate activitiJdbcTemplate;
    private final PersonService personService;
    private final BaseService baseService;

    @PostConstruct
    @Override
    public void refreshProcessDefinition() {
        String sql = "select * from ACT_RE_PROCDEF";
        List<Map<String, Object>> processDefinitionList = activitiJdbcTemplate.queryForList(sql);
        processDefinitionMap = new HashMap<String, Map<String, Object>>();
        processDefinitionEntityMap = new HashMap<String, ProcessDefinitionEntity>();
        bpmnModelMap = new HashMap<String, BpmnModel>();
        processDefinitionDiagramMap = new HashMap<String, ByteArrayOutputStream>();
        for (Map<String, Object> processDefinition : processDefinitionList) {
            try {
                processDefinitionMap.put((String) processDefinition.get("ID_"), processDefinition);
                processDefinitionEntityMap.put((String) processDefinition.get("ID_"), (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService).getDeployedProcessDefinition((String) processDefinition.get("ID_")));
                bpmnModelMap.put((String) processDefinition.get("ID_"), repositoryService.getBpmnModel((String) processDefinition.get("ID_")));

                InputStream inputStream = repositoryService.getResourceAsStream((String) processDefinition.get("DEPLOYMENT_ID_"), (String) processDefinition.get("DGRM_RESOURCE_NAME_"));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[65535];
                int len;
                try {
                    while ((len = inputStream.read(buffer)) > -1) {
                        baos.write(buffer, 0, len);
                    }
                    baos.flush();
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                processDefinitionDiagramMap.put((String) processDefinition.get("ID_"), baos);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Map<String, Object> loadProcessDefinition(String processDefinitionId) {
        return processDefinitionMap.get(processDefinitionId);
    }

    @Override
    public Map<String, Object> loadProcessDefinitionByProcessDefinitionKey(String processDefinitionKey) {
        Map<String, Object> result = null;

        for (Map<String, Object> processDefinition : processDefinitionMap.values()) {
            if (processDefinitionKey.equals(processDefinition.get("KEY_"))) {
                if (result == null) {
                    result = processDefinition;
                } else if (((BigDecimal) processDefinition.get("VERSION_")).intValue() > ((BigDecimal) result.get("VERSION_")).intValue()) {
                    result = processDefinition;
                }
            }
        }

        return result;
    }

    @Override
    public ProcessDefinition insertProcessDefinition(String category, List<String> fileNameList, List<InputStream> inputStreamList, Map<String, Object> operator) {
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().category(category);
        for (int i = 0; i < fileNameList.size(); i++) {
            deploymentBuilder.addInputStream(fileNameList.get(i), inputStreamList.get(i));
        }
        Deployment deployment = deploymentBuilder.deploy();

        refreshProcessDefinition();

        Map<String, Object> map = null;
        for (Map.Entry<String, Map<String, Object>> entry : processDefinitionMap.entrySet()) {
            if (entry.getValue().get("DEPLOYMENT_ID_").equals(deployment.getId())) {
                map = entry.getValue();
                break;
            }
        }
        ProcessDefinition processDefinition = new ProcessDefinition();
        processDefinition.setProcessDefinitionId((String) map.get(("ID_")));
        processDefinition.setProcessDefinitionName((String) map.get(("NAME_")));
        processDefinition.setProcessDefinitionKey((String) map.get(("KEY_")));
        processDefinition.setCategory((String) map.get(("CATEGORY_")));
        processDefinition.setVersion(((BigDecimal) map.get("VERSION_")).intValue());
        processDefinition.setSuspensionState(((BigDecimal) map.get("SUSPENSION_STATE_")).intValue());
        processDefinition.setDeploymentId((String) (map.get("DEPLOYMENT_ID_")));

        return processDefinition;
    }

    @Override
    public ProcessDefinition insertProcessDefinition(String category, MultipartFile[] multipartFiles) throws IOException {
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().category(category);
        String fileName;
        InputStream fileStream;
        for (MultipartFile multipartFile : multipartFiles) {
            fileName = multipartFile.getOriginalFilename();
            fileStream = multipartFile.getInputStream();
            deploymentBuilder.addInputStream(fileName, fileStream);
        }
        Deployment deployment = deploymentBuilder.deploy();

        refreshProcessDefinition();

        Map<String, Object> map = null;
        for (Map.Entry<String, Map<String, Object>> entry : processDefinitionMap.entrySet()) {
            if (entry.getValue().get("DEPLOYMENT_ID_").equals(deployment.getId())) {
                map = entry.getValue();
                break;
            }
        }
        ProcessDefinition processDefinition = new ProcessDefinition();
        processDefinition.setProcessDefinitionId((String) map.get(("ID_")));
        processDefinition.setProcessDefinitionName((String) map.get(("NAME_")));
        processDefinition.setProcessDefinitionKey((String) map.get(("KEY_")));
        processDefinition.setCategory((String) map.get(("CATEGORY_")));
        processDefinition.setVersion(((BigDecimal) map.get("VERSION_")).intValue());
        processDefinition.setSuspensionState(((BigDecimal) map.get("SUSPENSION_STATE_")).intValue());
        processDefinition.setDeploymentId((String) (map.get("DEPLOYMENT_ID_")));

        return processDefinition;
    }

    @Override
    public ProcessInstance startProcessInstance(String processDefinitionKey, String businessKey, Map<String, Object> processVariables, Map<String, Object> operator) {
        identityService.setAuthenticatedUserId((String) operator.get("EMP_CODE_"));
        org.activiti.engine.runtime.ProcessInstance _processInstance;

        try {
            _processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey, processVariables);
            _processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(_processInstance.getProcessInstanceId()).singleResult();
        } catch (Exception e) {
            //手动回滚
            //TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.info(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        ProcessInstance processInstance = new ProcessInstance();
        processInstance.setProcessInstanceId(_processInstance.getId());
        processInstance.setProcessDefinitionId(_processInstance.getProcessDefinitionId());
        processInstance.setProcessDefinitionName(_processInstance.getProcessDefinitionName());
        processInstance.setProcessDefinitionKey(_processInstance.getProcessDefinitionKey());
        processInstance.setBusinessKey(businessKey);
        processInstance.setStartUser((String) operator.get("EMP_CODE_"));
        processInstance.setStatus(ProcessInstance.STATUS_RUNNING);
        processInstance.setProcessVariables(processVariables);

        return processInstance;
    }

    @Override
    public ProcessInstance loadProcessInstance(String processInstanceId, Map<String, Object> operator) {
        ProcessInstanceFilter processInstanceFilter = new ProcessInstanceFilter();
        processInstanceFilter.setProcessInstanceId(processInstanceId);
        List<ProcessInstance> processInstanceList = selectProcessInstance(processInstanceFilter, null, 1, -1, 9, operator);
        if (processInstanceList.size() == 1) {
            return processInstanceList.get(0);
        }

        return null;
    }

    @Override
    public List<ProcessInstance> selectProcessInstance(ProcessInstanceFilter processInstanceFilter, BaseOrder orderDto, Integer page, Integer limit, int threshold, Map<String, Object> operator) {
        HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery();
        buildCriteriaProcessInstance(query, processInstanceFilter, operator);

        if (orderDto == null) {
            query.orderByProcessInstanceStartTime().desc();
        }
        if (orderDto != null && StringUtils.isNotEmpty(orderDto.getPropertyName())) {
            if (orderDto.getPropertyName().equals("processInstanceId")) {
                query.orderByProcessInstanceId();
            }
            if (orderDto.getPropertyName().equals("processInstanceBusinessKey")) {
                query.orderByProcessInstanceBusinessKey();
            }
            if (orderDto.getPropertyName().equals("processDefinitionId")) {
                query.orderByProcessDefinitionId();
            }
            if (orderDto.getPropertyName().equals("processInstanceStartTime")) {
                query.orderByProcessInstanceStartTime();
            }
            if (orderDto.getPropertyName().equals("processInstanceEndTime")) {
                query.orderByProcessInstanceEndTime();
            }
            if (orderDto.getPropertyName().equals("processInstanceDuration")) {
                query.orderByProcessInstanceDuration();
            }
            if (orderDto.isAsc() == true) {
                query.asc();
            } else {
                query.desc();
            }
        }

        List<HistoricProcessInstance> _processInstanceList;
        try {
            if (page != null && limit != null && limit != -1) {
                _processInstanceList = query.listPage((page - 1) * limit, limit);
            } else {
                _processInstanceList = query.list();
            }
        } catch (Exception e) {
            log.info(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        List<ProcessInstance> processInstanceList = new ArrayList<ProcessInstance>();
        for (HistoricProcessInstance _processInstance : _processInstanceList) {
            ProcessInstance processInstance = new ProcessInstance();

            processInstance.setProcessInstanceId(_processInstance.getId());
            processInstance.setProcessDefinitionId(_processInstance.getProcessDefinitionId());
            Map<String, Object> processDefinition = loadProcessDefinition(_processInstance.getProcessDefinitionId());
            if (processDefinition != null) {
                processInstance.setProcessDefinitionName((String) processDefinition.get("NAME_"));
                processInstance.setProcessDefinitionKey((String) processDefinition.get("KEY_"));
            }
            processInstance.setBusinessKey(_processInstance.getBusinessKey());
            processInstance.setStartActivityId(_processInstance.getStartActivityId());
            processInstance.setSuperProcessInstanceId(_processInstance.getSuperProcessInstanceId());
            processInstance.setStartUser(_processInstance.getStartUserId());
            processInstance.setStartTime(_processInstance.getStartTime());
            processInstance.setEndTime(_processInstance.getEndTime());
            processInstance.setDurationInMillis(_processInstance.getDurationInMillis());
            processInstance.setDeleteReason(_processInstance.getDeleteReason());
            if (_processInstance.getEndTime() == null) {
                processInstance.setStatus(ProcessInstance.STATUS_RUNNING);
            } else {
                processInstance.setStatus(ProcessInstance.STATUS_FINISHED);
            }
            Map<String, Object> processVariables = new HashMap<String, Object>();
            List<HistoricVariableInstance> historicVariableInstanceList = historyService.createHistoricVariableInstanceQuery().processInstanceId(_processInstance.getId()).list();
            for (HistoricVariableInstance historicVariableInstance : historicVariableInstanceList) {
                processVariables.put(historicVariableInstance.getVariableName(), historicVariableInstance.getValue());
            }
            processInstance.setProcessVariables(processVariables);

            processInstanceList.add(processInstance);
        }

        return processInstanceList;
    }

    @Override
    public long countProcessInstance(ProcessInstanceFilter processInstanceFilter, Map<String, Object> operator) {
        HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery();
        buildCriteriaProcessInstance(query, processInstanceFilter, operator);

        return query.count();
    }

    private HistoricProcessInstanceQuery buildCriteriaProcessInstance(HistoricProcessInstanceQuery query, ProcessInstanceFilter processInstanceFilter, Map<String, Object> operator) {

        if (StringUtils.isNotEmpty(processInstanceFilter.getProcessInstanceId())) {
            query = query.processInstanceId(processInstanceFilter.getProcessInstanceId());
        }
        if (StringUtils.isNotEmpty(processInstanceFilter.getProcessDefinitionId())) {
            query = query.processDefinitionId(processInstanceFilter.getProcessDefinitionId());
        }
        if (StringUtils.isNotEmpty(processInstanceFilter.getProcessDefinitionKey())) {
            query = query.processDefinitionKey(processInstanceFilter.getProcessDefinitionKey());
        }
        if (StringUtils.isNotEmpty(processInstanceFilter.getBusinessKey())) {
            query = query.processInstanceBusinessKey(processInstanceFilter.getBusinessKey());
        }
        if (StringUtils.isNotEmpty(processInstanceFilter.getStartUser())) {
            query = query.startedBy(processInstanceFilter.getStartUser());
        }
        if (StringUtils.isNotEmpty(processInstanceFilter.getInvolvedUserId())) {
            query = query.involvedUser(processInstanceFilter.getInvolvedUserId());
        }
        if (processInstanceFilter.getStartedBefore() != null) {
            query = query.startedBefore(processInstanceFilter.getStartedBefore());
        }
        if (processInstanceFilter.getStartedAfter() != null) {
            query = query.startedAfter(processInstanceFilter.getStartedAfter());
        }
        if (processInstanceFilter.getFinishedBefore() != null) {
            query = query.finishedBefore(processInstanceFilter.getFinishedBefore());
        }
        if (processInstanceFilter.getFinishedAfter() != null) {
            query = query.finishedAfter(processInstanceFilter.getFinishedAfter());
        }
        if (ProcessInstanceFilter.TRUE.equals(processInstanceFilter.getFinished())) {
            query = query.finished();
        }
        if (ProcessInstanceFilter.FALSE.equals(processInstanceFilter.getFinished())) {
            query = query.unfinished();
        }
        if (processInstanceFilter.getProcessVariables() != null) {
            for (Map.Entry<String, Object> entry : processInstanceFilter.getProcessVariables().entrySet()) {
                if (StringUtils.isNotEmpty((String) entry.getValue())) {
                    if (entry.getKey().equals("CODE_") || entry.getKey().equals("TITLE_")) {// 公文专用，文号，标题模糊查询
                        query = query.variableValueLike(entry.getKey(), "%" + (String) entry.getValue() + "%");
                    } else {
                        query = query.variableValueEquals(entry.getKey(), (String) entry.getValue());
                    }
                }
            }
        }

        return query;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ProcessInstance> selectInvolvedProcessInstance(ProcessInstanceFilter processInstanceFilter, BaseOrder orderDto, Integer page, Integer limit, int threshold, Map<String, Object> operator) {
        List<Object> result = buildCriteriaInvolvedProcessInstance(false, processInstanceFilter, orderDto, operator);
        String sql = (String) result.get(0);
        Map<String, Object> paramMap = (Map<String, Object>) result.get(1);

        if (page != null && limit != null && limit > 0) {
            int start = (page - 1) * limit + 1;
            int end = page * limit;
            sql = "select * from (select fulltable.*, ROWNUM RN from (" + sql + ") fulltable where ROWNUM <= " + end + ") where RN >= " + start;
        }

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(activitiJdbcTemplate);
        List<Map<String, Object>> _processInstanceList = namedParameterJdbcTemplate.queryForList(sql, paramMap);

        List<ProcessInstance> processInstanceList = new ArrayList<ProcessInstance>();
        for (Map<String, Object> _processInstance : _processInstanceList) {
            ProcessInstance processInstance = new ProcessInstance();

            processInstance.setProcessInstanceId((String) _processInstance.get("PROC_INST_ID_"));
            processInstance.setProcessDefinitionId((String) _processInstance.get("PROC_DEF_ID_"));
            Map<String, Object> processDefinition = loadProcessDefinition((String) _processInstance.get("PROC_DEF_ID_"));
            if (processDefinition != null) {
                processInstance.setProcessDefinitionName((String) processDefinition.get("NAME_"));
                processInstance.setProcessDefinitionKey((String) processDefinition.get("KEY_"));
            }
            processInstance.setBusinessKey((String) _processInstance.get("BUSINESS_KEY_"));
            processInstance.setSuperProcessInstanceId((String) _processInstance.get("SUPER_PROCESS_INSTANCE_ID_"));
            processInstance.setStartUser((String) _processInstance.get("START_USER_ID_"));
            processInstance.setStartTime((Date) _processInstance.get("START_TIME_"));
            processInstance.setEndTime((Date) _processInstance.get("END_TIME_"));
            processInstance.setDeleteReason((String) _processInstance.get("DELETE_REASON_"));
            if (_processInstance.get("END_TIME_") == null) {
                processInstance.setStatus(ProcessInstance.STATUS_RUNNING);
            } else {
                processInstance.setStatus(ProcessInstance.STATUS_FINISHED);
            }
            Map<String, Object> processVariables = new HashMap<String, Object>();
            List<HistoricVariableInstance> historicVariableInstanceList = historyService.createHistoricVariableInstanceQuery().processInstanceId((String) _processInstance.get("PROC_INST_ID_")).list();
            for (HistoricVariableInstance historicVariableInstance : historicVariableInstanceList) {
                processVariables.put(historicVariableInstance.getVariableName(), historicVariableInstance.getValue());
            }
            processInstance.setProcessVariables(processVariables);

            processInstanceList.add(processInstance);
        }

        return processInstanceList;
    }

    @SuppressWarnings("unchecked")
    @Override
    public long countInvolvedProcessInstance(ProcessInstanceFilter processInstanceFilter, Map<String, Object> operator) {
        List<Object> result = buildCriteriaInvolvedProcessInstance(true, processInstanceFilter, null, operator);
        String sql = (String) result.get(0);
        Map<String, Object> paramMap = (Map<String, Object>) result.get(1);

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(activitiJdbcTemplate);
        return namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
    }

    private List<Object> buildCriteriaInvolvedProcessInstance(boolean count, ProcessInstanceFilter processInstanceFilter, BaseOrder orderDto, Map<String, Object> operator) {
        String sql;
        if (count) {
            sql = "select count(*) from ACT_HI_PROCINST where PROC_INST_ID_ in (select distinct (hp.PROC_INST_ID_) from ACT_HI_PROCINST hp inner join ACT_HI_TASKINST ht on ht.PROC_INST_ID_ = hp.PROC_INST_ID_ inner join ACT_RE_PROCDEF rp on rp.ID_ = hp.PROC_DEF_ID_  where ht.END_TIME_ is not null";
        } else {
            sql = "select * from ACT_HI_PROCINST where PROC_INST_ID_ in (select distinct (hp.PROC_INST_ID_) from ACT_HI_PROCINST hp inner join ACT_HI_TASKINST ht on ht.PROC_INST_ID_ = hp.PROC_INST_ID_ inner join ACT_RE_PROCDEF rp on rp.ID_ = hp.PROC_DEF_ID_ where ht.END_TIME_ is not null";
        }

        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (StringUtils.isNotEmpty(processInstanceFilter.getInvolvedUserId())) {
            sql += " and ht.ASSIGNEE_ = :ASSIGNEE_";
            paramMap.put("ASSIGNEE_", processInstanceFilter.getInvolvedUserId());
        }

        //时间添加
        if (processInstanceFilter.getStartedBefore() != null) {
            sql += "  and hp.END_TIME_ < :END_TIME_";
            paramMap.put("END_TIME_", processInstanceFilter.getStartedBefore());
        }

        if (processInstanceFilter.getStartedAfter() != null) {
            sql += "  and hp.START_TIME_ > :START_TIME_";
            paramMap.put("START_TIME_", processInstanceFilter.getStartedAfter());
        }

        if (StringUtils.isNotEmpty(processInstanceFilter.getProcessDefinitionId())) {
            sql += "  and hp.PROC_DEF_ID_ = :PROC_DEF_ID_";
            paramMap.put("PROC_DEF_ID_", processInstanceFilter.getProcessDefinitionId());
        }
        if (StringUtils.isNotEmpty(processInstanceFilter.getProcessDefinitionKey())) {
            sql += "  and rp.KEY_ = :KEY_";
            paramMap.put("KEY_", processInstanceFilter.getProcessDefinitionKey());
        }
        if (processInstanceFilter.getProcessVariables() != null) {
            sql += " and hp.PROC_INST_ID_ in (select PROC_INST_ID_ from ACT_HI_VARINST group by PROC_INST_ID_ having 1 = 1";
            int i = 0;
            for (Map.Entry<String, Object> entry : processInstanceFilter.getProcessVariables().entrySet()) {
                if (StringUtils.isNotEmpty((String) entry.getValue())) {
                    sql += " and MAX(DECODE(NAME_, '" + entry.getKey() + "', TEXT_)) like '%' || :TEXT" + i + " || '%'";
                    paramMap.put("TEXT" + i, entry.getValue());
                }
                i++;
            }
            sql += ")";
        }
        sql += ")";
        if (ProcessInstanceFilter.TRUE.equals(processInstanceFilter.getFinished())) {
            sql += " and END_TIME_ is not null";
        }
        if (ProcessInstanceFilter.FALSE.equals(processInstanceFilter.getFinished())) {
            sql += " and END_TIME_ is null";
        }

        if (!count) {
            if (orderDto == null) {
                sql += " order by START_TIME_ desc";
            }
            if (orderDto != null && StringUtils.isNotEmpty(orderDto.getPropertyName())) {
                if (orderDto.getPropertyName().equals("processDefinitionId")) {
                    sql += " order by PROC_DEF_ID_";
                }
                if (orderDto.isAsc() == true) {
                    sql += " asc";
                } else {
                    sql += " desc";
                }
            }
        }

        List<Object> result = new ArrayList<Object>();
        result.add(sql);
        result.add(paramMap);

        return result;
    }

    @Override
    public List<ProcessInstance> selectActiveProcessInstance(ProcessInstanceFilter processInstanceFilter, BaseOrder orderDto, Integer page, Integer limit, int threshold, Map<String, Object> operator) {
        ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery();
        buildCriteriaActiveProcessInstance(query, processInstanceFilter, operator);

        if (orderDto == null) {
            query.orderByProcessDefinitionId().asc();
        }
        if (orderDto != null && StringUtils.isNotEmpty(orderDto.getPropertyName())) {
            if (orderDto.getPropertyName().equals("processInstanceId")) {
                query.orderByProcessInstanceId();
            }
            if (orderDto.getPropertyName().equals("processDefinitionId")) {
                query.orderByProcessDefinitionId();
            }
            if (orderDto.getPropertyName().equals("processDefinitionKey")) {
                query.orderByProcessDefinitionKey();
            }
            if (orderDto.isAsc() == true) {
                query.asc();
            } else {
                query.desc();
            }
        }

        List<org.activiti.engine.runtime.ProcessInstance> _processInstanceList;
        try {
            if (page != null && limit != null && limit != -1) {
                _processInstanceList = query.listPage((page - 1) * limit, limit);
            } else {
                _processInstanceList = query.list();
            }
        } catch (Exception e) {
            log.info(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        List<ProcessInstance> processInstanceList = new ArrayList<ProcessInstance>();
        for (org.activiti.engine.runtime.ProcessInstance _processInstance : _processInstanceList) {
            ProcessInstance processInstance = new ProcessInstance();

            processInstance.setProcessInstanceId(_processInstance.getProcessInstanceId());
            processInstance.setProcessDefinitionId(_processInstance.getProcessDefinitionId());
            processInstance.setProcessDefinitionName(_processInstance.getProcessDefinitionName());
            processInstance.setProcessDefinitionKey(_processInstance.getProcessDefinitionKey());
            Map<String, Object> processDefinition = loadProcessDefinition(_processInstance.getProcessDefinitionId());
            if (processDefinition != null) {
                processInstance.setProcessDefinitionName((String) processDefinition.get("NAME_"));
                processInstance.setProcessDefinitionKey((String) processDefinition.get("KEY_"));
            }
            processInstance.setBusinessKey(_processInstance.getBusinessKey());
            if (!_processInstance.isSuspended()) {
                processInstance.setStatus(ProcessInstance.STATUS_RUNNING);
            } else {
                processInstance.setStatus(ProcessInstance.STATUS_SUSPENDED);
            }
            if (threshold >= 9) {
                Map<String, Object> processVariables = new HashMap<String, Object>();
                List<HistoricVariableInstance> historicVariableInstanceList = historyService.createHistoricVariableInstanceQuery().processInstanceId(_processInstance.getId()).list();
                for (HistoricVariableInstance historicVariableInstance : historicVariableInstanceList) {
                    processVariables.put(historicVariableInstance.getVariableName(), historicVariableInstance.getValue());
                }
                processInstance.setProcessVariables(processVariables);
            }

            processInstanceList.add(processInstance);
        }

        return processInstanceList;
    }

    @Override
    public long countActiveProcessInstance(ProcessInstanceFilter processInstanceFilter, Map<String, Object> operator) {
        ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery();
        buildCriteriaActiveProcessInstance(query, processInstanceFilter, operator);

        return query.count();
    }

    private ProcessInstanceQuery buildCriteriaActiveProcessInstance(ProcessInstanceQuery query, ProcessInstanceFilter processInstanceFilter, Map<String, Object> operator) {
        if (StringUtils.isNotEmpty(processInstanceFilter.getProcessInstanceId())) {
            query = query.processInstanceId(processInstanceFilter.getProcessInstanceId());
        }
        if (StringUtils.isNotEmpty(processInstanceFilter.getProcessDefinitionId())) {
            query = query.processDefinitionId(processInstanceFilter.getProcessDefinitionId());
        }
        if (StringUtils.isNotEmpty(processInstanceFilter.getProcessDefinitionKey())) {
            query = query.processDefinitionKey(processInstanceFilter.getProcessDefinitionKey());
        }
        if (StringUtils.isNotEmpty(processInstanceFilter.getBusinessKey())) {
            query = query.processInstanceBusinessKey(processInstanceFilter.getBusinessKey());
        }
        if (StringUtils.isNotEmpty(processInstanceFilter.getInvolvedUserId())) {
            query = query.involvedUser(processInstanceFilter.getInvolvedUserId());
        }
        if (ProcessInstanceFilter.TRUE.equals(processInstanceFilter.getSuspended())) {
            query = query.suspended();
        }
        if (ProcessInstanceFilter.FALSE.equals(processInstanceFilter.getSuspended())) {
            query = query.active();
        }
        if (processInstanceFilter.getProcessVariables() != null) {
            for (Map.Entry<String, Object> entry : processInstanceFilter.getProcessVariables().entrySet()) {
                if (StringUtils.isNotEmpty((String) entry.getValue())) {
                    query = query.variableValueLike(entry.getKey(), "%" + (String) entry.getValue() + "%");
                }
            }
        }

        return query;
    }

    @Override
    public List<Map<String, Object>> selectDraftProcessInstance(Map<String, Object> operator) {
        String sql = "select * from ACT_RU_EXECUTION where SUSPENSION_STATE_ = 9";
        return activitiJdbcTemplate.queryForList(sql);
    }

    @Override
    public void deleteProcessInstance(String processInstanceId, String deleteReason, Map<String, Object> operator) {
        try {
            runtimeService.deleteProcessInstance(processInstanceId, deleteReason);
        } catch (Exception e) {
            log.info(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteHistoricProcessInstance(String processInstanceId, Map<String, Object> operator) {
        try {
            historyService.deleteHistoricProcessInstance(processInstanceId);
        } catch (Exception e) {
            log.info(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteProcessInstanceCompletely(final String processInstanceId, Map<String, Object> operator) {
        activitiTransactionTemplate.execute(new TransactionCallbackWithoutResult() {
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    String sql;
                    sql = "delete from ACT_HI_IDENTITYLINK where PROC_INST_ID_ = ? or TASK_ID_ in (select ID_ from ACT_HI_TASKINST where PROC_INST_ID_ = ?)";
                    activitiJdbcTemplate.update(sql, new Object[]{processInstanceId, processInstanceId});
                    sql = "delete from ACT_HI_VARINST where PROC_INST_ID_ = ?";
                    activitiJdbcTemplate.update(sql, new Object[]{processInstanceId});
                    sql = "delete from ACT_HI_ACTINST where PROC_INST_ID_ = ?";
                    activitiJdbcTemplate.update(sql, new Object[]{processInstanceId});
                    sql = "delete from ACT_HI_TASKINST where PROC_INST_ID_ = ?";
                    activitiJdbcTemplate.update(sql, new Object[]{processInstanceId});
                    sql = "delete from ACT_HI_PROCINST where PROC_INST_ID_ = ?";
                    activitiJdbcTemplate.update(sql, new Object[]{processInstanceId});
                    sql = "delete from ACT_RU_IDENTITYLINK where PROC_INST_ID_ = ? or TASK_ID_ in (select ID_ from ACT_RU_TASK where PROC_INST_ID_ = ?)";
                    activitiJdbcTemplate.update(sql, new Object[]{processInstanceId, processInstanceId});
                    sql = "delete from ACT_RU_VARIABLE where PROC_INST_ID_ = ?";
                    activitiJdbcTemplate.update(sql, new Object[]{processInstanceId});
                    sql = "delete from ACT_RU_TASK where PROC_INST_ID_ = ?";
                    activitiJdbcTemplate.update(sql, new Object[]{processInstanceId});
                    sql = "delete from ACT_RU_EXECUTION where PROC_INST_ID_ = ?";
                    activitiJdbcTemplate.update(sql, new Object[]{processInstanceId});
                } catch (Exception e) {
                    status.setRollbackOnly();
                    throw new RuntimeException(e);
                }
            }
        });

    }

    @Override
    public void suspendProcessInstance(String processInstanceId, Map<String, Object> operator) {
        try {
            runtimeService.suspendProcessInstanceById(processInstanceId);
        } catch (Exception e) {
            log.info(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void activateProcessInstance(String processInstanceId, Map<String, Object> operator) {
        try {
            runtimeService.activateProcessInstanceById(processInstanceId);
        } catch (Exception e) {
            log.info(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void draftProcessInstance(final String processInstanceId, Map<String, Object> operator) {
        activitiTransactionTemplate.execute(new TransactionCallbackWithoutResult() {
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    String sql;
                    sql = "update ACT_RU_TASK set SUSPENSION_STATE_ = 9 where PROC_INST_ID_ = ?";
                    activitiJdbcTemplate.update(sql, new Object[]{processInstanceId});
                    sql = "update ACT_RU_EXECUTION set SUSPENSION_STATE_ = 9 where PROC_INST_ID_ = ?";
                    activitiJdbcTemplate.update(sql, new Object[]{processInstanceId});
                } catch (Exception e) {
                    status.setRollbackOnly();
                    log.info(e.getMessage(), e);
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public void formalProcessInstance(final String processInstanceId, Map<String, Object> operator) {
        activitiTransactionTemplate.execute(new TransactionCallbackWithoutResult() {
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    String sql;
                    sql = "update ACT_RU_EXECUTION set SUSPENSION_STATE_ = 1 where PROC_INST_ID_ = ?";
                    activitiJdbcTemplate.update(sql, new Object[]{processInstanceId});
                    sql = "update ACT_RU_TASK set SUSPENSION_STATE_ = 1 where PROC_INST_ID_ = ?";
                    activitiJdbcTemplate.update(sql, new Object[]{processInstanceId});
                } catch (Exception e) {
                    status.setRollbackOnly();
                    log.info(e.getMessage(), e);
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public cn.newangels.system.dto.Task loadTask(String taskId, Map<String, Object> operator) {
        Task task = null;
        TaskFilter taskFilter = new TaskFilter();
        taskFilter.setTaskId(taskId);
        List<Task> taskList = selectTask(taskFilter, null, 1, -1, 9, operator);
        if (taskList.size() == 1) {
            task = taskList.get(0);
            task.setProcessVariables(taskService.getVariables(task.getTaskId()));
        }

        return task;
    }

    @SuppressWarnings("unchecked")
    public List<Task> selectAllRunningTask(String CODE_, String TITLE_, String EMP_CODE_, Boolean expired, Integer page, Integer limit, Map<String, Object> operator) {
        List<Object> result = buildSqlAllRunningTask(false, CODE_, TITLE_, EMP_CODE_, expired, operator);
        String sql = (String) result.get(0);
        Map<String, Object> paramMap = (Map<String, Object>) result.get(1);

        if (page != null && limit != null && limit > 0) {
            int start = (page - 1) * limit + 1;
            int end = page * limit;
            sql = "select * from (select fulltable.*, ROWNUM RN from (" + sql + ") fulltable where ROWNUM <= " + end + ") where RN >= " + start;
        }

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(activitiJdbcTemplate);
        List<Map<String, Object>> taskIdList = namedParameterJdbcTemplate.queryForList(sql, paramMap);
        List<Task> taskList = new ArrayList<>();
        for (Map<String, Object> taskId : taskIdList) {
            taskList.add(loadTask((String) taskId.get("ID_"), operator));
        }
        return taskList;
    }

    @SuppressWarnings("unchecked")
    public long countAllRunningTask(String CODE_, String TITLE_, String EMP_CODE_, Boolean expired, Map<String, Object> operator) {
        List<Object> result = buildSqlAllRunningTask(true, CODE_, TITLE_, EMP_CODE_, expired, operator);
        String sql = (String) result.get(0);
        Map<String, Object> paramMap = (Map<String, Object>) result.get(1);

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(activitiJdbcTemplate);
        return namedParameterJdbcTemplate.queryForObject(sql, paramMap, Integer.class);
    }

    private List<Object> buildSqlAllRunningTask(boolean count, String CODE_, String TITLE_, String EMP_CODE_, Boolean expired, Map<String, Object> operator) {
        String sql;
        if (count) {
            sql = "select count(*) from ACT_RU_TASK T where 1 = 1";
        } else {
            sql = "select ID_ from ACT_RU_TASK T where 1 = 1";
        }

        Map<String, Object> paramMap = new HashMap<String, Object>();

        if (StringUtils.isNotEmpty(EMP_CODE_)) {
            sql += " and T.ASSIGNEE_ like '%EMP[' || :ASSIGNEE_ || ']%'";
            paramMap.put("ASSIGNEE_", EMP_CODE_);
        }
        if (expired != null) {
            if (expired) {
                sql += " and T.DUE_DATE_ > :DUE_DATE_";
            } else {
                sql += " and T.DUE_DATE_ <= :DUE_DATE_";
            }
            paramMap.put("DUE_DATE_", new Date());
        }
        int sum = 0;
        Integer DATA_ACCESS_ = null;
        if (operator.get("DATA_ACCESS_") != null) {
            DATA_ACCESS_ = ((BigDecimal) operator.get("DATA_ACCESS_")).intValue();
        }
        if (DATA_ACCESS_ != null && DATA_ACCESS_ != 1) {
            sum++;
        }
        if (StringUtils.isNotEmpty(CODE_)) {
            sum++;
        }
        if (StringUtils.isNotEmpty(TITLE_)) {
            sum++;
        }
        if (sum > 0) {
            sql += " and PROC_INST_ID_ in (select PROC_INST_ID_ from ACT_RU_VARIABLE group by PROC_INST_ID_ having sum(case";
            if (StringUtils.isNotEmpty(CODE_)) {
                sql += " when NAME_ = 'CODE_' and TEXT_ like '%' || :CODE_ || '%' then 1";
                paramMap.put("CODE_", CODE_);
            }
            if (StringUtils.isNotEmpty(TITLE_)) {
                sql += " when NAME_ = 'TITLE_' and TEXT_ like '%' || :TITLE_ || '%' then 1";
                paramMap.put("TITLE_", TITLE_);
            }
            if (DATA_ACCESS_ != null && DATA_ACCESS_ != 1) {
                List<Map<String, Object>> orgList = new ArrayList<>();
                if (DATA_ACCESS_ == 2) {
                    //orgList = orgService.selectOrg(Arrays.asList("1", "2"), null, operator);
                }
                if (DATA_ACCESS_ == 3) {
                    //Map<String, Object> com = orgService.loadOrgByOrgCode((String) operator.get("COM_CODE_"), operator);
                    //orgList = orgService.selectChildOrgIncludeSelf((String) com.get("ORG_ID_"), operator);
                }

                List<String> orgCodeList = new ArrayList<>();
                for (Map<String, Object> org : orgList) {
                    orgCodeList.add((String) org.get("ORG_CODE_"));
                }
                sql += " when NAME_ = 'initComCode' and TEXT_ in (:orgCodeList) then 1";
                paramMap.put("orgCodeList", orgCodeList);
            }
            sql += " end) = :SUM)";
            paramMap.put("SUM", sum);
        }

        if (!count) {
            sql += " order by CREATE_TIME_ desc";
        }

        List<Object> result = new ArrayList<Object>();
        result.add(sql);
        result.add(paramMap);

        return result;
    }

    @Override
    public Task loadFirstTask(String processInstanceId, Map<String, Object> operator) {
        Task task = null;
        TaskFilter taskFilter = new TaskFilter();
        taskFilter.setProcessInstanceId(processInstanceId);
        List<Task> taskList = selectTask(taskFilter, null, 1, -1, 9, operator);
        if (taskList.size() > 0) {
            task = taskList.get(0);
            task.setProcessVariables(taskService.getVariables(task.getTaskId()));
        }

        return task;
    }

    @Override
    public Task loadLastTask(String processInstanceId, Map<String, Object> operator) {
        Task task = null;
        TaskFilter taskFilter = new TaskFilter();
        taskFilter.setProcessInstanceId(processInstanceId);
        List<Task> taskList = selectTask(taskFilter, null, 1, -1, 9, operator);
        if (taskList.size() > 0) {
            task = taskList.get(taskList.size() - 1);
            task.setProcessVariables(taskService.getVariables(task.getTaskId()));
        }

        return task;
    }

    @Override
    public Task loadHistoricTask(String taskId, Map<String, Object> operator) {
        HistoricTaskInstance _task = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        ProcessInstance processInstance = loadProcessInstance(_task.getProcessInstanceId(), operator);

        Task task = new Task();

        task.setTaskId(_task.getId());
        task.setTaskName(_task.getName());
        task.setParentTaskId(_task.getParentTaskId());
        task.setTaskDefinitionKey(_task.getTaskDefinitionKey());
        task.setExecutionId(_task.getExecutionId());
        task.setProcessInstanceId(_task.getProcessInstanceId());
        task.setProcessDefinitionId(_task.getProcessDefinitionId());
        task.setProcessDefinitionName(processInstance.getProcessDefinitionName());
        task.setProcessDefinitionKey(processInstance.getProcessDefinitionKey());
        task.setBusinessKey(processInstance.getBusinessKey());
        task.setFormKey(_task.getFormKey());
        task.setStartUser(processInstance.getStartUser());
        task.setAssignee(_task.getAssignee());
        task.setProcessInstanceCreateTime(processInstance.getStartTime());
        task.setCreateTime(_task.getCreateTime());
        task.setEndTime(_task.getEndTime());
        task.setDueDate(_task.getDueDate());
        task.setPriority(_task.getPriority());
        task.setApprovalMemo(_task.getDescription());
        task.setTenantId(_task.getTenantId());
        if (_task.getEndTime() == null) {
            task.setStatus(Task.STATUS_RUNNING);
        } else {
            task.setStatus(Task.STATUS_FINISHED);
        }
        task.setProcessVariables(processInstance.getProcessVariables());

        fillTask(task, operator);

        return task;
    }

    @Override
    public List<Task> selectTask(TaskFilter taskFilter, BaseOrder orderDto, Integer page, Integer limit, int threshold, Map<String, Object> operator) {
        TaskQuery query = taskService.createTaskQuery();
        buildCriteriaTask(query, taskFilter, operator);

        if (orderDto == null) {
            query.orderByTaskCreateTime().desc();
        }
        if (orderDto != null && StringUtils.isNotEmpty(orderDto.getPropertyName())) {
            if (orderDto.getPropertyName().equals("taskId")) {
                query.orderByTaskId();
            }
            if (orderDto.getPropertyName().equals("taskDefinitionKey")) {
                query.orderByTaskDefinitionKey();
            }
            if (orderDto.getPropertyName().equals("processInstanceId")) {
                query.orderByProcessInstanceId();
            }
            if (orderDto.getPropertyName().equals("processDefinitionId")) {
                query.orderByProcessDefinitionId();
            }
            if (orderDto.getPropertyName().equals("taskAssignee")) {
                query.orderByTaskAssignee();
            }
            if (orderDto.getPropertyName().equals("taskCreateTime")) {
                query.orderByTaskCreateTime();
            }
            if (orderDto.getPropertyName().equals("taskDueDate")) {
                query.orderByTaskDueDate();
            }
            if (orderDto.getPropertyName().equals("taskPriority")) {
                query.orderByTaskPriority();
            }
            if (orderDto.isAsc() == true) {
                query.asc();
            } else {
                query.desc();
            }
        }

        List<org.activiti.engine.task.Task> _taskList;
        if (page != null && limit != null && limit != -1) {
            _taskList = query.listPage((page - 1) * limit, limit);
        } else {
            _taskList = query.list();
        }

        List<Task> taskList = new ArrayList<Task>();
        for (org.activiti.engine.task.Task _task : _taskList) {
            Task task = new Task();
            ProcessInstance processInstance = loadProcessInstance(_task.getProcessInstanceId(), operator);
            task.setTaskId(_task.getId());
            task.setTaskName(_task.getName());
            task.setParentTaskId(_task.getParentTaskId());
            task.setTaskDefinitionKey(_task.getTaskDefinitionKey());
            task.setExecutionId(_task.getExecutionId());
            task.setProcessInstanceId(_task.getProcessInstanceId());
            task.setProcessDefinitionId(_task.getProcessDefinitionId());
            task.setProcessDefinitionName(processInstance.getProcessDefinitionName());
            task.setProcessDefinitionKey(processInstance.getProcessDefinitionKey());
            task.setBusinessKey(processInstance.getBusinessKey());
            task.setFormKey(_task.getFormKey());
            task.setStartUser(processInstance.getStartUser());
            task.setAssignee(_task.getAssignee());
            task.setProcessInstanceCreateTime(processInstance.getStartTime());
            task.setCreateTime(_task.getCreateTime());
            task.setDueDate(_task.getDueDate());
            task.setPriority(_task.getPriority());
            if (_task.isSuspended()) {
                task.setSuspensionState(Task.SUSPENSIONSTATE_SUSPENDED);
            } else {
                task.setSuspensionState(Task.SUSPENSIONSTATE_ACTIVE);
            }
            task.setApprovalMemo(_task.getDescription());
            task.setTenantId(_task.getTenantId());
            task.setStatus(Task.STATUS_RUNNING);
            task.setProcessVariables(processInstance.getProcessVariables());

            if (threshold >= 9) {
                // load information of candidateList
                // create context
                ExpressionFactory factory = new ExpressionFactoryImpl();
                SimpleContext context = new SimpleContext();
                Map<String, Object> processVariables = processInstance.getProcessVariables();
                for (Iterator<String> iterator = processVariables.keySet().iterator(); iterator.hasNext(); ) {
                    String key = iterator.next();
                    Object processVariable = processVariables.get(key);
                    context.setVariable(key, factory.createValueExpression(processVariable, Object.class));
                }

                // candidateList
                FlowNode flowNode = (FlowNode) getBpmnModel(processInstance.getProcessDefinitionId()).getFlowElement(_task.getTaskDefinitionKey());
                List<String> candidateUserExpressionTextList = ((UserTask) flowNode).getCandidateUsers();
                List<Map<String, Object>> candidateList = new ArrayList<>();
                String candidateUserExpressionText = StringUtils.join(candidateUserExpressionTextList, ",");
                ValueExpression expression = new ExpressionFactoryImpl().createValueExpression(context, candidateUserExpressionText, Object.class);
                Object value = expression.getValue(context);
                if (value instanceof String) {
                    String[] splits = ((String) value).split(":");
                    if ("selectEmpByDeptRole".equals(splits[0])) {
                        candidateList = baseService.selectEmpByDeptRole(transNull(splits[1]), transNull(splits[2]));  //TODO 根据角色专业查人员
                    }
                } else {
                    throw new RuntimeException("待办候选人数据错误");
                }
                task.setCandidateList(candidateList);
            }

            taskList.add(task);
        }

        fillTask(taskList, operator);

        return taskList;
    }
    /*

    @Override
    public List<Task> selectTask(TaskFilter taskFilter, BaseOrder orderDto, Integer page, Integer limit, int threshold, Map<String, Object> operator) {
        TaskQuery query = taskService.createTaskQuery();
        buildCriteriaTask(query, taskFilter, operator);

        if (orderDto == null) {
            query.orderByTaskCreateTime().desc();
        }
        if (orderDto != null && StringUtils.isNotEmpty(orderDto.getPropertyName())) {
            if (orderDto.getPropertyName().equals("taskId")) {
                query.orderByTaskId();
            }
            if (orderDto.getPropertyName().equals("taskDefinitionKey")) {
                query.orderByTaskDefinitionKey();
            }
            if (orderDto.getPropertyName().equals("processInstanceId")) {
                query.orderByProcessInstanceId();
            }
            if (orderDto.getPropertyName().equals("processDefinitionId")) {
                query.orderByProcessDefinitionId();
            }
            if (orderDto.getPropertyName().equals("taskAssignee")) {
                query.orderByTaskAssignee();
            }
            if (orderDto.getPropertyName().equals("taskCreateTime")) {
                query.orderByTaskCreateTime();
            }
            if (orderDto.getPropertyName().equals("taskDueDate")) {
                query.orderByTaskDueDate();
            }
            if (orderDto.getPropertyName().equals("taskPriority")) {
                query.orderByTaskPriority();
            }
            if (orderDto.isAsc() == true) {
                query.asc();
            } else {
                query.desc();
            }
        }

        List<org.activiti.engine.task.Task> _taskList;
        if (page != null && limit != null && limit != -1) {
            _taskList = query.listPage((page - 1) * limit, limit);
        } else {
            _taskList = query.list();
        }

        List<Task> taskList = new ArrayList<Task>();
        for (org.activiti.engine.task.Task _task : _taskList) {
            Task task = new Task();
            ProcessInstance processInstance = loadProcessInstance(_task.getProcessInstanceId(), operator);
            task.setTaskId(_task.getId());
            task.setTaskName(_task.getName());
            task.setParentTaskId(_task.getParentTaskId());
            task.setTaskDefinitionKey(_task.getTaskDefinitionKey());
            task.setExecutionId(_task.getExecutionId());
            task.setProcessInstanceId(_task.getProcessInstanceId());
            task.setProcessDefinitionId(_task.getProcessDefinitionId());
            task.setProcessDefinitionName(processInstance.getProcessDefinitionName());
            task.setProcessDefinitionKey(processInstance.getProcessDefinitionKey());
            task.setBusinessKey(processInstance.getBusinessKey());
            task.setFormKey(_task.getFormKey());
            task.setStartUser(processInstance.getStartUser());
            task.setAssignee(_task.getAssignee());
            task.setProcessInstanceCreateTime(processInstance.getStartTime());
            task.setCreateTime(_task.getCreateTime());
            task.setDueDate(_task.getDueDate());
            task.setPriority(_task.getPriority());
            if (_task.isSuspended()) {
                task.setSuspensionState(Task.SUSPENSIONSTATE_SUSPENDED);
            } else {
                task.setSuspensionState(Task.SUSPENSIONSTATE_ACTIVE);
            }
            task.setApprovalMemo(_task.getDescription());
            task.setTenantId(_task.getTenantId());
            task.setStatus(Task.STATUS_RUNNING);
            task.setProcessVariables(processInstance.getProcessVariables());

            if (threshold >= 9) {
                // load information of candidateList
                // create context
                ExpressionFactory factory = new ExpressionFactoryImpl();
                SimpleContext context = new SimpleContext();
                Map<String, Object> processVariables = processInstance.getProcessVariables();
                for (Iterator<String> iterator = processVariables.keySet().iterator(); iterator.hasNext(); ) {
                    String key = iterator.next();
                    Object processVariable = processVariables.get(key);
                    context.setVariable(key, factory.createValueExpression(processVariable, Object.class));
                }
                //context.setVariable("activitiEmpService", factory.createValueExpression(activitiEmpService, ActivitiEmpService.class));

                // candidateList
                FlowNode flowNode = (FlowNode) getBpmnModel(processInstance.getProcessDefinitionId()).getFlowElement(_task.getTaskDefinitionKey());
                List<String> candidateUserExpressionTextList = ((UserTask) flowNode).getCandidateUsers();
                List<Map<String, Object>> candidateList = new ArrayList<>();
                String candidateUserExpressionText = StringUtils.join(candidateUserExpressionTextList, ",");


                ApplicationContext applicationContext = processEngineConfiguration.getApplicationContext();
                Map beans = applicationContext.getBeansOfType(Object.class);
                SpringExpressionManager expressionManager = new SpringExpressionManager(applicationContext, beans);
                Expression expression = expressionManager.createExpression(candidateUserExpressionText);
                MyVariableScope impl = new MyVariableScope(processVariables);
                Context.setProcessEngineConfiguration(processEngineConfiguration);

                List<Map> taskSubProcCandidate = (List<Map>) expression.getValue(impl);
                candidateList = BaseUtils.listConvert(taskSubProcCandidate);
                if (candidateList.size() == 0) {
                    throw new RuntimeException("第一个待办候选人未设置");
                }
                task.setCandidateList(candidateList);
            }

            taskList.add(task);
        }

        //fillTask(taskList, operator);

        return taskList;
    }*/

    @Override
    public long countTask(TaskFilter taskFilter, Map<String, Object> operator) {
        TaskQuery query = taskService.createTaskQuery();
        buildCriteriaTask(query, taskFilter, operator);

        return query.count();
    }

    private TaskQuery buildCriteriaTask(TaskQuery query, TaskFilter taskFilter, Map<String, Object> operator) {
        if (StringUtils.isNotEmpty(taskFilter.getTaskId())) {
            query = query.taskId(taskFilter.getTaskId());
        }
        if (StringUtils.isNotEmpty(taskFilter.getTaskName())) {
            query = query.taskName(taskFilter.getTaskName());
        }
        if (StringUtils.isNotEmpty(taskFilter.getTaskDefinitionKey())) {
            query = query.taskDefinitionKey(taskFilter.getTaskDefinitionKey());
        }
        if (StringUtils.isNotEmpty(taskFilter.getExecutionId())) {
            query = query.executionId(taskFilter.getExecutionId());
        }
        if (StringUtils.isNotEmpty(taskFilter.getProcessInstanceId())) {
            query = query.processInstanceId(taskFilter.getProcessInstanceId());
        }
        if (StringUtils.isNotEmpty(taskFilter.getProcessDefinitionId())) {
            query = query.processDefinitionId(taskFilter.getProcessDefinitionId());
        }
        if (StringUtils.isNotEmpty(taskFilter.getProcessDefinitionKey())) {
            query = query.processDefinitionKey(taskFilter.getProcessDefinitionKey());
        }
        if (StringUtils.isNotEmpty(taskFilter.getBusinessKey())) {
            query = query.processInstanceBusinessKey(taskFilter.getBusinessKey());
        }
        if (StringUtils.isNotEmpty(taskFilter.getAssignee())) {
            query = query.taskAssignee(taskFilter.getAssignee());
        }
        if (StringUtils.isNotEmpty(taskFilter.getAssigneeLike())) {
            query = query.taskAssigneeLike(taskFilter.getAssigneeLike());
        }
        if (taskFilter.getTaskCreatedBefore() != null) {
            query = query.taskCreatedBefore(taskFilter.getTaskCreatedBefore());
        }
        if (taskFilter.getTaskCreatedAfter() != null) {
            query = query.taskCreatedAfter(taskFilter.getTaskCreatedAfter());
        }
        if (taskFilter.getTaskDueBefore() != null) {
            query = query.taskDueBefore(taskFilter.getTaskDueBefore());
        }
        if (taskFilter.getTaskDueAfter() != null) {
            query = query.taskDueAfter(taskFilter.getTaskDueAfter());
        }
        if (taskFilter.getTaskPriority() != null) {
            query = query.taskPriority(taskFilter.getTaskPriority());
        }
        if (taskFilter.getSuspensionState() != null) {
            if (taskFilter.getSuspensionState() == Task.SUSPENSIONSTATE_ACTIVE) {
                query = query.active();
            } else {
                query = query.suspended();
            }
        }
        if (StringUtils.isNotEmpty(taskFilter.getTenantId())) {
            query = query.taskTenantId(taskFilter.getTenantId());
        }

        if (taskFilter.getProcessVariables() != null) {
            for (Map.Entry<String, Object> entry : taskFilter.getProcessVariables().entrySet()) {
                if (StringUtils.isNotEmpty((String) entry.getValue())) {
                    query = query.processVariableValueLike(entry.getKey(), "%" + (String) entry.getValue() + "%");
                }
            }
        }
        return query;
    }

    @Override
    public List<Task> selectProcessInstanceHistoricTask(String processInstanceId, Map<String, Object> operator) {
        List<HistoricTaskInstance> _taskList = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).orderByTaskCreateTime().asc().list();
        ProcessInstance processInstance = loadProcessInstance(processInstanceId, operator);

        List<Task> taskList = new ArrayList<Task>();
        for (HistoricTaskInstance _task : _taskList) {
            Task task = new Task();

            task.setTaskId(_task.getId());
            task.setTaskName(_task.getName());
            task.setParentTaskId(_task.getParentTaskId());
            task.setTaskDefinitionKey(_task.getTaskDefinitionKey());
            task.setExecutionId(_task.getExecutionId());
            task.setProcessInstanceId(_task.getProcessInstanceId());
            task.setProcessDefinitionId(_task.getProcessDefinitionId());
            task.setProcessDefinitionName(processInstance.getProcessDefinitionName());
            task.setProcessDefinitionKey(processInstance.getProcessDefinitionKey());
            task.setBusinessKey(processInstance.getBusinessKey());
            task.setFormKey(_task.getFormKey());
            task.setStartUser(processInstance.getStartUser());
            task.setAssignee(_task.getAssignee());
            task.setProcessInstanceCreateTime(processInstance.getStartTime());
            task.setCreateTime(_task.getCreateTime());
            task.setEndTime(_task.getEndTime());
            task.setDueDate(_task.getDueDate());
            task.setPriority(_task.getPriority());
            task.setApprovalMemo(_task.getDescription());
            task.setTenantId(_task.getTenantId());
            if (_task.getEndTime() == null) {
                task.setStatus(Task.STATUS_RUNNING);
            } else {
                task.setStatus(Task.STATUS_FINISHED);
            }
            task.setProcessVariables(processInstance.getProcessVariables());

            taskList.add(task);
        }

        fillTask(taskList, operator);

        return taskList;
    }

    @Override
    public List<Task> selectHistoricTask(String assignee, Integer page, Integer limit, Map<String, Object> operator) {
        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery().taskInvolvedUser(assignee).finished().orderByTaskCreateTime().asc();

        List<HistoricTaskInstance> _taskList;
        if (page != null && limit != null && limit != -1) {
            _taskList = query.listPage((page - 1) * limit, limit);
        } else {
            _taskList = query.list();
        }

        List<Task> taskList = new ArrayList<Task>();
        ProcessInstance processInstance;
        for (HistoricTaskInstance _task : _taskList) {
            processInstance = loadProcessInstance(_task.getProcessInstanceId(), operator);

            Task task = new Task();

            task.setTaskId(_task.getId());
            task.setTaskName(_task.getName());
            task.setParentTaskId(_task.getParentTaskId());
            task.setTaskDefinitionKey(_task.getTaskDefinitionKey());
            task.setExecutionId(_task.getExecutionId());
            task.setProcessInstanceId(_task.getProcessInstanceId());
            task.setProcessDefinitionId(_task.getProcessDefinitionId());
            task.setProcessDefinitionName(processInstance.getProcessDefinitionName());
            task.setProcessDefinitionKey(processInstance.getProcessDefinitionKey());
            task.setBusinessKey(processInstance.getBusinessKey());
            task.setFormKey(_task.getFormKey());
            task.setStartUser(processInstance.getStartUser());
            task.setAssignee(_task.getAssignee());
            task.setProcessInstanceCreateTime(processInstance.getStartTime());
            task.setCreateTime(_task.getCreateTime());
            task.setEndTime(_task.getEndTime());
            task.setDueDate(_task.getDueDate());
            task.setPriority(_task.getPriority());
            task.setApprovalMemo(_task.getDescription());
            task.setTenantId(_task.getTenantId());
            if (_task.getEndTime() == null) {
                task.setStatus(Task.STATUS_RUNNING);
            } else {
                task.setStatus(Task.STATUS_FINISHED);
            }
            task.setProcessVariables(processInstance.getProcessVariables());

            taskList.add(task);
        }

        fillTask(taskList, operator);

        return taskList;
    }

    @Override
    public long countHistoricTask(String assignee, Map<String, Object> operator) {
        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery().taskInvolvedUser(assignee).finished();
        return query.count();
    }

    public long countHistoricTaskForSendMsg(String processInstanceId, Map<String, Object> operator) {
        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId);
        return query.count();
    }

    @Override
    public void reassignTask(String taskId, String assignee, Map<String, Object> operator) {
        org.activiti.engine.task.Task _task = taskService.createTaskQuery().taskId(taskId).singleResult();
        _task.setAssignee(assignee);
        taskService.saveTask(_task);
    }

    @Override
    public List<String> addCounterTask(String taskId, List<String> assigneeList, String approvalMemo, String formKey, Map<String, Object> operator) {
        List<String> counterTaskIdList = new ArrayList<>();

        final Task task = loadTask(taskId, operator);

        for (String assignee : assigneeList) {
            String traceId = BaseUtils.getUuid();
            org.activiti.engine.task.Task _counterTask = taskService.newTask();
            _counterTask.setAssignee(assignee);
            _counterTask.setName("内部流转(" + operator.get("EMP_NAME_") + ")");
            _counterTask.setParentTaskId(task.getTaskId());
            _counterTask.setTenantId(traceId);
            _counterTask.setFormKey(formKey);
            taskService.saveTask(_counterTask);

            final String counterTaskId = taskService.createTaskQuery().taskTenantId(traceId).singleResult().getId();
            counterTaskIdList.add(counterTaskId);
            activitiTransactionTemplate.execute(new TransactionCallbackWithoutResult() {
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    try {
                        String sql = "update ACT_RU_TASK set PROC_INST_ID_ = ?, PROC_DEF_ID_  = ?, TASK_DEF_KEY_ = ?, TENANT_ID_ = ? where ID_ = ?";
                        activitiJdbcTemplate.update(sql, new Object[]{task.getProcessInstanceId(), task.getProcessDefinitionId(), task.getTaskDefinitionKey(), Task.COUNTERTASK, counterTaskId});

                        sql = "update ACT_HI_TASKINST set PROC_INST_ID_ = ?, PROC_DEF_ID_  = ?, TASK_DEF_KEY_ = ?, TENANT_ID_ = ? where ID_ = ?";
                        activitiJdbcTemplate.update(sql, new Object[]{task.getProcessInstanceId(), task.getProcessDefinitionId(), task.getTaskDefinitionKey(), Task.COUNTERTASK, counterTaskId});
                    } catch (Exception e) {
                        status.setRollbackOnly();
                        log.info(e.getMessage(), e);
                        throw new RuntimeException(e);
                    }
                }
            });
        }

        suspendTask(taskId, operator);

        return counterTaskIdList;
    }

    @Override
    public void suspendTask(final String taskId, Map<String, Object> operator) {
        activitiTransactionTemplate.execute(new TransactionCallbackWithoutResult() {
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    String sql = "update ACT_RU_TASK set SUSPENSION_STATE_ = 2 where ID_ = ?";
                    activitiJdbcTemplate.update(sql, new Object[]{taskId});
                } catch (Exception e) {
                    status.setRollbackOnly();
                    log.info(e.getMessage(), e);
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public void activateTask(final String taskId, final Map<String, Object> operator) {
        activitiTransactionTemplate.execute(new TransactionCallbackWithoutResult() {
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    String sql = "update ACT_RU_TASK set SUSPENSION_STATE_ = 1 where ID_ = ?";
                    activitiJdbcTemplate.update(sql, new Object[]{taskId});

                    Task task = loadTask(taskId, operator);
                    ProcessInstance processInstance = loadProcessInstance(task.getProcessInstanceId(), operator);
                    Map<String, Object> processVariables = processInstance.getProcessVariables();
                    String assigneeString = task.getAssignee();
                    String INSTANCE_MSG_ID_ = BaseUtils.getUuid();
                    String SESSION_ = BaseUtils.getUuid();
                    String EMP_CODE_ = assigneeString.substring(assigneeString.indexOf("[") + 1, assigneeString.indexOf("]"));
                    String POSI_ID_ = assigneeString.substring(assigneeString.lastIndexOf("[") + 1, assigneeString.lastIndexOf("]"));
                    String URL_ = "";
                    String INSTANCE_MSG_ = "您有一个新的待办任务: " + processVariables.get("TITLE_");// 公文title
                    String BIZ_URL_ = "directLinkIndex";

                    String formKey = task.getFormKey();
                    StringBuffer CONTENT_URL_ = new StringBuffer(200);
                    CONTENT_URL_.append(formKey);
                    if (CONTENT_URL_.indexOf("?") == -1) {
                        CONTENT_URL_.append("?");
                    } else {
                        CONTENT_URL_.append("&");
                    }
                    CONTENT_URL_.append("FORM_ID_=").append(processVariables.get("FORM_ID_")).append("&processInstanceId=").append(task.getProcessInstanceId()).append("&taskId=").append(taskId).append("&active=true");

                    //instanceMsgService.insertInstanceMsg(INSTANCE_MSG_ID_, SESSION_, EMP_CODE_, POSI_ID_, INSTANCE_MSG_, URL_, BIZ_URL_, CONTENT_URL_.toString(), new Date(), 0, null);

                    if (true) {
                        EMP_CODE_ = "jishitong";
                    }

                    String xele = "<SendMessage><AM_Name>" + EMP_CODE_ + "</AM_Name><PhoneNum></PhoneNum><UserId></UserId><MessageTxt>" + processVariables.get("TITLE_") + "</MessageTxt><SystemName>公文系统</SystemName><Type>即时通</Type><Access></Access><Email></Email><IsBack></IsBack><IsEncrypt></IsEncrypt><ISPriority></ISPriority><Ohter1></Ohter1><Ohter2></Ohter2></SendMessage>";
                    String url = "<a href=\"" + URL_ + "\" target=\"_blank\">" + "您有一个新的待办任务 - " + processVariables.get("TITLE_") + "</a>";
                    //instanceMsgService.AMToMessIFCheck(xele, url, null);

                    //sendMessagesService.sendAM(EMP_CODE_, processVariables.get("TITLE_").toString(), URL_, "您有一个新的待办任务 - " + processVariables.get("TITLE_"),operator);
                } catch (Exception e) {
                    status.setRollbackOnly();
                    log.info(e.getMessage(), e);
                    throw new RuntimeException(e);
                }
            }
        });
    }

    //重新激活父节点任务(选取候选人)
    //@Override
    private void activateTaskN(final String taskId, final String sel_emp_code, final String sel_posi_id, final Map<String, Object> operator) {
        activitiTransactionTemplate.execute(new TransactionCallbackWithoutResult() {
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    String sql = "update ACT_RU_TASK set SUSPENSION_STATE_ = 1,ASSIGNEE_ = 'EMP[" + sel_emp_code + "]POSI[" + sel_posi_id + "]' where ID_ = ?";
                    activitiJdbcTemplate.update(sql, new Object[]{taskId});

                    Task task = loadTask(taskId, operator);
                    ProcessInstance processInstance = loadProcessInstance(task.getProcessInstanceId(), operator);
                    Map<String, Object> processVariables = processInstance.getProcessVariables();

                    String INSTANCE_MSG_ID_ = BaseUtils.getUuid();
                    String SESSION_ = BaseUtils.getUuid();
                    String EMP_CODE_ = sel_emp_code;
                    String POSI_ID_ = sel_posi_id;
                    String URL_ = "";
                    String INSTANCE_MSG_ = "您有一个新的待办任务: " + processVariables.get("TITLE_");// 公文title
                    String BIZ_URL_ = "directLinkIndex";

                    String formKey = task.getFormKey();
                    StringBuffer CONTENT_URL_ = new StringBuffer(200);
                    CONTENT_URL_.append(formKey);
                    if (CONTENT_URL_.indexOf("?") == -1) {
                        CONTENT_URL_.append("?");
                    } else {
                        CONTENT_URL_.append("&");
                    }
                    CONTENT_URL_.append("FORM_ID_=").append(processVariables.get("FORM_ID_")).append("&processInstanceId=").append(task.getProcessInstanceId()).append("&taskId=").append(taskId).append("&active=true");

                    //instanceMsgService.insertInstanceMsg(INSTANCE_MSG_ID_, SESSION_, EMP_CODE_, POSI_ID_, INSTANCE_MSG_, URL_, BIZ_URL_, CONTENT_URL_.toString(), new Date(), 0, null);

                    if (true) {
                        EMP_CODE_ = "jishitong";
                    }

                    String xele = "<SendMessage><AM_Name>" + EMP_CODE_ + "</AM_Name><PhoneNum></PhoneNum><UserId></UserId><MessageTxt>" + processVariables.get("TITLE_") + "</MessageTxt><SystemName>公文系统</SystemName><Type>即时通</Type><Access></Access><Email></Email><IsBack></IsBack><IsEncrypt></IsEncrypt><ISPriority></ISPriority><Ohter1></Ohter1><Ohter2></Ohter2></SendMessage>";
                    String url = "<a href=\"" + URL_ + "\" target=\"_blank\">" + "您有一个新的待办任务 - " + processVariables.get("TITLE_") + "</a>";
                    //instanceMsgService.AMToMessIFCheck(xele, url, null);

                    //sendMessagesService.sendAM(EMP_CODE_, processVariables.get("TITLE_").toString(), URL_, "您有一个新的待办任务 - " + processVariables.get("TITLE_"),operator);
                } catch (Exception e) {
                    status.setRollbackOnly();
                    log.info(e.getMessage(), e);
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public void withdrawToTask(String taskId, Map<String, Object> processVariables, Map<String, Object> operator) {
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();

        if (historicTaskInstance == null) {
            throw new RuntimeException("该任务不存在");
        }
        if (historicTaskInstance.getEndTime() == null) {
            throw new RuntimeException("该任务正在执行中，无需回退");
        }
        if (!historicTaskInstance.getAssignee().startsWith("EMP[" + (String) operator.get("EMP_CODE_") + "]")) {
            log.info("您不是该任务办理人: assigneeId=" + historicTaskInstance.getAssignee() + ", operatorId=" + (String) operator.get("EMP_CODE_"));
            throw new RuntimeException("您不是该任务办理人");
        }

        List<Task> historicTaskList = selectProcessInstanceHistoricTask(historicTaskInstance.getProcessInstanceId(), operator);
        List<Task> removeTaskList = new ArrayList<Task>();
        for (Task historicTask : historicTaskList) {
            if (historicTask.getCreateTime().getTime() > historicTaskInstance.getCreateTime().getTime()) {
                if (historicTask.getEndTime() != null) {
                    throw new RuntimeException("后续任务已经完成，无法撤回");
                }
                removeTaskList.add(historicTask);
            }
        }

        for (int i = removeTaskList.size() - 1; i >= 0; i--) {
            org.activiti.engine.task.Task _task = taskService.createTaskQuery().taskId(removeTaskList.get(i).getTaskId()).singleResult();
            if (_task != null) {
                if (_task.getTenantId().equals(Task.COUNTERTASK)) {
                    activateTask(_task.getParentTaskId(), operator);
                    taskService.complete(_task.getId(), processVariables);
                } else {
                    try {
                        completeToTask(_task, historicTaskInstance.getTaskDefinitionKey(), processVariables);
                    } catch (Exception e) {
                        log.info(e.getMessage(), e);
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    @Override
    public void completeToTask(String taskId, String taskDefinitionKey, Map<String, Object> processVariables, boolean checkAuth, Map<String, Object> operator) {
        org.activiti.engine.task.Task _task = taskService.createTaskQuery().taskId(taskId).singleResult();

        if (_task == null) {
            log.info("任务已处理完毕或被撤回: taskId=" + taskId);
            throw new RuntimeException("任务已处理完毕或被撤回");
        }
        if (!checkAuth || _task.getAssignee().startsWith("EMP[" + (String) operator.get("EMP_CODE_") + "]")) {
            try {
                completeToTask(_task, taskDefinitionKey, processVariables);
            } catch (Exception e) {
                log.info(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        } else {
            log.info("您不是该任务办理人: assigneeId=" + _task.getAssignee() + ", operatorId=" + (String) operator.get("EMP_CODE_"));
            throw new RuntimeException("您不是该任务办理人");
        }
    }

    private void completeToTask(org.activiti.engine.task.Task _task, String taskDefinitionKey, Map<String, Object> processVariables) throws Exception {
        ProcessDefinitionEntity processDefinition = getProcessDefinitionEntity(_task.getProcessDefinitionId());

        ActivityImpl activity = ((ProcessDefinitionImpl) processDefinition).findActivity(_task.getTaskDefinitionKey());
        List<PvmTransition> pvmTransitionList = activity.getOutgoingTransitions();

        List<PvmTransition> tempPvmTransitionList = new ArrayList<PvmTransition>();
        tempPvmTransitionList.addAll(pvmTransitionList);

        synchronized (pvmTransitionList) {
            pvmTransitionList.clear();

            TransitionImpl tempTransition = activity.createOutgoingTransition();
            ActivityImpl targetActivity = ((ProcessDefinitionImpl) processDefinition).findActivity(taskDefinitionKey);
            tempTransition.setDestination(targetActivity);

            taskService.complete(_task.getId(), processVariables);

            targetActivity.getIncomingTransitions().remove(tempTransition);
            pvmTransitionList.clear();
            pvmTransitionList.addAll(tempPvmTransitionList);
        }
    }

    @Override
    public void withdrawTask(String taskId, Map<String, Object> processVariables, Map<String, Object> operator) {
        org.activiti.engine.task.Task _task = taskService.createTaskQuery().taskId(taskId).singleResult();
        Task previousUserTask = loadPreviousUserTask(taskId, null, operator);

        if (_task == null) {
            log.info("任务已处理完毕或被撤回: taskId=" + taskId);
            throw new RuntimeException("任务已处理完毕或被撤回");
        }
        if (previousUserTask.getAssignee().startsWith("EMP[" + (String) operator.get("EMP_CODE_") + "]")) {
            try {
                if (_task.getTenantId().equals(Task.COUNTERTASK)) {
                    taskService.complete(taskId, processVariables);
                    String sql = "select count(*) from ACT_RU_TASK where PARENT_TASK_ID_ = ? and TENANT_ID_ = ?";
                    if (activitiJdbcTemplate.queryForObject(sql, new Object[]{_task.getParentTaskId(), Task.COUNTERTASK}, Integer.class) == 0) {
                        activateTask(_task.getParentTaskId(), operator);
                    }
                } else {
                    if (processVariables == null) {
                        processVariables = new HashMap<>();
                    }
                    processVariables.put("assignee", previousUserTask.getAssignee());
                    setProcessVariables(_task.getProcessInstanceId(), processVariables, operator);
                    completeToTask(_task, previousUserTask.getTaskDefinitionKey(), processVariables);
                }

            } catch (Exception e) {
                log.info(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        } else {
            log.info("您不是上个任务办理人: assigneeId=" + _task.getAssignee() + ", operatorId=" + (String) operator.get("EMP_CODE_"));
            throw new RuntimeException("您不是上个任务办理人");
        }
    }

    @Override
    public void completeTask(String taskId, Map<String, Object> processVariables, Map<String, Object> operator) {
        org.activiti.engine.task.Task _task = taskService.createTaskQuery().taskId(taskId).singleResult();

        if (_task == null) {
            log.info("任务已处理完毕或被撤回: taskId=" + taskId);
            throw new RuntimeException("任务已处理完毕或被撤回");
        }
        if (_task.getAssignee().startsWith("EMP[" + operator.get("EMP_CODE_") + "]")) {
            try {
                taskService.complete(taskId, processVariables);
                if (_task.getTenantId().equals(Task.COUNTERTASK)) {
                    String sql = "select count(*) from ACT_RU_TASK where PARENT_TASK_ID_ = ? and TENANT_ID_ = ?";
                    if (activitiJdbcTemplate.queryForObject(sql, new Object[]{_task.getParentTaskId(), Task.COUNTERTASK}, Integer.class) == 0) {
                        activateTask(_task.getParentTaskId(), operator);
                    }
                }
            } catch (Exception e) {
                if ("UserTask should not be signalled before complete".equals(e.getMessage())) {
                    if (_task.getTenantId().equals(Task.COUNTERTASK)) {
                        String sql = "select count(*) from ACT_RU_TASK where PARENT_TASK_ID_ = ? and TENANT_ID_ = ?";
                        if (activitiJdbcTemplate.queryForObject(sql, new Object[]{_task.getParentTaskId(), Task.COUNTERTASK}, Integer.class) == 0) {
                            activateTask(_task.getParentTaskId(), operator);
                        }
                    }
                } else {
                    log.info("异常：" + e.getMessage());
                    throw new RuntimeException(e.getMessage());
                }
            }

        } else {
            log.info("您不是该任务办理人: assigneeId=" + _task.getAssignee() + ", operatorId=" + (String) operator.get("EMP_CODE_"));
            throw new RuntimeException("您不是该任务办理人");
        }
    }

    @Override
    public void completeTaskN(String taskId, Map<String, Object> processVariables, String sel_emp_code, String sel_posi_id, Map<String, Object> operator) {
        org.activiti.engine.task.Task _task = taskService.createTaskQuery().taskId(taskId).singleResult();

        if (_task == null) {
            log.info("任务已处理完毕或被撤回: taskId=" + taskId);
            throw new RuntimeException("任务已处理完毕或被撤回");
        }
        if (_task.getAssignee().startsWith("EMP[" + (String) operator.get("EMP_CODE_") + "]")) {
            try {
                taskService.complete(taskId, processVariables);
                if (_task.getTenantId().equals(Task.COUNTERTASK)) {
                    String sql = "select count(*) from ACT_RU_TASK where PARENT_TASK_ID_ = ? and TENANT_ID_ = ?";
                    if (activitiJdbcTemplate.queryForObject(sql, new Object[]{_task.getParentTaskId(), Task.COUNTERTASK}, Integer.class) == 0) {
                        activateTaskN(_task.getParentTaskId(), sel_emp_code, sel_posi_id, operator);
                    }
                }
            } catch (Exception e) {
                if ("UserTask should not be signalled before complete".equals(e.getMessage())) {
                    if (_task.getTenantId().equals(Task.COUNTERTASK)) {
                        String sql = "select count(*) from ACT_RU_TASK where PARENT_TASK_ID_ = ? and TENANT_ID_ = ?";
                        if (activitiJdbcTemplate.queryForObject(sql, new Object[]{_task.getParentTaskId(), Task.COUNTERTASK}, Integer.class) == 0) {
                            activateTask(_task.getParentTaskId(), operator);
                        }
                    }
                }
            }

        } else {
            log.info("您不是该任务办理人: assigneeId=" + _task.getAssignee() + ", operatorId=" + (String) operator.get("EMP_CODE_"));
            throw new RuntimeException("您不是该任务办理人");
        }
    }

    @Override
    public void updateTaskApprovalMemo(final String taskId, final String approvalMemo, final Map<String, Object> operator) {
        activitiTransactionTemplate.execute(new TransactionCallbackWithoutResult() {
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    String sql = "update ACT_RU_TASK set DESCRIPTION_ = ? where ID_ = ? and ASSIGNEE_ = ?";
                    activitiJdbcTemplate.update(sql, new Object[]{approvalMemo, taskId, (String) operator.get("EMP_CODE_")});
                } catch (Exception e) {
                    status.setRollbackOnly();
                    log.info(e.getMessage(), e);
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public void updateHistoricTaskApprovalMemo(final String taskId, final String approvalMemo, Map<String, Object> operator) {
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();

        if (historicTaskInstance == null) {
            throw new RuntimeException("该任务不存在");
        }
        if (!historicTaskInstance.getAssignee().startsWith("EMP[" + (String) operator.get("EMP_CODE_") + "]")) {
            log.info("您不是该任务办理人: assigneeId=" + historicTaskInstance.getAssignee() + ", operatorId=" + (String) operator.get("EMP_CODE_"));
            throw new RuntimeException("您不是该任务办理人");
        }

        List<Task> historicTaskList = selectProcessInstanceHistoricTask(historicTaskInstance.getProcessInstanceId(), operator);
        for (Task historicTask : historicTaskList) {
            if (historicTask.getCreateTime().getTime() > historicTaskInstance.getCreateTime().getTime()) {
                if (historicTask.getEndTime() != null) {
                    throw new RuntimeException("后续任务已经完成，无法修改意见");
                }
            }
        }

        activitiTransactionTemplate.execute(new TransactionCallbackWithoutResult() {
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    String sql = "update ACT_RU_TASK set DESCRIPTION_ = ? where ID_ = ?";
                    activitiJdbcTemplate.update(sql, new Object[]{approvalMemo, taskId});

                    sql = "update ACT_HI_TASKINST set DESCRIPTION_ = ? where ID_ = ?";
                    activitiJdbcTemplate.update(sql, new Object[]{approvalMemo, taskId});
                } catch (Exception e) {
                    status.setRollbackOnly();
                    log.info(e.getMessage(), e);
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public void updateAnyHistoricTaskApprovalMemo(final String taskId, final String approvalMemo, Map<String, Object> operator) {
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();

        if (historicTaskInstance == null) {
            throw new RuntimeException("该任务不存在");
        }

        activitiTransactionTemplate.execute(new TransactionCallbackWithoutResult() {
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    String sql = "update ACT_RU_TASK set DESCRIPTION_ = ? where ID_ = ?";
                    activitiJdbcTemplate.update(sql, new Object[]{approvalMemo, taskId});

                    sql = "update ACT_HI_TASKINST set DESCRIPTION_ = ? where ID_ = ?";
                    activitiJdbcTemplate.update(sql, new Object[]{approvalMemo, taskId});
                } catch (Exception e) {
                    status.setRollbackOnly();
                    log.info(e.getMessage(), e);
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public void setProcessVariables(String processInstanceId, Map<String, Object> processVariables, Map<String, Object> operator) {
        for (Iterator<String> iterator = processVariables.keySet().iterator(); iterator.hasNext(); ) {
            String key = iterator.next();
            Object processVariable = processVariables.get(key);
            runtimeService.setVariable(processInstanceId, key, processVariable);
        }
    }

    @Override
    public void setExecutionVariables(String executionId, Map<String, Object> processVariables, Map<String, Object> operator) {
        for (Iterator<String> iterator = processVariables.keySet().iterator(); iterator.hasNext(); ) {
            String key = iterator.next();
            Object processVariable = processVariables.get(key);
            runtimeService.setVariableLocal(executionId, key, processVariable);
        }
    }

    /**
     * 获得下一个用户任务节点
     *
     * @param taskId 任务Id
     * @return 下一个用户任务节点
     */
    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public UserTaskDefinition loadNextUserTaskDefinition(String taskId, Map<String, Object> processVariables, Map<String, Object> operator) {

        Task task = loadTask(taskId, operator);
        UserTaskDefinition userTaskDefinition = new UserTaskDefinition();

        // create context
        ExpressionFactory factory = new ExpressionFactoryImpl();
        SimpleContext context = new SimpleContext();
        for (Iterator<String> iterator = task.getProcessVariables().keySet().iterator(); iterator.hasNext(); ) {
            String key = iterator.next();
            Object processVariable = task.getProcessVariables().get(key);
            context.setVariable(key, factory.createValueExpression(processVariable, Object.class));
        }
        for (Iterator<String> iterator = processVariables.keySet().iterator(); iterator.hasNext(); ) {
            String key = iterator.next();
            Object processVariable = processVariables.get(key);
            context.setVariable(key, factory.createValueExpression(processVariable, Object.class));
        }
        //context.setVariable("activitiEmpService", factory.createValueExpression(activitiEmpService, ActivitiEmpService.class));

        // 查询流程定义
        BpmnModel bpmnModel = getBpmnModel(task.getProcessDefinitionId());
        // 当前节点定义
        FlowNode flowNode = (FlowNode) bpmnModel.getFlowElement(task.getTaskDefinitionKey());

        // taskCandidateList
        FlowNode nextFlowNode = findNextFlowNode(bpmnModel, flowNode, context);
        if (nextFlowNode instanceof UserTask) {
            List<String> candidateUserExpressionTextList = ((UserTask) nextFlowNode).getCandidateUsers();
            List<Map<String, Object>> candidateList = new ArrayList<>();
            String candidateUserExpressionText = StringUtils.join(candidateUserExpressionTextList, ",");
            ValueExpression expression = new ExpressionFactoryImpl().createValueExpression(context, candidateUserExpressionText, Object.class);
            Object value = expression.getValue(context);
            if (value instanceof String) {
                String[] splits = ((String) value).split(":");
                if ("selectEmpByDeptRole".equals(splits[0])) {
                    candidateList = baseService.selectEmpByDeptRole(transNull(splits[1]), transNull(splits[2]));   //TODO 根据角色专业查人员
                }
            } else {
                throw new RuntimeException("待办候选人数据错误");
            }

            userTaskDefinition.setCandidateList(candidateList);
        }

        // taskName
        if (nextFlowNode != null) {
            userTaskDefinition.setTaskName(nextFlowNode.getName());
        }

        return userTaskDefinition;
    }

    private String transNull(String string) {
        if ("null".equals(string)) {
            return null;
        }
        return string;
    }

    /**
     * 获得下一个用户任务节点, 内部递归调用
     *
     * @param flowNode 当前节点
     * @param context  当前流程变量环境，用于流转条件判断
     * @return 下一个用户任务节点
     */
    private FlowNode findNextFlowNode(BpmnModel bpmnModel, FlowNode flowNode, SimpleContext context) {
        FlowNode childFlowNode = null;
        FlowNode nextFlowNode = null;
        FlowNode defaultFlowNode = null;

        for (SequenceFlow sequenceFlow : flowNode.getOutgoingFlows()) {
            childFlowNode = (FlowNode) bpmnModel.getFlowElement(sequenceFlow.getTargetRef());
            if (StringUtils.isEmpty(sequenceFlow.getConditionExpression())) {
                defaultFlowNode = childFlowNode;
            } else {
                try {
                    ValueExpression e = new ExpressionFactoryImpl().createValueExpression(context, sequenceFlow.getConditionExpression(), Boolean.class);
                    if ((Boolean) e.getValue(context)) {
                        nextFlowNode = childFlowNode;
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (flowNode instanceof ExclusiveGateway && nextFlowNode == null) {
            nextFlowNode = defaultFlowNode;
        }

        if (flowNode instanceof ParallelGateway && nextFlowNode == null) {
            nextFlowNode = defaultFlowNode;
        }

        if (flowNode instanceof InclusiveGateway && nextFlowNode == null) {
            nextFlowNode = defaultFlowNode;
        }

        if (flowNode instanceof UserTask && nextFlowNode == null && defaultFlowNode != null) {
            nextFlowNode = defaultFlowNode;
        }

        if (nextFlowNode instanceof ExclusiveGateway) {
            return findNextFlowNode(bpmnModel, nextFlowNode, context);
        }

        if (nextFlowNode instanceof ParallelGateway) {
            return findNextFlowNode(bpmnModel, nextFlowNode, context);
        }
        if (nextFlowNode instanceof InclusiveGateway) {
            return findNextFlowNode(bpmnModel, nextFlowNode, context);
        }

        return nextFlowNode;
    }

    @Override
    public UserTaskDefinition loadPreviousUserTaskDefinition(String taskId, Map<String, Object> processVariables, Map<String, Object> operator) {
        HistoricTaskInstance _task = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();

        List<HistoricTaskInstance> _historicTaskList = historyService.createHistoricTaskInstanceQuery().processInstanceId(_task.getProcessInstanceId()).orderByTaskCreateTime().desc().list();
        HistoricTaskInstance _historicTask = null;
        for (int i = 0; i < _historicTaskList.size(); i++) {
            if (!_historicTaskList.get(i).getName().equals(_task.getName())) {
                _historicTask = _historicTaskList.get(i);
                break;
            }
        }
        if (_historicTask == null) {
            return null;
        }

        UserTaskDefinition userTaskDefinition = new UserTaskDefinition();
        BpmnModel bpmnModel = getBpmnModel(_historicTask.getProcessDefinitionId());
        FlowNode flowNode = (FlowNode) bpmnModel.getFlowElement(_historicTask.getTaskDefinitionKey());
        List<String> EMP_CODE_LIST = new ArrayList<String>();
        EMP_CODE_LIST.add(_historicTask.getAssignee());
        //userTaskDefinition.setCandidateList(empService.selectPersonByPerCodeList(EMP_CODE_LIST, operator));
        userTaskDefinition.setTaskName(flowNode.getName());

        return userTaskDefinition;
    }

    @Override
    public Task loadPreviousUserTask(String taskId, Map<String, Object> processVariables, Map<String, Object> operator) {
        HistoricTaskInstance _task = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();

        List<HistoricTaskInstance> _historicTaskList = historyService.createHistoricTaskInstanceQuery().processInstanceId(_task.getProcessInstanceId()).orderByTaskCreateTime().desc().list();
        HistoricTaskInstance _historicTask = null;
        for (int i = 0; i < _historicTaskList.size(); i++) {
            if (!_historicTaskList.get(i).getName().equals(_task.getName()) && _historicTaskList.get(i).getParentTaskId() == null) {
                _historicTask = _historicTaskList.get(i);
                break;
            }
        }
        if (_historicTask == null) {
            return null;
        }

        Task task = new Task();

        ProcessInstance processInstance = loadProcessInstance(_historicTask.getProcessInstanceId(), operator);
        task.setTaskId(_historicTask.getId());
        task.setTaskName(_historicTask.getName());
        task.setParentTaskId(_historicTask.getParentTaskId());
        task.setTaskDefinitionKey(_historicTask.getTaskDefinitionKey());
        task.setExecutionId(_historicTask.getExecutionId());
        task.setProcessInstanceId(_historicTask.getProcessInstanceId());
        task.setProcessDefinitionId(_historicTask.getProcessDefinitionId());
        task.setProcessDefinitionName(processInstance.getProcessDefinitionName());
        task.setProcessDefinitionKey(processInstance.getProcessDefinitionKey());
        task.setBusinessKey(processInstance.getBusinessKey());
        task.setStartUser(processInstance.getStartUser());
        task.setAssignee(_historicTask.getAssignee());
        task.setProcessInstanceCreateTime(processInstance.getStartTime());
        task.setCreateTime(_historicTask.getCreateTime());
        task.setDueDate(_historicTask.getDueDate());
        task.setPriority(_historicTask.getPriority());
        task.setApprovalMemo(_historicTask.getDescription());
        task.setTenantId(_historicTask.getTenantId());
        task.setStatus(Task.STATUS_RUNNING);
        task.setProcessVariables(processInstance.getProcessVariables());

        return task;
    }

    @Override
    public List<ProcessDefinition> selectProcessDefinition(int threshold, Map<String, Object> operator) {
        Map<String, ProcessDefinition> processDefinitionMap = new HashMap<String, ProcessDefinition>();
        String sql;
        List<Map<String, Object>> list;

        sql = "select * from ACT_RE_PROCDEF";
        list = activitiJdbcTemplate.queryForList(sql);
        for (Map<String, Object> map : list) {
            ProcessDefinition processDefinition = new ProcessDefinition();
            processDefinition.setProcessDefinitionId((String) map.get(("ID_")));
            processDefinition.setProcessDefinitionName((String) map.get(("NAME_")));
            processDefinition.setProcessDefinitionKey((String) map.get(("KEY_")));
            processDefinition.setCategory((String) map.get(("CATEGORY_")));
            processDefinition.setVersion(((BigDecimal) map.get("VERSION_")).intValue());
            processDefinition.setSuspensionState(((BigDecimal) map.get("SUSPENSION_STATE_")).intValue());
            processDefinition.setDeploymentId((String) (map.get("DEPLOYMENT_ID_")));

            processDefinitionMap.put(processDefinition.getProcessDefinitionId(), processDefinition);
        }
        if (threshold >= 9) {
            sql = "select PROC_DEF_ID_, count(*) totalProcessInstanceCount from ACT_HI_PROCINST group by PROC_DEF_ID_";
            list = activitiJdbcTemplate.queryForList(sql);
            for (Map<String, Object> map : list) {
                processDefinitionMap.get((String) map.get(("PROC_DEF_ID_"))).setTotalProcessInstanceCount(((BigDecimal) map.get("totalProcessInstanceCount")).intValue());
            }

            sql = "select PROC_DEF_ID_, count(*) activeProcessInstanceCount from ACT_HI_PROCINST where END_TIME_ is null group by PROC_DEF_ID_";
            list = activitiJdbcTemplate.queryForList(sql);
            for (Map<String, Object> map : list) {
                processDefinitionMap.get((String) map.get(("PROC_DEF_ID_"))).setActiveProcessInstanceCount(((BigDecimal) map.get("activeProcessInstanceCount")).intValue());
            }

            sql = "select PROC_DEF_ID_, count(*) activeTaskCount from ACT_RU_TASK group by PROC_DEF_ID_";
            list = activitiJdbcTemplate.queryForList(sql);
            for (Map<String, Object> map : list) {
                processDefinitionMap.get((String) map.get(("PROC_DEF_ID_"))).setActiveTaskCount(((BigDecimal) map.get("activeTaskCount")).intValue());
            }

            sql = "select PROC_DEF_ID_, count(*) overtimeTaskCount from ACT_RU_TASK where DUE_DATE_ < SYSDATE group by PROC_DEF_ID_";
            list = activitiJdbcTemplate.queryForList(sql);
            for (Map<String, Object> map : list) {
                processDefinitionMap.get((String) map.get(("PROC_DEF_ID_"))).setOvertimeTaskCount(((BigDecimal) map.get("overtimeTaskCount")).intValue());
            }
        }
        List<ProcessDefinition> processDefinitionList = new ArrayList<ProcessDefinition>();
        processDefinitionList.addAll(processDefinitionMap.values());
        Collections.sort(processDefinitionList, new Comparator<ProcessDefinition>() {
            public int compare(ProcessDefinition o1, ProcessDefinition o2) {
                if (o1.getProcessDefinitionName().compareTo(o2.getProcessDefinitionName()) > 0) {
                    return 1;
                }
                if (o1.getProcessDefinitionName().compareTo(o2.getProcessDefinitionName()) < 0) {
                    return -1;
                }
                if (o1.getVersion() < o2.getVersion()) {
                    return 1;
                }
                if (o1.getVersion() > o2.getVersion()) {
                    return -1;
                }
                return 0;
            }
        });
        return processDefinitionList;
    }

    public List<Map<String, Object>> selectProcessDefinitionKey(Map<String, Object> operator) {
        String sql = "select distinct KEY_, NAME_ from ACT_RE_PROCDEF order by KEY_";
        return activitiJdbcTemplate.queryForList(sql);
    }

    @Override
    public InputStream loadProcessDefinitionDiagram(String processDefinitionKey, Map<String, Object> operator) {
        InputStream inputStream;

        try {
            org.activiti.engine.repository.ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefinitionKey).latestVersion().singleResult();
            inputStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), processDefinition.getDiagramResourceName());
            if (inputStream == null) {
                throw new RuntimeException("未找到流程图");
            }
        } catch (Exception e) {
            log.info(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        return inputStream;
    }

    @Override
    public InputStream loadProcessInstanceDiagram(String processInstanceId, Map<String, Object> operator) {
        InputStream inputStream;

        try {
            HistoricProcessInstance _processInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            if (_processInstance == null) {
                throw new RuntimeException("流程不存在");
            }

            List<String> executionActivityIdList = new ArrayList<String>();
            List<Execution> executionList = runtimeService.createExecutionQuery().processInstanceId(processInstanceId).list();
            for (Execution execution : executionList) {
                executionActivityIdList.add(execution.getActivityId());
            }

            ProcessDefinitionEntity processDefinitionEntity = getProcessDefinitionEntity(_processInstance.getProcessDefinitionId());
            inputStream = getProcessDefinitionDiagram(_processInstance.getProcessDefinitionId());
            if (inputStream != null) {
                if (executionActivityIdList.size() > 0) {
                    BufferedImage image = ImageIO.read(inputStream);
                    Graphics2D g = (Graphics2D) image.getGraphics();
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g.setColor(Color.RED);
                    g.setStroke(new BasicStroke(2.0F));

                    List<ActivityImpl> activityList = (processDefinitionEntity).getActivities();
                    for (ActivityImpl activity : activityList) {
                        if (executionActivityIdList.contains(activity.getId())) {
                            g.drawRect(activity.getX(), activity.getY(), activity.getWidth(), activity.getHeight());
                        }
                    }

                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    ImageIO.write(image, "png", outputStream);
                    inputStream.close();
                    inputStream = new ByteArrayInputStream(outputStream.toByteArray());
                }
            } else {
                throw new RuntimeException("未找到流程图");
            }
        } catch (Exception e) {
            log.info(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        return inputStream;
    }

    private ProcessDefinitionEntity getProcessDefinitionEntity(String processDefinitionId) {
        return processDefinitionEntityMap.get(processDefinitionId);
    }

    private BpmnModel getBpmnModel(String processDefinitionId) {
        return bpmnModelMap.get(processDefinitionId);
    }

    private ByteArrayInputStream getProcessDefinitionDiagram(String processDefinitionId) {
        return new ByteArrayInputStream(processDefinitionDiagramMap.get(processDefinitionId).toByteArray());
    }

    private void fillTask(Task task, Map<String, Object> operator) {
        List<String> EMP_CODE_LIST = new ArrayList<String>();
        EMP_CODE_LIST.add(task.getStartUser());
        EMP_CODE_LIST.add(task.getAssignee().substring(task.getAssignee().indexOf("[") + 1, task.getAssignee().indexOf("]")));
        if (EMP_CODE_LIST.size() == 0) {
            return;
        }

        List<Map<String, Object>> empList = personService.selectPersonByPerCodeList(EMP_CODE_LIST);  //TODO 根据人员编码
        for (Map<String, Object> emp : empList) {
            if (task.getStartUser().equals(emp.get("V_PERCODE"))) {
                task.setStartUserName((String) emp.get("V_PERNAME"));
                break;
            }
        }
        for (Map<String, Object> emp : empList) {
            if (task.getAssignee().substring(task.getAssignee().indexOf("[") + 1, task.getAssignee().indexOf("]")).equals(emp.get("V_PERCODE"))) {
                task.setAssigneeName((String) emp.get("V_PERNAME"));
                task.setAssigneeOrgId((String) emp.get("ORG_CODE_"));
                task.setAssigneeOrgName((String) emp.get("ORG_NAME_"));
                break;
            }
        }
    }

    private void fillTask(List<Task> taskList, Map<String, Object> operator) {
        List<String> EMP_CODE_LIST = new ArrayList<String>();
        for (Task task : taskList) {
            if (EMP_CODE_LIST.indexOf(task.getStartUser()) < 0) {
                EMP_CODE_LIST.add(task.getStartUser());
            }
            if (EMP_CODE_LIST.indexOf(task.getAssignee().substring(task.getAssignee().indexOf("[") + 1, task.getAssignee().indexOf("]"))) < 0) {
                EMP_CODE_LIST.add(task.getAssignee().substring(task.getAssignee().indexOf("[") + 1, task.getAssignee().indexOf("]")));
            }
        }

        if (EMP_CODE_LIST.size() == 0) {
            return;
        }

        List<Map<String, Object>> empList = personService.selectPersonByPerCodeList(EMP_CODE_LIST);  //TODO 根据人员编码
        for (Task task : taskList) {
            for (Map<String, Object> emp : empList) {
                if (task.getStartUser().equals(emp.get("EMP_CODE_"))) {
                    task.setStartUserName((String) emp.get("EMP_NAME_"));
                    break;
                }
            }
            for (Map<String, Object> emp : empList) {
                if (task.getAssignee().substring(task.getAssignee().indexOf("[") + 1, task.getAssignee().indexOf("]")).equals(emp.get("EMP_CODE_"))) {
                    task.setAssigneeName((String) emp.get("EMP_NAME_"));
                    task.setAssigneeOrgId((String) emp.get("ORG_CODE_"));
                    task.setAssigneeOrgName((String) emp.get("ORG_NAME_"));
                    break;
                }
            }
        }
    }

    public void updateHistoricVarinst(final String processInstanceId, final String CODE_, final String TITLE_) {
        activitiTransactionTemplate.execute(new TransactionCallbackWithoutResult() {
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    String sql = "update ACT_HI_VARINST set TEXT_ = ? where PROC_INST_ID_ =? and NAME_='CODE_' ";
                    activitiJdbcTemplate.update(sql, new Object[]{CODE_, processInstanceId});

                    sql = "update ACT_HI_VARINST set TEXT_ = ? where PROC_INST_ID_ = ? and NAME_='TITLE_'";
                    activitiJdbcTemplate.update(sql, new Object[]{TITLE_, processInstanceId});

                    historyService.createHistoricVariableInstanceQuery();

                } catch (Exception e) {
                    status.setRollbackOnly();
                    log.info(e.getMessage(), e);
                    throw new RuntimeException(e);
                }
            }
        });

    }

    /**
     * 根据流程定义编码查询最新流程下的第一步候选人列表
     *
     * @param processDefinitionKey 流程定义key
     * @param taskDefinitionKey    任务定义key
     * @return 下一个用户任务节点
     */
    @Override
    public List<Map<String, Object>> selectFirstTaskProcCandidate(String processDefinitionKey, String taskDefinitionKey, Map<String, Object> processVariables, Map<String, Object> operator) {
        List<Map<String, Object>> candidateList = new ArrayList<>();
        List<Map> taskSubProcCandidate = new ArrayList<>();

        // create context
        ExpressionFactory factory = new ExpressionFactoryImpl();
        SimpleContext context = new SimpleContext();
        for (Iterator<String> iterator = processVariables.keySet().iterator(); iterator.hasNext(); ) {
            String key = iterator.next();
            Object processVariable = processVariables.get(key);
            context.setVariable(key, factory.createValueExpression(processVariable, Object.class));
        }
        //context.setVariable("activitiEmpService", factory.createValueExpression(activitiEmpService, ActivitiEmpService.class));

        //获取流程定义Id
        String processDefinitionId = (String) loadProcessDefinitionByProcessDefinitionKey(processDefinitionKey).get("ID_");
        // 查询流程定义
        BpmnModel bpmnModel = getBpmnModel(processDefinitionId);
        // 当前节点定义
        FlowNode flowNode = (FlowNode) bpmnModel.getFlowElement(taskDefinitionKey);

        // taskCandidateList
        if (flowNode instanceof UserTask) {
            List<String> candidateUserExpressionTextList = ((UserTask) flowNode).getCandidateUsers();
            String candidateUserExpressionText = StringUtils.join(candidateUserExpressionTextList, ",");
            ValueExpression expression = new ExpressionFactoryImpl().createValueExpression(context, candidateUserExpressionText, Object.class);
            Object value = expression.getValue(context);
            if (value instanceof String) {
                String[] splits = ((String) value).split(":");
                if ("selectEmpByDeptRole".equals(splits[0])) {
                    candidateList = baseService.selectEmpByDeptRole(transNull(splits[1]), transNull(splits[2]));   //TODO 根据角色专业查人员
                }
            } else {
                throw new RuntimeException("待办候选人数据错误");
            }

            /*ApplicationContext applicationContext = processEngineConfiguration.getApplicationContext();
            Map beans = applicationContext.getBeansOfType(Object.class);
            SpringExpressionManager expressionManager = new SpringExpressionManager(applicationContext, beans);
            Expression expression = expressionManager.createExpression(candidateUserExpressionText);
            MyVariableScope impl = new MyVariableScope(processVariables);
            Context.setProcessEngineConfiguration(processEngineConfiguration);
            List<Map> taskSubProcCandidate = (List<Map>) expression.getValue(impl);*/
        }

        return candidateList;
    }

    @Override
    public InputStream loadProcessDefinitionDiagramByDeploymentId(String deploymentId) {
        InputStream inputStream;

        try {
            org.activiti.engine.repository.ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deploymentId).singleResult();
            inputStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), processDefinition.getDiagramResourceName());
            if (inputStream == null) {
                throw new RuntimeException("未找到流程图");
            }
        } catch (Exception e) {
            log.info(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        return inputStream;
    }

    @Override
    public String submitCountersignTask(String taskId, String approval, Map<String, Object> processVariables) throws Exception {
        org.activiti.engine.task.Task task = taskService.createTaskQuery().taskId(taskId).singleResult();// 获取当前任务
        if ("approve".equals(approval)) {// 通过
            String sid = task.getTaskDefinitionKey();// 获取当前任务的activitiyId
            List<org.activiti.engine.task.Task> list = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).list();// 获取当前流程的所有未完成任务
            if (list.size() == 1) {// 如果只剩一个任务,可能是普通任务,可能是会签任务
                if ("siblingOrgLeader".equals(sid)) {// 如果是会签,则提交到下一个审批节点(并行网关之后的第一个节点)
                    //工作流任意流转,流转到下一个审批节点,因为我的会签里面包含三个节点,所以经过activitiyId来判定是不是这三个节点,如果是,则任意流转节点,如果不是,则正常完成任务,注意:工作流程图确定之后,每个节点的activityId是永恒不变的!因此,才可以做此判定
                    commitProcess(task.getId(), processVariables, "lawManager");
                }
            } else {
                commitProcess(task.getId(), processVariables, "lawManager");
            }
        } else if ("reject".equals(approval)) {// 拒绝
            turnBackNew(task.getId(), "draft");
        } else if ("end".equals(approval)) {// 终止流程
            //endProcess(task.getTaskId(), "sid-404150FC-8E2D-4582-9CFB-BC9FF43F09E1");
        }

        return "123";
    }

    /**
     * @param taskId        任务id
     * @param endActivityId 结束节点的activitiyId
     * @throws Exception
     */
    public void turnBackNew(String taskId, String endActivityId) throws Exception {
        Map<String, Object> variables;
        // 取得当前任务
        HistoricTaskInstance currTask = historyService
                .createHistoricTaskInstanceQuery().taskId(taskId)
                .singleResult();
        // 取得流程实例
        org.activiti.engine.runtime.ProcessInstance instance = runtimeService
                .createProcessInstanceQuery()
                .processInstanceId(currTask.getProcessInstanceId())
                .singleResult();
        if (instance == null) {
            throw new RuntimeException("流程已结束");
        }
        variables = instance.getProcessVariables();
        // 取得流程定义
        ProcessDefinitionEntity definition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
                .getDeployedProcessDefinition(currTask
                        .getProcessDefinitionId());
        if (definition == null) {
            throw new RuntimeException("流程定义未找到");
        }
        /*// 查询本节点发起的会签任务，并结束
        List<org.activiti.engine.task.Task> list = taskService.createTaskQuery().processInstanceId(instance.getId()).list();
        for (org.activiti.engine.task.Task task : list) {
            commitProcess(task.getId(), null, null);
        }

        // 查找所有并行任务节点，同时驳回
        List<org.activiti.engine.task.Task> taskList = taskService.createTaskQuery().processInstanceId(
                findProcessInstanceByTaskId(
                        taskId).getId()).taskDefinitionKey(findTaskById(taskId).getTaskDefinitionKey()).list();
        for (org.activiti.engine.task.Task task : taskList) {
            commitProcess(task.getId(), variables, endActivityId);
        }*/

        // 取得上一步活动
        ActivityImpl currActivity = ((ProcessDefinitionImpl) definition)
                .findActivity(currTask.getTaskDefinitionKey());
        List<ActivityImpl> rtnList = new ArrayList<>();
        List<ActivityImpl> tempList = new ArrayList<>();
        List<ActivityImpl> activities = iteratorBackActivity(
                taskId,
                currActivity,
                rtnList,
                tempList
        );
        if (activities == null || activities.size() <= 0) throw new RuntimeException("没有可以选择的驳回节点!");
        List<org.activiti.engine.task.Task> list = taskService.createTaskQuery().processInstanceId(instance.getId()).list();
        for (org.activiti.engine.task.Task task : list) {
            if (!task.getId().equals(taskId)) {
                task.setAssignee("排除标记");
                commitProcess(task.getId(), null, endActivityId);
            }
        }
        //turnTransition(taskId, activities.get(0).getId(), null);
        turnTransition(taskId, endActivityId, null);
    }

    /**
     * @param taskId     当前任务ID
     * @param variables  流程变量
     * @param activityId 流程转向执行任务节点ID<br>
     *                   此参数为空，默认为提交操作
     * @throws Exception
     */
    private void commitProcess(String taskId, Map<String, Object> variables,
                               String activityId) throws Exception {
        if (variables == null) {
            variables = new HashMap<String, Object>();
        }
        // 跳转节点为空，默认提交操作
        if (activityId == null || activityId.equals("")) {
            taskService.complete(taskId, variables);
        } else {// 流程转向操作
            turnTransition(taskId, activityId, variables);
        }
    }

    /**
     * 清空指定活动节点流向
     *
     * @param activityImpl 活动节点
     * @return 节点流向集合
     */
    private List<PvmTransition> clearTransition(ActivityImpl activityImpl) {
        // 存储当前节点所有流向临时变量
        List<PvmTransition> oriPvmTransitionList = new ArrayList<PvmTransition>();
        // 获取当前节点所有流向，存储到临时变量，然后清空
        List<PvmTransition> pvmTransitionList = activityImpl
                .getOutgoingTransitions();
        for (PvmTransition pvmTransition : pvmTransitionList) {
            oriPvmTransitionList.add(pvmTransition);
        }
        pvmTransitionList.clear();

        return oriPvmTransitionList;
    }

    /**
     * 还原指定活动节点流向
     *
     * @param activityImpl         活动节点
     * @param oriPvmTransitionList 原有节点流向集合
     */
    private void restoreTransition(ActivityImpl activityImpl,
                                   List<PvmTransition> oriPvmTransitionList) {
        // 清空现有流向
        List<PvmTransition> pvmTransitionList = activityImpl
                .getOutgoingTransitions();
        pvmTransitionList.clear();
        // 还原以前流向
        for (PvmTransition pvmTransition : oriPvmTransitionList) {
            pvmTransitionList.add(pvmTransition);
        }
    }

    /**
     * 流程转向操作
     *
     * @param taskId     当前任务ID
     * @param activityId 目标节点任务ID
     * @param variables  流程变量
     * @throws Exception
     */
    private void turnTransition(String taskId, String activityId,
                                Map<String, Object> variables) throws Exception {
        // 当前节点
        ActivityImpl currActivity = findActivitiImpl(taskId, null);
        // 清空当前流向
        List<PvmTransition> oriPvmTransitionList = clearTransition(currActivity);

        // 创建新流向
        TransitionImpl newTransition = currActivity.createOutgoingTransition();
        // 目标节点
        ActivityImpl pointActivity = findActivitiImpl(taskId, activityId);
        // 设置新流向的目标节点
        newTransition.setDestination(pointActivity);

        // 执行转向任务
        taskService.complete(taskId, variables);
        // 删除目标节点新流入
        pointActivity.getIncomingTransitions().remove(newTransition);

        // 还原以前流向
        restoreTransition(currActivity, oriPvmTransitionList);
    }

    /**
     * 迭代循环流程树结构，查询当前节点可驳回的任务节点
     *
     * @param taskId       当前任务ID
     * @param currActivity 当前活动节点
     * @param rtnList      存储回退节点集合
     * @param tempList     临时存储节点集合（存储一次迭代过程中的同级userTask节点）
     * @return 回退节点集合
     */
    private List<ActivityImpl> iteratorBackActivity(String taskId,
                                                    ActivityImpl currActivity, List<ActivityImpl> rtnList,
                                                    List<ActivityImpl> tempList) throws Exception {
        // 查询流程定义，生成流程树结构
        org.activiti.engine.runtime.ProcessInstance processInstance = findProcessInstanceByTaskId(taskId);

        // 当前节点的流入来源
        List<PvmTransition> incomingTransitions = currActivity
                .getIncomingTransitions();
        // 条件分支节点集合，userTask节点遍历完毕，迭代遍历此集合，查询条件分支对应的userTask节点
        List<ActivityImpl> exclusiveGateways = new ArrayList<ActivityImpl>();
        // 并行节点集合，userTask节点遍历完毕，迭代遍历此集合，查询并行节点对应的userTask节点
        List<ActivityImpl> parallelGateways = new ArrayList<ActivityImpl>();
        // 遍历当前节点所有流入路径
        for (PvmTransition pvmTransition : incomingTransitions) {
            TransitionImpl transitionImpl = (TransitionImpl) pvmTransition;
            ActivityImpl activityImpl = transitionImpl.getSource();
            String type = (String) activityImpl.getProperty("type");
            /**
             * 并行节点配置要求：<br>
             * 必须成对出现，且要求分别配置节点ID为:XXX_start(开始)，XXX_end(结束)
             */
            if ("parallelGateway".equals(type)) {// 并行路线
                String gatewayId = activityImpl.getId();
                String gatewayType = gatewayId.substring(gatewayId
                        .lastIndexOf("_") + 1);
                if ("START".equals(gatewayType.toUpperCase())) {// 并行起点，停止递归
                    return rtnList;
                } else {// 并行终点，临时存储此节点，本次循环结束，迭代集合，查询对应的userTask节点
                    parallelGateways.add(activityImpl);
                }
            } else if ("startEvent".equals(type)) {// 开始节点，停止递归
                return rtnList;
            } else if ("userTask".equals(type)) {// 用户任务
                tempList.add(activityImpl);
            } else if ("exclusiveGateway".equals(type)) {// 分支路线，临时存储此节点，本次循环结束，迭代集合，查询对应的userTask节点
                currActivity = transitionImpl.getSource();
                exclusiveGateways.add(currActivity);
            }
        }

        /**
         * 迭代条件分支集合，查询对应的userTask节点
         */
        for (ActivityImpl activityImpl : exclusiveGateways) {
            iteratorBackActivity(taskId, activityImpl, rtnList, tempList);
        }

        /**
         * 迭代并行集合，查询对应的userTask节点
         */
        for (ActivityImpl activityImpl : parallelGateways) {
            iteratorBackActivity(taskId, activityImpl, rtnList, tempList);
        }

        /**
         * 根据同级userTask集合，过滤最近发生的节点
         */
        currActivity = filterNewestActivity(processInstance, tempList);
        if (currActivity != null) {
            // 查询当前节点的流向是否为并行终点，并获取并行起点ID
            String id = findParallelGatewayId(currActivity);
            if (id == null || id.equals("")) {// 并行起点ID为空，此节点流向不是并行终点，符合驳回条件，存储此节点
                rtnList.add(currActivity);
            } else {// 根据并行起点ID查询当前节点，然后迭代查询其对应的userTask任务节点
                currActivity = findActivitiImpl(taskId, id);
            }

            // 清空本次迭代临时集合
            tempList.clear();
            // 执行下次迭代
            iteratorBackActivity(taskId, currActivity, rtnList, tempList);
        }
        return rtnList;
    }

    /**
     * 根据当前节点，查询输出流向是否为并行终点，如果为并行终点，则拼装对应的并行起点ID
     *
     * @param activityImpl 当前节点
     * @return
     */
    private String findParallelGatewayId(ActivityImpl activityImpl) {
        List<PvmTransition> incomingTransitions = activityImpl
                .getOutgoingTransitions();
        for (PvmTransition pvmTransition : incomingTransitions) {
            TransitionImpl transitionImpl = (TransitionImpl) pvmTransition;
            activityImpl = transitionImpl.getDestination();
            String type = (String) activityImpl.getProperty("type");
            if ("parallelGateway".equals(type)) {// 并行路线
                String gatewayId = activityImpl.getId();
                String gatewayType = gatewayId.substring(gatewayId
                        .lastIndexOf("_") + 1);
                if ("END".equals(gatewayType.toUpperCase())) {
                    return gatewayId.substring(0, gatewayId.lastIndexOf("_"))
                            + "_start";
                }
            }
        }
        return null;
    }

    /**
     * 根据流入任务集合，查询最近一次的流入任务节点
     *
     * @param processInstance 流程实例
     * @param tempList        流入任务集合
     * @return
     */
    private ActivityImpl filterNewestActivity(org.activiti.engine.runtime.ProcessInstance processInstance,
                                              List<ActivityImpl> tempList) {
        while (tempList.size() > 0) {
            ActivityImpl activity_1 = tempList.get(0);
            HistoricActivityInstance activityInstance_1 = findHistoricUserTask(
                    processInstance, activity_1.getId());
            if (activityInstance_1 == null) {
                tempList.remove(activity_1);
                continue;
            }

            if (tempList.size() > 1) {
                ActivityImpl activity_2 = tempList.get(1);
                HistoricActivityInstance activityInstance_2 = findHistoricUserTask(
                        processInstance, activity_2.getId());
                if (activityInstance_2 == null) {
                    tempList.remove(activity_2);
                    continue;
                }

                if (activityInstance_1.getEndTime().before(
                        activityInstance_2.getEndTime())) {
                    tempList.remove(activity_1);
                } else {
                    tempList.remove(activity_2);
                }
            } else {
                break;
            }
        }
        if (tempList.size() > 0) {
            return tempList.get(0);
        }
        return null;
    }

    /**
     * 查询指定任务节点的最新记录
     *
     * @param processInstance 流程实例
     * @param activityId
     * @return
     */
    private HistoricActivityInstance findHistoricUserTask(
            org.activiti.engine.runtime.ProcessInstance processInstance, String activityId) {
        HistoricActivityInstance rtnVal = null;
        // 查询当前流程实例审批结束的历史节点
        List<HistoricActivityInstance> historicActivityInstances = historyService
                .createHistoricActivityInstanceQuery().activityType("userTask")
                .processInstanceId(processInstance.getProcessInstanceId()).activityId(
                        activityId).finished()
                .orderByHistoricActivityInstanceEndTime().desc().list();
        if (historicActivityInstances.size() > 0) {
            rtnVal = historicActivityInstances.get(0);
        }

        return rtnVal;
    }

    /**
     * 根据任务ID和节点ID获取活动节点 <br>
     *
     * @param taskId     任务ID
     * @param activityId 活动节点ID <br>
     *                   如果为null或""，则默认查询当前活动节点 <br>
     *                   如果为"end"，则查询结束节点 <br>
     * @return
     * @throws Exception
     */
    private ActivityImpl findActivitiImpl(String taskId, String activityId)
            throws Exception {
        // 取得流程定义
        ProcessDefinitionEntity processDefinition = findProcessDefinitionEntityByTaskId(taskId);

        // 获取当前活动节点ID
        if (activityId == null || "".equals(activityId)) {
            activityId = findTaskById(taskId).getTaskDefinitionKey();
        }

        // 根据流程定义，获取该流程实例的结束节点
        if (activityId.toUpperCase().equals("END")) {
            for (ActivityImpl activityImpl : processDefinition.getActivities()) {
                List<PvmTransition> pvmTransitionList = activityImpl
                        .getOutgoingTransitions();
                if (pvmTransitionList.isEmpty()) {
                    return activityImpl;
                }
            }
        }

        // 根据节点ID，获取对应的活动节点
        ActivityImpl activityImpl = ((ProcessDefinitionImpl) processDefinition)
                .findActivity(activityId);

        return activityImpl;
    }

    /**
     * 根据任务ID获取流程定义
     *
     * @param taskId 任务ID
     * @return
     * @throws Exception
     */
    private ProcessDefinitionEntity findProcessDefinitionEntityByTaskId(
            String taskId) throws Exception {
        // 取得流程定义
        ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
                .getDeployedProcessDefinition(findTaskById(taskId)
                        .getProcessDefinitionId());

        if (processDefinition == null) {
            throw new Exception("流程定义未找到!");
        }

        return processDefinition;
    }

    /**
     * 根据任务ID获得任务实例
     *
     * @param taskId 任务ID
     * @return
     * @throws Exception
     */
    private TaskEntity findTaskById(String taskId) throws Exception {
        TaskEntity task = (TaskEntity) taskService.createTaskQuery().taskId(
                taskId).singleResult();
        if (task == null) {
            throw new Exception("任务实例未找到!");
        }
        return task;
    }

    /**
     * 根据任务ID获取对应的流程实例
     *
     * @param taskId 任务ID
     * @return
     * @throws Exception
     */
    private org.activiti.engine.runtime.ProcessInstance findProcessInstanceByTaskId(String taskId)
            throws Exception {
        // 找到流程实例
        org.activiti.engine.runtime.ProcessInstance processInstance = runtimeService
                .createProcessInstanceQuery().processInstanceId(
                        findTaskById(taskId).getProcessInstanceId())
                .singleResult();
        if (processInstance == null) {
            throw new Exception("流程实例未找到!");
        }
        return processInstance;
    }

    @Override
    public List<ActivitiVariable> getInstanceVariables(String instanceId) {
        try {
            List<ActivitiVariable> variables = new ArrayList<>();
            List<HistoricVariableInstance> vars = historyService
                    .createHistoricVariableInstanceQuery()
                    .processInstanceId(instanceId).list();
            for (HistoricVariableInstance var : vars) {
                ActivitiVariable av = new ActivitiVariable();
                av.setName(var.getVariableName());
                av.setValue(var.getValue() == null ? "null" : var.getValue());
                av.setType(var.getVariableTypeName());
                variables.add(av);
            }
            return variables;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
