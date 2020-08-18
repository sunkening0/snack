import com.fasterxml.jackson.databind.node.ObjectNode;
import com.keelin.ActivitiApplication;
import com.keelin.entity.Holiday;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricVariableInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试设置流程变量
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ActivitiApplication.class})
public class TestSetProcessVar {

    /**
     *  在启动节点设置流程变量
     */
    @Test
    public void assigneeValue(){
        //1.得到ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        RuntimeService runtimeService = processEngine.getRuntimeService();

        //设置assignee的值
        Map<String,Object> map = new HashMap<>();
        map.put("assignee0","zhangsan");
        map.put("assignee1","lisi");
        map.put("assignee2","wangwu");

        //启动流程实例  同时还要设置流程实例的assignee的值
        runtimeService.startProcessInstanceByKey("myProcess_8",map);
    }

    /**
     * 在任务中设置变量
     */
    @Test
    public void test_completeTask(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        RepositoryService repositoryService =   processEngine.getRepositoryService();
        Map<String,Object> varlues = new HashMap();
        varlues.put("applyTitle","test_请假申请流程");
        varlues.put("applyTime","7天");
        varlues.put("applyReason","结婚");
        TaskService taskService =processEngine.getTaskService();
        taskService.complete("71059883-e057-11ea-8cc6-1063c85592f7",varlues);  //5005 taskId
    }

    /**
     * 设置流程变量数据（全局）  在当前任务节点以后的任何一个任务节点  都能用当前任务id获取
     */
    @Test
    public void setVariableValues(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        TaskService taskService=processEngine.getTaskService(); // 任务Service
        String taskId="b11253a8-e038-11ea-8c4d-1063c85592f7";
        taskService.setVariable(taskId, "days", 2);
        taskService.setVariable(taskId, "date", new Date());
        taskService.setVariable(taskId, "reason", "发烧");
        Holiday holiday = new Holiday();
        holiday.setApplyName("Ted1");
        holiday.setManagerName("Karen1");
        taskService.setVariable(taskId, "holiday1", holiday); // 存序列化对象
    }


    /**
     * 获取流程变量数据
     */
    @Test
    public void getVariableValues(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        TaskService taskService=processEngine.getTaskService(); // 任务Service
        String taskId="8f98ab36-e03f-11ea-be41-1063c85592f7";
        Integer days=(Integer) taskService.getVariable(taskId, "days");
        Date date=(Date) taskService.getVariable(taskId, "date");
        String reason=(String) taskService.getVariable(taskId, "reason");
        ObjectNode objectNode = (ObjectNode)taskService.getVariable(taskId, "holiday1");

        System.out.println("请假天数："+days);
        System.out.println("请假日期："+date);
        System.out.println("请假原因："+reason);

        System.out.println("请假对象："+objectNode.get("applyName")+","+objectNode.get("managerName"));
    }

    /**
     * 设置局部变量   局部流程变量只能在该任务节点中设置和获取
     */
    @Test
    public void setLocalVariableValues(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        TaskService taskService=processEngine.getTaskService(); // 任务Service
        String taskId="8f98ab36-e03f-11ea-be41-1063c85592f7";
        taskService.setVariableLocal(taskId,"datelocal", new Date());
    }

    /**
     * 获取局部变量   局部流程变量只能在该任务节点中设置和获取
     */
    @Test
    public void getLocalVariableValues(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        TaskService taskService=processEngine.getTaskService(); // 任务Service
        String taskId="8f98ab36-e03f-11ea-be41-1063c85592f7";
        Date date=(Date) taskService.getVariableLocal(taskId, "datelocal");
        System.out.println(date);
    }

    /**
     * 得到流程变量
     */
    @Test
    public void getVariable1() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        HistoryService historyService = processEngine.getHistoryService();
        List<HistoricVariableInstance> list = historyService.createHistoricVariableInstanceQuery().taskId("d8ed53f0-e057-11ea-a67d-1063c85592f7").list();
        for (HistoricVariableInstance hvi : list) {
            System.out.println("变量ID:"+hvi.getId());
            System.out.println("变量类型:"+hvi.getVariableTypeName());
            System.out.println("变量名称:"+hvi.getVariableName());
            System.out.println("变量值:"+hvi.getValue());
            System.out.println("#####################");
        }
    }


}
