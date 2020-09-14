package com.hg.lib.treeview;


import com.hg.lib.treeview.base.model.NodeId;

public class TreeBean implements NodeId {

    private String id;
    private String pId;
    private String name;


    public TreeBean(String id, String pId, String name) {
        this.id = id;
        this.pId = pId;
        this.name = name;
    }

    public String getpId() {
        return pId;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getPId() {
        return pId;
    }
}
