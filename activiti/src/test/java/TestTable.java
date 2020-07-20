import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.util.List;

public class TestTable {

    /**
     * 建表
     */
    @Test
    public void testTable(){
        ProcessEngineConfiguration configuration = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml");
        ProcessEngine processEngine = configuration.buildProcessEngine();
        System.out.print(processEngine);
    }

    /**
     * 流程部署
     */
    @Test
    public void deployProcess(){
        //创建ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        RepositoryService repositoryService = processEngine.getRepositoryService();

        Deployment deployment = repositoryService.createDeployment().addClasspathResource("bpmn/test.bpmn")
                .addClasspathResource("bpmn/test.png")
                .name("请假申请流程")
                .deploy();

        System.out.println(deployment.getId());
        System.out.println(deployment.getName());
    }

    /**
     * 流程实例启动
     * 流程定义（就是我们画的bpmn图，也就是bpmn文件，本身是xml文件）
     * 流程部署（就是把流程定义的内容持久化到数据库中）流程定义的部署
     * 流程实例，流程定义好比是java中的一个类，而流程实例好比java中实例对象。
     * 一个流程定义可以对应多个流程实例。
     */
    @Test
    public void startProcess(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        RuntimeService runtimeService = processEngine.getRuntimeService();

        //key是唯一标识
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("myProcess_1");

        System.out.println("流程部署ID："+processInstance.getDeploymentId());
        System.out.println("流程实例ID："+processInstance.getId());
        System.out.println("流程定义ID："+processInstance.getProcessDefinitionId());
        System.out.println("活动ID："+processInstance.getActivityId());
    }


    /**
     * 查询任务列表
     */
    @Test
    public void queryTaskList(){
        //1.得到ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        //2.得到TaskService对象
        TaskService taskService = processEngine.getTaskService();

        //3.根据流程定义的key，负责人assignee来实现当前用户的任务列表查询
        List<Task> taskList = taskService.createTaskQuery()
                .processDefinitionKey("myProcess_1")
                .taskAssignee("keelin")
                .list();
        for(Task task : taskList){
            System.out.println("流程实例ID："+task.getProcessInstanceId());
            System.out.println("任务ID："+task.getId());
            System.out.println("任务负责人："+task.getAssignee());
            System.out.println("任务名称："+task.getName());
        }
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
        List<Task> taskList = taskService.createTaskQuery()
                .processDefinitionKey("myProcess_1")
                .taskAssignee("wangwu")
                .list();
        for(Task task : taskList){
            taskService.complete(task.getId());
        }
    }

    /**
     * 流程定义信息查询
     */
    @Test
    public void queryProcessDefinitionInfo(){
        //1.得到ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        //2.得到RepositoryService对象
        RepositoryService repositoryService = processEngine.getRepositoryService();

        //3.得到ProcessDefinitionQuery对象，可以认为它是一个查询器
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();

        //4.设置条件 并查询出当前的所有流程定义
        List<ProcessDefinition> list = processDefinitionQuery.processDefinitionKey("myProcess_1")
                .orderByProcessDefinitionVersion()
                .desc()
                .list();

        //5.输出流程定义信息
        for(ProcessDefinition processDefinition : list){
            System.out.println("流程定义ID："+processDefinition.getId());
            System.out.println("流程定义名称："+processDefinition.getName());
            System.out.println("流程定义key："+processDefinition.getKey());
            System.out.println("流程定义版本号："+processDefinition.getVersion());
        }
    }

}
