package com.cjy.contenthub.common.util;

import java.text.ParseException;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.stereotype.Component;

import com.cjy.contenthub.common.constants.CommonEnum.JwtValidateResultEnum;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT 유틸리티 클래스
 * JWT 토큰 생성 및 검증 기능을 제공하는 클래스
 * 주로 유저 인증 및 권한 부여에 사용됨
 */
@Slf4j
@Component
public class JwtUtil {

	/** JWT 서명에 사용할 비밀 키 */
	private static final SecretKey SECRET_KEY = Jwts.SIG.HS256.key().build();
	
//	private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(
//		    Decoders.BASE64.decode(System.getenv("JWT_SECRET_KEY"))
//		);

	/**
	 * JWT 토큰 생성 메소드
	 * 
	 * @param id 유저 ID
	 * @param provider 인증 제공자 (예: "naver", "kakao")
	 * @param nickName 유저 닉네임
	 * @param currentDate 현재 날짜
	 * @param expireDate 토큰 만료 날짜
	 * @return 생성된 JWT 토큰 문자열
	 * @throws ParseException 날짜 파싱 예외
	 */
	public String createToken(String id, String provider, String nickName, Date currentDate, Date expireDate) throws ParseException {
		
		// Claims 설정
		Claims claims = Jwts.claims()
				.subject(id) // subject로 유저 식별
				.add("provider", provider) // 인증 제공자 정보 추가
				.add("nickName", nickName) // 유저 닉네임 추가
				.build(); // Claims 객체 생성
		// JWT 생성
		return Jwts.builder()
				.claims(claims) // Claims 설정
				.issuedAt(currentDate) // 현재 날짜 설정
				.expiration(expireDate) // 만료 날짜 설정
				.signWith(SECRET_KEY) // 서명
				.compact(); // JWT 토큰 문자열로 변환
	}

	/**
	 * JWT 토큰 검증 메소드
	 * 
	 * @param token JWT 토큰 문자열
	 * @return 토큰 검증 결과 코드
	 * @throws ParseException 날짜 파싱 예외
	 */
	public String validateToken(String token) throws AccountExpiredException {
		try {
			// JWT 토큰 파싱 및 검증
			Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token);
			// 토큰이 유효한 경우 Claims 객체 반환
			return JwtValidateResultEnum.VALID_TOKEN.getJwtValidateResultCode();
		} catch (ExpiredJwtException ex) {
			// 토큰이 만료된 경우 로그 출력
			log.info(JwtValidateResultEnum.EXPIRED_TOKEN.getJwtValidateResultMsg(), ex);
			// 만료된 토큰의 경우 만료된 토큰 코드를 반환
			return JwtValidateResultEnum.EXPIRED_TOKEN.getJwtValidateResultCode();
	    } catch (JwtException | IllegalArgumentException ex) {
	    	// 토큰이 유효하지 않거나 파싱 중 오류가 발생한 경우 로그 출력
	    	log.info(JwtValidateResultEnum.INVALID_TOKEN.getJwtValidateResultMsg(), ex);
	    	// 유효하지 않은 토큰의 경우 유효하지 않은 토큰 코드를 반환
	    	return JwtValidateResultEnum.INVALID_TOKEN.getJwtValidateResultCode();
	    }
	}
	
	/**
	 * JWT 토큰에서 유저 정보를 추출하는 메소드
	 * 
	 * @param token JWT 토큰
	 * @return NaverUserDetails 객체에 유저 정보가 담김
	 */
	public Claims parseClaims(String token) {
		// JWT 토큰에서 Claims 객체를 추출
		return Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token).getPayload();
	}

}
