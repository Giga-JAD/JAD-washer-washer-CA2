package com.Giga_JAD.Washer_Washer;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnvConfig {

	private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

	@Bean
	public Dotenv dotenv() {
		return dotenv;
	}

	public static String get(String key) {
		return dotenv.get(key);
	}
}