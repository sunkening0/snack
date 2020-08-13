package com.keelin.entity;

import java.io.Serializable;

public class Holiday implements Serializable {
    private String applyName;
    private String managerName;

    public String getApplyName() {
        return applyName;
    }

    public void setApplyName(String applyName) {
        this.applyName = applyName;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }
}


