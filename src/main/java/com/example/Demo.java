package com.example;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.apache.commons.codec.digest.DigestUtils;
import org.mindrot.jbcrypt.BCrypt;

public class Demo {

	public static void main(String[] args) throws Exception {
		
			String result = DigestUtils.sha256Hex("123456");
			
			
		
	}

}
