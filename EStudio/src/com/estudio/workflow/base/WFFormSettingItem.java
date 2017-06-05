package com.estudio.workflow.base;

/**
 * 表单控件设置
 * 
 * @author ShengHongL
 * 
 */
public class WFFormSettingItem {
    String name; // 控件名称
    boolean hidden; // 是否隐藏
    boolean readonly; // 是否只读
    boolean require; // 是否必须

    /**
     * 构造函数
     * 
     * @param name
     *            控件名称
     * @param hidden
     *            是否隐藏
     * @param readonly
     *            是否只读
     */
    public WFFormSettingItem(final String name, final boolean hidden, final boolean readonly, final boolean require) {
        super();
        this.name = name;
        this.hidden = hidden;
        this.readonly = readonly;
        this.require = require;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(final boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public void setReadonly(final boolean readonly) {
        this.readonly = readonly;
    }

    public boolean isRequire() {
        return require;
    }

    public void setRequire(final boolean require) {
        this.require = require;
    }

}
