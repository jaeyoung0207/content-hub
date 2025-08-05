package com.cjy.contenthub.common;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.junit.jupiter.api.Test;

import jakarta.xml.bind.DatatypeConverter;

public class ConvertApiKeyTest {
	
//	@Test
//	void test() throws NoSuchAlgorithmException {
//		
//		String tmdbApiToken = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI4ZTkyZGNkZGE2YmVhOGU0OTVjZGI5YjM1YWIxZTY2NCIsIm5iZiI6MTc0NTAzMTQ5Ni4wNTUsInN1YiI6IjY4MDMxMTQ4NjFiMWM0YmIzMjlhNTA0OCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.IJFQE9TgzRyibt963p8WkXtb53c7HuJ_PX2oVZNTN18";
//		
//		MessageDigest md = MessageDigest.getInstance("SHA-256");
//		
//        byte[] digest1 = md.digest(tmdbApiToken.getBytes(StandardCharsets.UTF_8));
//        String tmdbApiHashCode = DatatypeConverter.printHexBinary(digest1).toLowerCase();
//        System.out.println(tmdbApiHashCode);
//        
//		String deeplApiToken = "b79e6d1a-09a9-40ad-a29b-018cab21e62b:fx";
//        byte[] digest2 = md.digest(deeplApiToken.getBytes(StandardCharsets.UTF_8));
//        String deeplApiHashCode = DatatypeConverter.printHexBinary(digest2).toLowerCase();
//        System.out.println(deeplApiHashCode);
//		
//	}

}
