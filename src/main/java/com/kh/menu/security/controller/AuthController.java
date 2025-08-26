package com.kh.menu.security.controller;

import java.time.Duration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.menu.security.model.dto.AuthDto.AuthResult;
import com.kh.menu.security.model.dto.AuthDto.LoginRequest;
import com.kh.menu.security.model.dto.AuthDto.User;
import com.kh.menu.security.model.provider.JWTProvider;
import com.kh.menu.security.model.service.AuthService;
import com.kh.menu.security.model.service.KakaoService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
	private final JWTProvider jwt;
	private final AuthService service;
	private final KakaoService kakaoService;
	public static final String REFRESH_COOKIE = "REFRESH_TOKEN";
	/**
	 * 로그인
	 * - 현재 DB에 존재하지 않는 이메일이면 404 반환.
	 * - 프론트에서 응답상태 404인 경우, 회원가입 처리.
	 * - 이메일은 존재하나 비밀번호가 틀린경우 401 상태(미인증상태) 반환.
	 * - 모두 성공시 유저 정보와 JWT 토큰 반환.
	 */
	@PostMapping("/login")
	public ResponseEntity<?> login(
			@RequestBody LoginRequest req
			) {
		// 1) 사용자가 존재하는지 확인
		boolean exists = service.findUserByEmail(req.getEmail());
		
		if(!exists) {
			return ResponseEntity.notFound().build();
		}
		
		try {
			AuthResult result = service.login(req.getEmail(), req.getPassword());
			System.out.println(result.getRefreshToken());
			
			// refreshToken은 http-only 쿠키로 설정하여 반환.
			ResponseCookie refreshCookie =
					ResponseCookie
					.from(REFRESH_COOKIE, result.getRefreshToken())
					.httpOnly(true)
					.secure(false) // true: https 에서만 사용. false: http/https 에서 사용.
					.path("/")
					.sameSite("Lax")
					.maxAge(Duration.ofDays(7)) // 만료시간
					.build();
			
			System.out.println(refreshCookie);
			
			return ResponseEntity.ok()
					.header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
					.body(result);
		} catch (BadCredentialsException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}		
	}
	
	/**
	 * 회원가입
	 */
	@PostMapping("/signup")
	public ResponseEntity<?> signup(
			@RequestBody LoginRequest req
			) {
		AuthResult result = service.signUp(req.getEmail(), req.getPassword());
		
		// refreshToken은 http-only 쿠키로 설정하여 반환.
		ResponseCookie refreshCookie =
				ResponseCookie
				.from(REFRESH_COOKIE, result.getRefreshToken())
				.httpOnly(true)
				.secure(false) // true: https 에서만 사용. false: http/https 에서 사용.
				.path("/")
				.sameSite("Lax")
				.maxAge(Duration.ofDays(7)) // 만료시간
				.build();
		
		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
				.body(result);
	}
	
	// accessToken 재발급 url
	@PostMapping("/refresh")
	public ResponseEntity<AuthResult> refresh(
			@CookieValue(name=REFRESH_COOKIE, required=false) String refreshCookie
			) {
		if (refreshCookie == null || refreshCookie.isBlank()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		
		// 쿠키가 있으면 쿠키를 검증하여 새로운 accessToken 생성.
		AuthResult result = service.refreshByCookie(refreshCookie);
		
		return ResponseEntity.ok(result);
	}
	
	// logout
	@PostMapping("/logout")
	public ResponseEntity<Void> logout(HttpServletRequest req) {
		// 1. 클라이언트의 헤더에서 id값 추출
		String accessToken = resolveAccessToken(req);
		Long userId = jwt.getUserId(accessToken);
		
		// 2. DB에서 사용자의 카카오 액세스 토큰 조회
		String kakaoAccessToken = service.getKakaoAccessToken(userId);
		
		if (kakaoAccessToken != null) {
			// 카카오에 로그아웃 요청(액세스 토큰 만료)
			kakaoService.logout(kakaoAccessToken);
		}
		
		// 리프레쉬 토큰 제거
		ResponseCookie refreshCookie =
				ResponseCookie
				.from(REFRESH_COOKIE, "")
				.httpOnly(true)
				.secure(false) // true: https 에서만 사용. false: http/https 에서 사용.
				.path("/")
				.sameSite("Lax")
				.maxAge(0) // 만료시간
				.build();
		
		return ResponseEntity.noContent().header(HttpHeaders.SET_COOKIE, refreshCookie.toString()).build();
	}
	
	@GetMapping("/me")
	public ResponseEntity<User> getUserInfo(HttpServletRequest req) {
		// 1. 요청 헤더에서 jwt 토큰 추출
		String jwtToken = resolveAccessToken(req);
		
		if (jwtToken == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		
		// 2. JWT 토큰에서 ID 값 추출
		Long userId = jwt.getUserId(jwtToken);
		
		// 사용자정보 조회
		User user = service.findUserByUserId(userId);
		
		if (user == null) {
			return ResponseEntity.notFound().build();
		}
		
		return ResponseEntity.ok(user);
	}
	
	public String resolveAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}









