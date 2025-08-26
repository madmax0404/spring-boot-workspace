package com.kh.menu.security.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.menu.security.model.dao.AuthDao;
import com.kh.menu.security.model.dto.AuthDto.AuthResult;
import com.kh.menu.security.model.dto.AuthDto.User;
import com.kh.menu.security.model.dto.AuthDto.UserAuthority;
import com.kh.menu.security.model.dto.AuthDto.UserCredential;
import com.kh.menu.security.model.provider.JWTProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
	private final AuthDao dao;
	private final PasswordEncoder encoder; // bean 객체 생성 필요.
	private final JWTProvider jwt;
	private final KakaoService service;

	@Override
	public boolean findUserByEmail(String email) {
		User user = dao.findUserByEmail(email);
		return user != null;
	}

	@Override
	public AuthResult login(String email, String password) {
		// 1. 사용자 정보 조회
		User user = dao.findUserByEmail(email);
		
		if (!encoder.matches(password, user.getPassword())) {
			throw new BadCredentialsException("비밀번호 오류");
		}
		
		// 2. 토큰 발급
		String accessToken = jwt.createAccessToken(user.getId(), 30);
		String refreshToken = jwt.createRefreshToken(user.getId(), 7);
		
		User userNoPassword = User.builder()
				.id(user.getId())
				.email(user.getEmail())
				.name(user.getName())
				.profile(user.getProfile())
				.roles(user.getRoles())
				.build();
		
		return AuthResult.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.user(userNoPassword)
				.build();
	}

	@Override
	@Transactional
	public AuthResult signUp(String email, String password) {
		// 1) Users 테이블에 데이터 추가
		User user = User.builder()
				.email(email)
				.name(email.split("@")[0])
				.build();
		dao.insertUser(user);
		
		// 2) Credential 추가
		UserCredential cred = UserCredential.builder()
				.userId(user.getId())
				.password(encoder.encode(password))
				.build();
		dao.insertCred(cred);
		
		// 3) 권한 추가
		UserAuthority auth = UserAuthority.builder()
				.userId(user.getId())
				.roles(List.of("ROLE_USER"))
				.build();
		dao.insertUserRole(auth);
		
		// 4) 토큰 발급
		String accessToken = jwt.createAccessToken(user.getId(), 30);
		String refreshToken = jwt.createRefreshToken(user.getId(), 7);
		
		user = dao.findUserByUserId(user.getId());
		
		return AuthResult.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.user(user)
				.build();
	}

	@Override
	public AuthResult refreshByCookie(String refreshCookie) {
		Long userId = jwt.parseRefresh(refreshCookie);
		User user = dao.findUserByUserId(userId);
		
		String accessToken = jwt.createAccessToken(userId, 30);
		
		return AuthResult.builder()
				.accessToken(accessToken)
				.user(user)
				.build();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public User findUserByUserId(Long userId) {
		String accessToken = dao.getKakaoAccessToken(userId);
		Map<String, Object> userInfo = service.getUserInfo(accessToken);
		
		Map<String,Object> kakao_account = (Map<String,Object>)userInfo.get("kakao_account");
		Map<String,Object> userInfoProfile = (Map<String,Object>)kakao_account.get("profile");
		String nickname = (String)(userInfoProfile.get("nickname"));
		String profile = (String)(userInfoProfile.get("profile_image_url"));
		String email = (String)userInfo.get("email");
		
		User user = User.builder()
				.name(nickname)
				.email(email)
				.profile(profile)
				.roles(List.of("ROLE_USER"))
				.build();
		
		return user;
	}

	@Override
	public String getKakaoAccessToken(Long userId) {
		return dao.getKakaoAccessToken(userId);
	}

}
