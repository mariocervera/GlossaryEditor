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
//import org.eclipse.emf.ecore.EObject;
//import org.eclipse.emf.ecore.EStructuralFeature;
//import org.eclipse.gmf.runtime.notation.Diagram;
//import org.eclipse.jface.viewers.Viewer;
//import org.eclipse.jface.viewers.ViewerComparator;
//
//import es.cv.gvcase.ide.navigator.provider.NavigatorLoadedResourcesItem;
//import es.cv.gvcase.ide.navigator.provider.NavigatorRootItem;
//
///**
// * Sorts the elements by their containingFeature
// * 
// * @author fjcano
// * 
// */
//public class ContainingFeatureViewerSorter extends ViewerComparator {
//
//	protected EStructuralFeature getContainingFeature(EObject element) {
//		if (element != null) {
//			element.eContainingFeature();
//		}
//		return null;
//	}
//
//	@Override
//	public int category(Object element) {
//		if (element instanceof NavigatorRootItem) {
//			return 0;
//		}
//		if (element instanceof NavigatorLoadedResourcesItem) {
//			return 0;
//		}
//		if (element instanceof Diagram) {
//			return Integer.MAX_VALUE;
//		}
//		if (element instanceof EObject) {
//			EObject eObject = (EObject) element;
//			return eObject.eContainingFeature().getFeatureID();
//		}
//
//		return super.category(element);
//	}
//
//	@Override
//	public int compare(Viewer viewer, Object e1, Object e2) {
//		if (e1 instanceof NavigatorRootItem || e2 instanceof NavigatorRootItem) {
//			return 0;
//		}
//		if (e1 instanceof NavigatorLoadedResourcesItem
//				|| e2 instanceof NavigatorLoadedResourcesItem) {
//			return 0;
//		}
//
//		EStructuralFeature feature1 = null, feature2 = null;
//		if (e1 instanceof EObject) {
//			feature1 = getContainingFeature((EObject) e1);
//		}
//		if (e2 instanceof EObject) {
//			feature2 = getContainingFeature((EObject) e2);
//		}
//		int n = 0;
//		if (feature1 != null) {
//			if (feature2 != null) {
//				n = feature1.getName().compareTo(feature2.getName());
//			}
//		}
//		if (n == 0) {
//			String t1 = null, t2 = null;
//			t1 = getText(e1);
//			t2 = getText(e2);
//			if (t1 != null) {
//				return t1.compareTo(t2);
//			}
//			if (t2 != null) {
//				return t2.compareTo(t1);
//			}
//			return 0;
//		} else {
//			return n;
//		}
//	}
//}
