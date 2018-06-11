package com.csilberg.aws.enums;

public enum ActionControl {
    CREATETABLE("create-table"),
    ADDITEM("add-item"),
    GETITEM("get-item"),
    LISTTABLES("list-tables"),
    DELETEITEM("delete-item"),
    DELETETABLE("delete-table"),
    PRODUCTADDITEM("product-addItem"),
    PRODUCTGETITEM("product-getItem"),
    PRODUCTDELETEITEM("product-deleteItem"),
    GETITEMMAPPER("getItemMapper"),
    WRITEITEMMAPPER("writeItemMapper"),
    TESTMAPPER("testMapper"),
    WRITECATALOGITEM("writeCatalogItem"),
    SCANITEM("scan-item");

    private String action;

    ActionControl(String action) {
        this.action = action;
    }

    public String action() {
        return action;
    }

    public static ActionControl getAction(String action){
        ActionControl ctrls [] = ActionControl.values();
        for(ActionControl ctrl : ctrls) {
            if (ctrl.action() == action) {
                return ctrl;
            }
        }
            return  null;
    }
}
