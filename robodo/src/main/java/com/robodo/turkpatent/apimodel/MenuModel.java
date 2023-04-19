package com.robodo.turkpatent.apimodel;

import java.util.ArrayList;
import java.util.List;

public class MenuModel {
	public String displayName;
    public String iconName;
    public List<MenuChild> children=new  ArrayList<MenuChild>();
    
    
    
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getIconName() {
		return iconName;
	}
	public void setIconName(String iconName) {
		this.iconName = iconName;
	}
	public List<MenuChild> getChildren() {
		return children;
	}
	public void setChildren(List<MenuChild> children) {
		this.children = children;
	}
    
    
}
