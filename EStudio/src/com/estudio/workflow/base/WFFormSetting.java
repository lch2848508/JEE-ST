package com.estudio.workflow.base;

import java.util.ArrayList;
import java.util.List;

public class WFFormSetting {
    long id; // 表单ID
    String caption; // 表单标题
    boolean readonly = false;
    boolean hidden = false;
    boolean require = false;

    public boolean isReadonly() {
        return readonly;
    }

    public void setReadonly(final boolean readonly) {
        this.readonly = readonly;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(final boolean hidden) {
        this.hidden = hidden;
    }

    List<WFFormSettingItem> controls = new ArrayList<WFFormSettingItem>(); // 表单控件设置

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(final String caption) {
        this.caption = caption;
    }

    public List<WFFormSettingItem> getControls() {
        return controls;
    }

    public boolean isRequire() {
        return require;
    }

    public void setRequire(final boolean require) {
        this.require = require;
    }

}
