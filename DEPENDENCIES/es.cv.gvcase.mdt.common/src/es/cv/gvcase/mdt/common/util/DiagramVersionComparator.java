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
package es.cv.gvcase.mdt.common.util;

import java.util.Comparator;

public class DiagramVersionComparator implements Comparator<String> {

	/**
	 * Compare two diagram versions: Returns -1 if ver1 is bigger than ver2; 0
	 * if ver1 is equals than ver2; 1 if ver1 is smaller than ver2
	 */
	public int compare(String version1, String version2) {
		int major1 = MDTUtil.getMajorNumberVersion(version1);
		int major2 = MDTUtil.getMajorNumberVersion(version2);
		int minor1 = MDTUtil.getMinorNumberVersion(version1);
		int minor2 = MDTUtil.getMinorNumberVersion(version2);
		int revision1 = MDTUtil.getRevisionNumberVersion(version1);
		int revision2 = MDTUtil.getRevisionNumberVersion(version2);

		if (major1 < 0 || major2 < 0 || minor1 < 0 || minor2 < 0
				|| revision1 < 0 || revision2 < 0) {
			return 0;
		}

		// major number of version 1 is bigger than version 2, version 1 is
		// bigger
		if (major1 > major2) {
			return -1;
		}

		// major number of version 1 is smaller than version 2, version 2 is
		// bigger
		if (major1 < major2) {
			return 1;
		}

		// if major number are equals, then compare minor version
		// minor number of version 1 is bigger than version 2, version 1 is
		// bigger
		if (minor1 > minor2) {
			return -1;
		}

		// minor number of version 1 is smaller than version 2, version 2 is
		// bigger
		if (minor1 < minor2) {
			return 1;
		}

		// if minor number are equals, then compare revision version
		// revision number of version 1 is bigger than version 2, version 1 is
		// bigger
		if (revision1 > revision2) {
			return -1;
		}

		// revision number of version 1 is smaller than version 2, version 2 is
		// bigger
		if (revision1 < revision2) {
			return 1;
		}

		// if revision number are equals, then the versions are equals too
		return 0;
	}
}
