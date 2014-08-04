package com.ctrip.protoshop.mini.model;

import java.util.ArrayList;
import java.util.List;

public class LinkModel {
    public String id;
    public String posX;
    public String posY;
    public String width;
    public String height;
    public String type = "hotspot";
    public List<ActionModel> actions = new ArrayList<ActionModel>(1);
}
