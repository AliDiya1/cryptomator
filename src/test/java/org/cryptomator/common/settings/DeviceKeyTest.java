package org.cryptomator.common.settings;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.security.SecureRandom;

public class DeviceKeyTest {

	private DeviceKey deviceKey;

	@BeforeEach
	public void setUp() {
		SecureRandom csprng = new SecureRandom();
		deviceKey = new DeviceKey(null, null, csprng);
	}

	@Test
	public void testRandomPasswordNotNull() throws Exception {
		Method randomPasswordMethod = DeviceKey.class.getDeclaredMethod("randomPassword");
		randomPasswordMethod.setAccessible(true);
		char[] password = (char[]) randomPasswordMethod.invoke(deviceKey);

		Assertions.assertNotNull(password, "The generated password should not be null.");
		Assertions.assertTrue(password.length > 0, "The generated password should not be empty.");
	}

	@Test
	public void testRandomPasswordLength() throws Exception {
		Method randomPasswordMethod = DeviceKey.class.getDeclaredMethod("randomPassword");
		randomPasswordMethod.setAccessible(true);
		char[] password = (char[]) randomPasswordMethod.invoke(deviceKey);
		String passwordString = new String(password);

		// UUIDs have 36 characters (32 alphanumeric + 4 hyphens)
		Assertions.assertEquals(36, passwordString.length(), "The generated password should be 36 characters long.");
	}

	@Test
	public void testRandomPasswordIsRandom() throws Exception {
		Method randomPasswordMethod = DeviceKey.class.getDeclaredMethod("randomPassword");
		randomPasswordMethod.setAccessible(true);

		String password1 = new String((char[]) randomPasswordMethod.invoke(deviceKey));
		String password2 = new String((char[]) randomPasswordMethod.invoke(deviceKey));

		Assertions.assertNotEquals(password1, password2, "Subsequent calls to randomPassword() should generate different passwords.");
	}
}
