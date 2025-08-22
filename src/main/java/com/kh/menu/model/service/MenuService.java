package com.kh.menu.model.service;

import java.util.HashMap;
import java.util.List;

import com.kh.menu.model.dto.MenuDto.*;

public interface MenuService {

	List<MenuResponse> getMenus(HashMap<String, Object> param);

	int insertMenu(MenuPost menu);

	MenuResponse getMenu(long id);

	int putMenu(MenuPut menu);

	int deleteMenu(long id);
	
}
