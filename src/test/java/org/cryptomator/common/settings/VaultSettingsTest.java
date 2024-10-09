/*******************************************************************************
 * Copyright (c) 2016, 2017 Sebastian Stenzel and others.
 * All rights reserved.
 * This program and the accompanying materials are made available under the terms of the accompanying LICENSE file.
 *
 * Contributors:
 *     Sebastian Stenzel - initial API and implementation
 *******************************************************************************/
package org.cryptomator.common.settings;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VaultSettingsTest {

	@Test
	public void testEquals() {
		// Création de deux objets VaultSettings avec le même ID
		VaultSettingsJson json1 = new VaultSettingsJson();
		json1.id = "testId";
		VaultSettings vaultSettings1 = new VaultSettings(json1);

		VaultSettingsJson json2 = new VaultSettingsJson();
		json2.id = "testId";
		VaultSettings vaultSettings2 = new VaultSettings(json2);

		// Test d'égalité
		assertTrue(vaultSettings1.equals(vaultSettings2), "Les deux objets devraient etre egaux");

		// Test d'inégalité pour des IDs différents
		json2.id = "anotherId";
		vaultSettings2 = new VaultSettings(json2);
		assertFalse(vaultSettings1.equals(vaultSettings2), "Les deux objets ne devraient pas etre egaux");

		// Test d'inégalité avec un objet de type différent
		assertFalse(vaultSettings1.equals(new Object()), "L'objet de type different ne devrait pas etre egal");
	}

	@Test
	public void testSerialized() {
		// Créer une instance de VaultSettingsJson avec des valeurs connues
		VaultSettingsJson json = new VaultSettingsJson();
		json.id = "testId";
		json.path = "/path/to/vault";
		json.displayName = "Test Vault";
		json.unlockAfterStartup = true;
		json.revealAfterMount = false;
		json.usesReadOnlyMode = true;
		json.mountFlags = "flag1,flag2";
		json.maxCleartextFilenameLength = 255;
		json.actionAfterUnlock = WhenUnlocked.ASK;
		json.autoLockWhenIdle = true;
		json.autoLockIdleSeconds = 60;
		json.mountPoint = "/mount/point";
		json.mountService = "mountService";
		json.port = 42427;

		// Créer une instance de VaultSettings à partir de VaultSettingsJson
		VaultSettings vaultSettings = new VaultSettings(json);

		// Appeler la méthode serialized
		VaultSettingsJson serializedJson = vaultSettings.serialized();

		// Vérifier que les valeurs sérialisées correspondent à celles de l'instance d'origine
		assertEquals(vaultSettings.id, serializedJson.id, "L'ID ne correspond pas");
		assertEquals(vaultSettings.path.get().toString(), serializedJson.path, "Le chemin ne correspond pas");
		assertEquals(vaultSettings.displayName.get(), serializedJson.displayName, "Le nom d'affichage ne correspond pas");
		assertEquals(vaultSettings.unlockAfterStartup.get(), serializedJson.unlockAfterStartup, "L'option de déverrouillage après le démarrage ne correspond pas");
		assertEquals(vaultSettings.revealAfterMount.get(), serializedJson.revealAfterMount, "L'option de révélation après le montage ne correspond pas");
		assertEquals(vaultSettings.usesReadOnlyMode.get(), serializedJson.usesReadOnlyMode, "L'option de mode lecture seule ne correspond pas");
		assertEquals(vaultSettings.mountFlags.get(), serializedJson.mountFlags, "Les flags de montage ne correspondent pas");
		assertEquals(vaultSettings.maxCleartextFilenameLength.get(), serializedJson.maxCleartextFilenameLength, "La longueur maximale du nom de fichier en clair ne correspond pas");
		assertEquals(vaultSettings.actionAfterUnlock.get(), serializedJson.actionAfterUnlock, "L'action après déverrouillage ne correspond pas");
		assertEquals(vaultSettings.autoLockWhenIdle.get(), serializedJson.autoLockWhenIdle, "L'option de verrouillage automatique en cas d'inactivité ne correspond pas");
		assertEquals(vaultSettings.autoLockIdleSeconds.get(), serializedJson.autoLockIdleSeconds, "Les secondes d'inactivité avant verrouillage automatique ne correspondent pas");
		assertEquals(vaultSettings.mountPoint.get().toString(), serializedJson.mountPoint, "Le point de montage ne correspond pas");
		assertEquals(vaultSettings.mountService.get(), serializedJson.mountService, "Le service de montage ne correspond pas");
		assertEquals(vaultSettings.port.get(), serializedJson.port, "Le port ne correspond pas");
	}

	@ParameterizedTest(name = "VaultSettings.normalizeDisplayName({0}) = {1}")
	@CsvSource(value = {
			"a\u000Fa,a_a",
			": \\,_ _",
			"汉语,汉语",
			"..,_",
			"a\ta,a\u0020a",
			"'\t\n\r',_"
	})
	public void testNormalize(String test, String expected) {
		assertEquals(expected, VaultSettings.normalizeDisplayName(test));
	}
}
