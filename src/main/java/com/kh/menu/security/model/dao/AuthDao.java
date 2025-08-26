package com.kh.menu.security.model.dao;

import com.kh.menu.security.model.dto.AuthDto.User;
import com.kh.menu.security.model.dto.AuthDto.UserAuthority;
import com.kh.menu.security.model.dto.AuthDto.UserCredential;
import com.kh.menu.security.model.dto.AuthDto.UserIdentities;

public interface AuthDao {
	
	User findUserByEmail(String email);
	
	void insertUser(User user);
	
	void insertCred(UserCredential cred);
	
	void insertUserRole(UserAuthority auth);
	
	User findUserByUserId(Long userId);

	void insertUserIdentities(UserIdentities userIdentities);

	void updateUserIdentities(UserIdentities userIdentities);
	
	String getKakaoAccessToken(Long userId);

}
