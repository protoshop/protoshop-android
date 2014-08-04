package com.ctrip.protoshop.mini.model;

import java.util.ArrayList;
import java.util.List;

public class PageModel {
    public String id;
    public String background;
    public String name = "";
    public String order = "0";
    public List<LinkModel> elements =new ArrayList<LinkModel>();
    
    public boolean isEditModel=false;
    public boolean isCurPage=false;
    public boolean isLinkPage=false;

}
