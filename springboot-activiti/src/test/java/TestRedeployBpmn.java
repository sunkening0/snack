import com.keelin.ActivitiApplication;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ActivitiApplication.class})
public class TestRedeployBpmn {
    /**
     * 对流程图bpmn文件进行修改之后  需要重新对流程进行部署
     * 1、可以先删除原来的流程  在重新部署
     * 2、不删除  直接部署
     * 为了保证已经进行的流程实例数据适用于新的流程，在删除或更新流程之后，需要执行一下操作
     * update act_ru_task set proc_def_id_ = 'key_hftb:6:1025015' where proc_def_id_ = 'key_hftb:5:905020';
     * update act_hi_taskinst set proc_def_id_ = 'key_hftb:6:1025015' where proc_def_id_ = 'key_hftb:5:905020';
     * update act_hi_procinst set proc_def_id_ = 'key_hftb:6:1025015' where proc_def_id_ = 'key_hftb:5:905020';
     * update act_hi_actinst set proc_def_id_ = 'key_hftb:6:1025015' where proc_def_id_ = 'key_hftb:5:905020';
     * update act_ru_execution set proc_def_id_ = 'key_hftb:6:1025015' where proc_def_id_ = 'key_hftb:5:905020';
     */

}
