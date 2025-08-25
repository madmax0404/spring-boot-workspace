package com.kh.menu.security.model.dao;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.kh.menu.security.model.dto.AuthDto.User;
import com.kh.menu.security.model.dto.AuthDto.UserAuthority;
import com.kh.menu.security.model.dto.AuthDto.UserCredential;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AuthDaoImpl implements AuthDao {
	private final SqlSessionTemplate session;
	
	@Override
	public User findUserByEmail(String email) {
        return session.selectOne("auth.findUserByEmail", email);
    }
	
	@Override
    public void insertUser(User user) {
        session.insert("auth.insertUser", user);
    }
	
	@Override
    public void insertCred(UserCredential cred) {
        session.insert("auth.insertCred", cred);
    }
	
	@Override
    public void insertUserRole(UserAuthority auth) {
        session.insert("auth.insertUserRole", auth);
    }
	
	@Override
    public User findUserByUserId(Long userId) {
        return session.selectOne("auth.findUserByUserId", userId);
    }
}
