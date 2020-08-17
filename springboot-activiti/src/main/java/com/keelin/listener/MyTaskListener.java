package com.keelin.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 *  assignee的值用监听器实现
 */
public class MyTaskListener implements TaskListener {


    @Override
    public void notify(DelegateTask delegateTask) {
        //这里指定任务负责人
        delegateTask.setAssignee("sara");

        Object holiday = delegateTask.getVariable("holiday");
    }
}
