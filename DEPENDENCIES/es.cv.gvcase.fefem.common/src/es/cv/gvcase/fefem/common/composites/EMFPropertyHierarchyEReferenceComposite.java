/***************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte,
 * Generalitat de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Jose Manuel García Valladolid (CIT) - Initial API and implementation
 *
 **************************************************************************/

package es.cv.gvcase.fefem.common.composites;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import es.cv.gvcase.fefem.common.FEFEMPage;

/**
 * This composite is similar to EMFPropertyEReferenceComposite but the elements
 * in the selection list are organized as a searchable hierarchy collection. The
 * ContentProvider, Input and LabelProvider of internal tree viewer are
 * delegated to descendants in order to support any kind of hierarchy structure.
 * 
 * @author Jose Manuel García Valladolid
 */
public abstract class EMFPropertyHierarchyEReferenceComposite extends
		EMFPropertyEReferenceComposite {

	/**
	 * @param parent
	 * @param style
	 * @param toolkit
	 * @param object
	 * @param page
	 */
	public EMFPropertyHierarchyEReferenceComposite(Composite parent, int style,
			FormToolkit toolkit, EObject object, FEFEMPage page) {
		super(parent, style, toolkit, object, page);
	}

	/**
	 * @param parent
	 * @param style
	 * @param toolkit
	 * @param viewer
	 * @param page
	 */
	public EMFPropertyHierarchyEReferenceComposite(Composite parent, int style,
			FormToolkit toolkit, Viewer viewer, FEFEMPage page) {
		super(parent, style, toolkit, viewer, page);
	}

	@Override
	protected ITreeContentProvider getChooserContentProvider() {
		return getHierarchyContentProvider();
	}

	@Override
	protected ILabelProvider getLabelProvider() {
		return getHierarchyLabelProvider();
	}

	@Override
	protected boolean checkSelectionValid(Object o) {
		return isSelectable(o);
	}

	protected abstract ITreeContentProvider getHierarchyContentProvider();

	protected abstract ILabelProvider getHierarchyLabelProvider();

	protected abstract boolean isSelectable(Object element);

	@Deprecated
	protected abstract Object getHierarchyInput();

}
