package com.kh.menu.model.dao;

import java.util.HashMap;
import java.util.List;

import com.kh.menu.model.dto.MenuDto.*;

public interface MenuDao {

	List<MenuResponse> getMenus(HashMap<String, Object> param);

	int insertMenu(MenuPost menu);

	MenuResponse getMenus(long id);

	int putMenu(MenuPut menu);

	int deleteMenu(long id);

}
