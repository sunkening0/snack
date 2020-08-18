import com.keelin.ActivitiApplication;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 连线（SequenceFlow）
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ActivitiApplication.class})
public class TestSequenceFlowProcess {

    /**部署流程定义+启动流程实例*/
    @Test
    public void deployementAndStartProcess(){
        //创建ProcessEngine对象

        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        RepositoryService repositoryService = processEngine.getRepositoryService();

        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("processes/test1.bpmn")
                .name("测试连线流程")
                .deploy();

        System.out.println(deployment.getId());
        System.out.println(deployment.getName());

        //启动流程实例
        Map<String,Object> map = new HashMap<>();
        map.put("bmjl","zhangsan");
        map.put("zjl","lisi");
        ProcessInstance pi = processEngine.getRuntimeService()//
                .startProcessInstanceByKey("SequenceFlowDemoProcess",map);//使用流程定义的key的最新版本启动流程
        System.out.println("流程实例ID："+pi.getId());
        System.out.println("流程定义的ID："+pi.getProcessDefinitionId());
    }


    /**查询我的个人任务*/

    @Test

    public void findPersonalTaskList(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //任务办理人
        String assignee = "zhangsan";
        List<Task> list = processEngine.getTaskService()//
                .createTaskQuery()//
                .taskAssignee(assignee)//个人任务的查询
                .list();

        if(list!=null && list.size()>0){
            for(Task task:list){
                System.out.println("任务ID："+task.getId());
                System.out.println("任务的办理人："+task.getAssignee());
                System.out.println("任务名称："+task.getName());
                System.out.println("任务的创建时间："+task.getCreateTime());
                System.out.println("流程实例ID："+task.getProcessInstanceId());
                System.out.println("#######################################");
            }
        }
    }


    /**完成任务*/

    @Test
    public void completeTask(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //任务ID
        String taskId = "e2df2943-e0f0-11ea-a3d4-1063c85592f7";
        //完成任务的同时，设置流程变量，让流程变量判断连线该如何执行
        Map<String, Object> variables = new HashMap<String, Object>();
        //其中message对应sequenceFlow.bpmn中的${message=='不重要'}，不重要对应流程变量的值
        variables.put("message", "0");
        processEngine.getTaskService()//
                .complete(taskId,variables);
        System.out.println("完成任务："+taskId);
    }


}
