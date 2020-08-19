import com.keelin.ActivitiApplication;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.transformer.Identity;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 组任务
 *
 * 组任务办理流程
 * 1、查询组任务（指定候选人，查询其当前待办任务，候选人不能处理任务）
 * 2、拾取任务（该组的所有候选人都能拾取，拾取之后，将候选人的组任务变成了个人任务，原来的候选人就变成了任务的负责人。
 *            如果拾取后不想办理该任务，需要将已经拾取的个人任务归还到组里边，将个人任务改为组任务）
 * 3、查询个人任务
 * 4、办理个人任务
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ActivitiApplication.class})
public class TestActivitiCandidateUsersProcess {

    /**部署流程定义+启动流程实例*/
    @Test
    public void deployementAndStartProcess(){
        //创建ProcessEngine对象

        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        RepositoryService repositoryService = processEngine.getRepositoryService();

        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("processes/test4.bpmn")
                .name("测试组任务（CandidateUsersDemoProcess）流程")
                .deploy();

        System.out.println(deployment.getId());
        System.out.println(deployment.getName());

        //启动流程实例
        ProcessInstance pi = processEngine.getRuntimeService()//
                .startProcessInstanceByKey("CandidateUsersDemoProcess");//使用流程定义的key的最新版本启动流程
        System.out.println("流程实例ID："+pi.getId());
        System.out.println("流程定义的ID："+pi.getProcessDefinitionId());
    }


    /**
     * 查询组任务
     */
    @Test
    public  void getGropTask(){
        //获取ProcessEngine对象   默认配置文件名称：activiti.cfg.xml  并且configuration的Bean实例ID为processEngineConfiguration
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //获取TaskService
        TaskService taskService = processEngine.getTaskService();
        //查询组任务
        List<Task> list = taskService.createTaskQuery().processInstanceId("fd7e5897-e115-11ea-930e-1063c85592f7").taskCandidateUser("小红").list();
        for (Task task:list){
            System.out.println("任务ID:"+task.getId());
            System.out.println("任务名称："+task.getName());
            System.out.println("任务处理人："+task.getAssignee());
        }
    }

    /**
     * 拾取任务
     */
    @Test
    public void claimTask() {
        //获取ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //获取TaskService对象
        TaskService taskService = processEngine.getTaskService();
        //获取组任务
        String candidateUser="小明";
        Task task = taskService.createTaskQuery().processInstanceId("fd7e5897-e115-11ea-930e-1063c85592f7").taskCandidateUser(candidateUser).singleResult();
        //不等于Null代表能够获取到该任务
        if(task!=null){
            //拾取任务   任务ID，任务执行人
            taskService.claim(task.getId(),candidateUser);
            System.out.println("任务被："+candidateUser+"拾取~");
        }
    }

    /**
     * 任务退回/交接
     */
    @Test
    public void taskReturn(){
        //获取ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //获取TaskService对象
        TaskService taskService = processEngine.getTaskService();
        //查询到自己的任务，然后通过任务ID进行提交
        Task task = taskService.createTaskQuery().processInstanceId("fd7e5897-e115-11ea-930e-1063c85592f7").taskAssignee("小明").singleResult();

        if(task!=null){
            //任务退回  任务ID    null代表没有处理人执行，需要后续再次拾取任务
            //任务交接 任务ID     如果第二个参数，处理人不为空代表将该任务交给次处理人
            taskService.setAssignee(task.getId(),"wangnan");
            System.out.println("任务退回");
        }
    }

    /**
     * 查看任务 完成任务
     */
    @Test
    public void getTask() {
        //获取ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //获取TaskService对象
        TaskService taskService = processEngine.getTaskService();
        //查询到自己的任务，然后通过任务ID进行提交
        Task task = taskService.createTaskQuery().processInstanceId("fd7e5897-e115-11ea-930e-1063c85592f7").taskAssignee("小明").singleResult();
        //处理任务
        if(task!=null){
            taskService.complete(task.getId());
            System.out.println("任务处理完毕");

        }
    }


}
