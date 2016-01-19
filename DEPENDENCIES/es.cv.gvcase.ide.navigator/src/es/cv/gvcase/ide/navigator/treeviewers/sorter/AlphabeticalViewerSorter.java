// XXX: disable after migration
// TODO: adapt to MOSKittCommonNavigatorFramework
///*******************************************************************************
// * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
// * de la Comunitat Valenciana . All rights reserved. This program
// * and the accompanying materials are made available under the terms of the
// * Eclipse Public License v1.0 which accompanies this distribution, and is
// * available at http://www.eclipse.org/legal/epl-v10.html
// * 
// * Contributors: Francisco Javier Cano Muñoz (Prodevelop) - initial api implementation
// *
// ******************************************************************************/
//package es.cv.gvcase.ide.navigator.treeviewers.sorter;
//
//import org.eclipse.jface.viewers.Viewer;
//import org.eclipse.jface.viewers.ViewerComparator;
//import org.eclipse.ui.navigator.CommonViewer;
//
//import es.cv.gvcase.ide.navigator.provider.NavigatorLoadedResourcesItem;
//import es.cv.gvcase.ide.navigator.provider.NavigatorRootItem;
//
///**
// * Sorts elements alphabetically
// * 
// * @author fjcano
// * 
// */
//public class AlphabeticalViewerSorter extends ViewerComparator {
//
//	@Override
//	public int compare(Viewer viewer, Object e1, Object e2) {
//		
//		if (e1 instanceof NavigatorRootItem || e2 instanceof NavigatorRootItem) {
//			return 0;
//		}
//		if (e1 instanceof NavigatorLoadedResourcesItem
//				|| e2 instanceof NavigatorLoadedResourcesItem) {
//			return 0;
//		}
//		
//		String t1 = getText(e1);
//		String t2 = getText(e2);
//		if (t1 != null) {
//			return t1.compareTo(t2);
//		}
//		if (t2 != null) {
//			return t2.compareTo(t1);
//		}
//		return 0;
//	}
//
//}
