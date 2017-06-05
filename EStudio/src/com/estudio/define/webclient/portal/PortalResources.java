package com.estudio.define.webclient.portal;

import java.util.ArrayList;

import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

public class PortalResources {
    private class PortalResourceItem {
        String name;
        String text;
        String title;
        String icon;

        public PortalResourceItem(final String name, final String text, final String title, final String icon) {
            super();
            this.name = name;
            this.text = text;
            this.title = title;
            this.icon = icon;
        }
    }

    private final ArrayList<PortalResourceItem> resourceList = new ArrayList<PortalResourceItem>();
    private String folderGif;

    public void addResource(final String name, final String text, final String title, final String iconFileName) {
        final String icon = iconFileName.replace(".bmp", ".png");
        resourceList.add(new PortalResourceItem(name, text, title, icon));
        if (StringUtils.equals(name, "TREE_ICON"))
            folderGif = icon;
    }

    /**
     * 生成JSON对象
     * 
     * @return
     * @throws JSONException
     */
    public JSONObject toJSON() {
        final JSONObject json = new JSONObject();
        for (int i = 0; i < resourceList.size(); i++) {
            final PortalResourceItem item = resourceList.get(i);
            final JSONObject itemJSON = new JSONObject();
            itemJSON.put("Text", item.text);
            itemJSON.put("Title", item.title);
            itemJSON.put("Icon", item.icon);
            json.put(item.name, itemJSON);
        }
        return json;
    }

    /**
     * 目录节点图标
     * 
     * @return
     */
    public String getFolderGif() {
        return folderGif;
    }
}
