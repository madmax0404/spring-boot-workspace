package com.kh.menu.model.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kh.menu.model.dao.MenuDao;
import com.kh.menu.model.dto.MenuDto.*;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {
	private final MenuDao dao;

	@Override
	public List<MenuResponse> getMenus(HashMap<String, Object> param) {
		return dao.getMenus(param);
	}

	@Override
	public int insertMenu(MenuPost menu) {
		return dao.insertMenu(menu);
	}

	@Override
	public MenuResponse getMenu(long id) {
		return dao.getMenus(id);
	}

	@Override
	public int putMenu(MenuPut menu) {
		return dao.putMenu(menu);
	}

	@Override
	public int deleteMenu(long id) {
		return dao.deleteMenu(id);
	}

}
