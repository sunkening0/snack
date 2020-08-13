import com.keelin.ActivitiApplication;
import com.keelin.entity.Holiday;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ActivitiApplication.class})
public class ActivitiApiTest {
    /**
     * 部署流程
     */
    @Test
    public void deploy(){
        //创建ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        RepositoryService repositoryService = processEngine.getRepositoryService();

        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("processes/test.bpmn")
                .addClasspathResource("processes/test.png")
                .name("请假申请流程")
                .deploy();

        System.out.println(deployment.getId());
        System.out.println(deployment.getName());
    }

    /**
     * 流程实例化
     */
    @Test
    public void instance(){
        //创建ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        RuntimeService runtimeService = processEngine.getRuntimeService();

        //key是唯一标识
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("myProcess_4");

        System.out.println("流程部署ID："+processInstance.getDeploymentId());
        System.out.println("流程实例ID："+processInstance.getId());
        System.out.println("流程定义ID："+processInstance.getProcessDefinitionId());
        System.out.println("活动ID："+processInstance.getActivityId());
    }

    /**
     * 处理任务
     */
    @Test
    public void completeTask(){
        //1.得到ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        //2.得到TaskService对象
        TaskService taskService = processEngine.getTaskService();

        //3.根据流程定义的key，负责人assignee来实现当前用户的任务列表查询
        List<Task> taskList = taskService.createTaskQuery().processInstanceId("a15423e0-dd66-11ea-94d0-1063c85592f7")
                .taskAssignee("lisi")
                .list();
        for(Task task : taskList){
            System.out.println(task.getId());
            taskService.complete(task.getId());
        }
    }


    /**
     * 查询任务列表
     */
    @Test
    public void queryTaskList(){
        //1.得到ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        HistoryService historyService = processEngine.getHistoryService();

        HistoricActivityInstanceQuery historicActivityInstanceQuery = historyService.createHistoricActivityInstanceQuery();

        historicActivityInstanceQuery.processInstanceId("45edce36-dd56-11ea-a11a-1063c85592f7");

        //2.得到TaskService对象
        TaskService taskService = processEngine.getTaskService();

        List<HistoricActivityInstance> list = historicActivityInstanceQuery.orderByHistoricActivityInstanceStartTime().asc().list();

        for(HistoricActivityInstance historicActivityInstance : list){
            System.out.println("流程实例ID："+historicActivityInstance.getProcessInstanceId());
            System.out.println(historicActivityInstance.getProcessDefinitionId());
            System.out.println(historicActivityInstance.getActivityId());
            System.out.println(historicActivityInstance.getActivityName());
        }
    }

    /**
     * 流程定义挂起和激活
     * 目的：如果流程发生改动，可以先挂起流程，所有的流程实例不能继续流转，更新流程定义，然后激活流程定义。或者不激活直接新增一个流程定义
     * 流程定义为挂起状态该流程定义将不允许启动新的流程实例，同时该流程定义下所有的流程实例将全部挂起暂停执行。
     */
    @Test
    public void suspendAndActive(){
        //1.得到ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        RepositoryService repositoryService = processEngine.getRepositoryService();
        //查询流程定义对象
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("myProcess_1").singleResult();

        //获取当i请安流程定义的实例是否暂停状态
        boolean suspended = processDefinition.isSuspended();

        String processDefinitionId = processDefinition.getId();

        //判断
        if(suspended){
            //如果挂起 则激活
            repositoryService.activateProcessDefinitionById(processDefinitionId,true,null);;
            System.out.println("流程定义ID："+processDefinitionId+" 激活");
        }else{
            //如果激活 则挂起
            repositoryService.suspendProcessDefinitionById(processDefinitionId,true,null);
            System.out.println("流程定义ID："+processDefinitionId+" 挂起");
        }
    }

    /**
     * 单个流程实例的花旗和激活
     */
    @Test
    public void singleSuspendAndActive(){
        //1.得到ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        RuntimeService runtimeService = processEngine.getRuntimeService();

        //查询流程实例
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId("45edce36-dd56-11ea-a11a-1063c85592f7")
                .singleResult();

        //获取当前流程定义的实例是否为挂起状态
        boolean suspended = processInstance.isSuspended();

        String processDefinitionId = processInstance.getId();

        //判断
        if(suspended){
            //如果挂起 则激活
            runtimeService.activateProcessInstanceById(processDefinitionId);;
            System.out.println("流程定义ID："+processDefinitionId+" 激活");
        }else{
            //如果激活 则挂起
            runtimeService.suspendProcessInstanceById(processDefinitionId);
            System.out.println("流程定义ID："+processDefinitionId+" 挂起");
        }
    }

    /**
     *  assignee的值用UEL实现    设置assignee   影响表：act_ru_identitylink（运行时用户信息表）
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
        runtimeService.startProcessInstanceByKey("myProcess_1",map);

    }


    /**
     * assignee的值用UEL的POJO实现
     */
    @Test
    public void startProcessAndAssigneeValue() {
        //1. 通过工具类（ProcessEngines）获取流程引擎实例
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        //2. 得到RuntimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();

        //3. 设置assignee的取值，用户在界面上设置流程的执行人
        Map<String, Object> map = new HashMap<>();
        Holiday holiday = new Holiday();
        holiday.setApplyName("Ted");
        holiday.setManagerName("Karen");
        map.put("holiday", holiday);

        //4. 启动流程实例，同时还要设置流程定义的assignee的值
        ProcessInstance processInstance =  runtimeService.startProcessInstanceByKey("myProcess_8", map);
        System.out.println("流程实例ID："+ processInstance.getId());
    }



}
