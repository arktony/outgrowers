package com.farm_erp.utilities;

import java.util.Random;

import org.springframework.stereotype.Service;

@Service
public class RandomGenerator {

	public static String randomString(int length) {

		int leftLimit = 97; // letter 'a'
		int rightLimit = 122; // letter 'z'
		StringBuilder buffer = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			int randomLimitedInt = leftLimit + (int) (new Random().nextFloat() * (rightLimit - leftLimit + 1));
			buffer.append((char) randomLimitedInt);
		}
		String generatedString = buffer.toString();

		return generatedString;
	}

	public static int randomNumber(int length) {

		Random random = new Random();

		int x = random.nextInt(999) + 100;

		return x;
	}

	public String randomPrefixNumberGenerator(String prefix, int pref, int latestAccount) {
		String randomNumber = prefix + pref + String.format("%06d", (latestAccount + 1));

		return randomNumber;
	}

	public String randomPrefixNumberGenerator(int prefix, int latestAccount) {
		String randomNumber = prefix + String.format("%06d", (latestAccount + 1));

		return randomNumber;
	}

	public String randomPrefixNumberGenerator(String prefix, int latestAccount) {
		String randomNumber = prefix + String.format("%06d", (latestAccount + 1));

		return randomNumber;
	}

}
