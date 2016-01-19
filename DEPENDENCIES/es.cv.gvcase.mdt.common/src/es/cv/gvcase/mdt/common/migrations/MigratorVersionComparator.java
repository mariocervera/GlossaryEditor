/*******************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Marc Gil Sendar (Prodevelop) – Initial implementation
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.migrations;

import java.util.Comparator;

import es.cv.gvcase.mdt.common.util.MDTUtil;

public class MigratorVersionComparator implements Comparator<MigratorService> {

	/**
	 * Compare two diagram migratorService versions: Returns -1 if m2 is bigger
	 * than m1; 0 if m1 is equals than m2; 1 if m2 is smaller than m1
	 */
	public int compare(MigratorService m1, MigratorService m2) {
		String targetVersionM1 = m1.getTargetVersion();
		String targetVersionM2 = m2.getTargetVersion();

		return -(MDTUtil.compareDiagramVersions(targetVersionM1,
				targetVersionM2));
	}
}
