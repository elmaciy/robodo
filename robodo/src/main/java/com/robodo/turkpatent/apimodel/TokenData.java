package com.robodo.turkpatent.apimodel;

import java.util.ArrayList;
import java.util.List;

public class TokenData {
	public String jwttoken;
    public List<MenuModel> menuModels=new ArrayList<MenuModel>();
    
    
	public String getJwttoken() {
		return jwttoken;
	}
	public void setJwttoken(String jwttoken) {
		this.jwttoken = jwttoken;
	}
	public List<MenuModel> getMenuModels() {
		return menuModels;
	}
	public void setMenuModels(List<MenuModel> menuModels) {
		this.menuModels = menuModels;
	}
    
    
}
