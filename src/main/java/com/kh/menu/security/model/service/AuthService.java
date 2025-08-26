package com.kh.menu.security.model.service;

import com.kh.menu.security.model.dto.AuthDto.AuthResult;
import com.kh.menu.security.model.dto.AuthDto.User;

public interface AuthService {

	AuthResult login(String email, String password);

	boolean findUserByEmail(String email);

	AuthResult signUp(String email, String password);

	AuthResult refreshByCookie(String refreshCookie);

	User findUserByUserId(Long userId);

	String getKakaoAccessToken(Long userId);

}
