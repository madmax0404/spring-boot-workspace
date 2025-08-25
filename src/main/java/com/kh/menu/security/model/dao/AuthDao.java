package com.kh.menu.security.model.dao;

import com.kh.menu.security.model.dto.AuthDto.User;
import com.kh.menu.security.model.dto.AuthDto.UserAuthority;
import com.kh.menu.security.model.dto.AuthDto.UserCredential;

public interface AuthDao {
	
	User findUserByEmail(String email);
	
	void insertUser(User user);
	
	void insertCred(UserCredential cred);
	
	void insertUserRole(UserAuthority auth);
	
	User findUserByUserId(Long userId);

}
