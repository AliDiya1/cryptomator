package org.cryptomator.common.keychain;

import org.cryptomator.integrations.keychain.KeychainAccessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class KeychainManagerTest {

	private KeychainManager keychainManager;

	@BeforeEach
	public void setUp() {
		// Utilisation d'une instance de MapKeychainAccess pour simuler les opérations du porte-clés
		keychainManager = new KeychainManager(new SimpleObjectProperty<>(new MapKeychainAccess()));
	}

	@Test
	public void testIsPassphraseStored() throws KeychainAccessException {
		// Cas où la passphrase n'est pas stockée
		boolean resultForNonExistentKey = keychainManager.isPassphraseStored("nonExistentKey");
		Assertions.assertFalse(resultForNonExistentKey, "Expected isPassphraseStored() to return false for a non-existent passphrase.");

		// Cas où la passphrase est stockée
		keychainManager.storePassphrase("testKey", "Test", "password");
		boolean resultForStoredKey = keychainManager.isPassphraseStored("testKey");
		Assertions.assertTrue(resultForStoredKey, "Expected isPassphraseStored() to return true for a stored passphrase.");
	}

	@Test
	public void testStoreAndLoad() throws KeychainAccessException {
		KeychainManager keychainManager = new KeychainManager(new SimpleObjectProperty<>(new MapKeychainAccess()));
		keychainManager.storePassphrase("test", "Test", "asd");
		Assertions.assertArrayEquals("asd".toCharArray(), keychainManager.loadPassphrase("test"));
	}

	@Nested
	public static class WhenObservingProperties {

		@BeforeAll
		public static void startup() throws InterruptedException {
			CountDownLatch latch = new CountDownLatch(1);
			Platform.startup(latch::countDown);
			var javafxStarted = latch.await(5, TimeUnit.SECONDS);
			Assumptions.assumeTrue(javafxStarted);
		}

		@Test
		public void testPropertyChangesWhenStoringPassword() throws KeychainAccessException, InterruptedException {
			KeychainManager keychainManager = new KeychainManager(new SimpleObjectProperty<>(new MapKeychainAccess()));
			ReadOnlyBooleanProperty property = keychainManager.getPassphraseStoredProperty("test");
			Assertions.assertFalse(property.get());

			keychainManager.storePassphrase("test", null,"bar");

			AtomicBoolean result = new AtomicBoolean(false);
			CountDownLatch latch = new CountDownLatch(1);
			Platform.runLater(() -> {
				result.set(property.get());
				latch.countDown();
			});
			Assertions.assertTimeoutPreemptively(Duration.ofSeconds(1), () -> latch.await());
			Assertions.assertTrue(result.get());
		}

	}

}