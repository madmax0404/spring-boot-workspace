package com.kh.menu.model.dao;

import java.util.HashMap;
import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.kh.menu.model.dto.MenuDto.*;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MenuDaoImpl implements MenuDao {
	private final SqlSessionTemplate session;

	@Override
	public List<MenuResponse> getMenus(HashMap<String, Object> param) {
		return session.selectList("menumapper.getMenus", param);
	}

	@Override
	public int insertMenu(MenuPost menu) {
		return session.insert("menumapper.insertMenu", menu);
	}

	@Override
	public MenuResponse getMenus(long id) {
		return session.selectOne("menumapper.getMenu", id);
	}

	@Override
	public int putMenu(MenuPut menu) {
		return session.update("menumapper.putMenu", menu);
	}

	@Override
	public int deleteMenu(long id) {
		return session.delete("menumapper.deleteMenu", id);
	}
	
}
