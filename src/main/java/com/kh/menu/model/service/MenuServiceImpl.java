package com.kh.menu.model.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kh.menu.model.dao.MenuDao;
import com.kh.menu.model.vo.Menu;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {
	private final MenuDao dao;

	@Override
	public List<Menu> getMenus() {
		return dao.getMenus();
	}

}
