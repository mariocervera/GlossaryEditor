/*******************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Marc Gil Sendra (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.migrations;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;

import es.cv.gvcase.mdt.common.util.ExtensionPointParser;

public class DiagramMigrationRegistry {

	/**
	 * The extension point identifier
	 */
	private final String extensionPointID = "es.cv.gvcase.mdt.common.diagramMigration";

	private List<MigratorService> migratorsList;

	/**
	 * Gets the instance for this registry
	 * 
	 * @return
	 */
	public static DiagramMigrationRegistry getInstance() {
		return new DiagramMigrationRegistry();
	}

	/**
	 * Executes the migration
	 * 
	 * @param input
	 */
	public List<MigratorService> parseMigrators() {
		migratorsList = new ArrayList<MigratorService>();
		parseMigratorsFromExtensionPoint();
		return migratorsList;
	}

	private void parseMigratorsFromExtensionPoint() {
		ExtensionPointParser extensionPointParser = new ExtensionPointParser(
				extensionPointID, new Class[] { Migrator.class, ModelID.class });

		List<Migrator> migrators = new ArrayList<Migrator>();
		for (Object o : extensionPointParser.parseExtensionPoint()) {
			if (o instanceof Migrator) {
				Migrator migrator = (Migrator) Platform.getAdapterManager()
						.getAdapter(o, Migrator.class);
				migrators.add(migrator);
			} else if (o instanceof ModelID) {
			}
		}

		for (Migrator migrator : migrators) {
			if (migrator.MigratorClass == null) {
				continue;
			}

			MigratorService migratorService = (MigratorService) Platform
					.getAdapterManager().getAdapter(migrator.MigratorClass,
							MigratorService.class);
			if (migratorService == null) {
				continue;
			}

			try {
				for (ModelID id : migrator.ModelID) {
					migratorService.addModelId(id.ModelID);
				}
				migratorService.setSourceVersion(migrator.SourceVersion);
				migratorService.setTargetVersion(migrator.TargetVersion);
				migratorService.setLabel(migrator.Label);

				migratorsList.add(migratorService);
			} catch (Exception e) {
				continue;
			}
		}
	}
}
