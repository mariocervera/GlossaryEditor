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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.emf.databinding.EMFObservables;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;

import es.cv.gvcase.fefem.common.FEFEMPage;

/**
 * Creates a readonly combo list composite very usefull for creating the master
 * part of a master/detail UI patterns. The candidate elements are automatically
 * obtained by the given structural feature. The descendant classes only needs
 * to provide a LabelProvider for these elements.
 * 
 * @author Jose Manuel García Valladolid
 */
public abstract class EMFPropertyComboListViewComposite extends
		EMFPropertyComposite {

	protected ComboViewer viewer;
	protected Combo combo;

	/**
	 * @param parent
	 * @param style
	 * @param toolkit
	 * @param object
	 * @param page
	 */
	public EMFPropertyComboListViewComposite(Composite parent, int style,
			FormToolkit toolkit, EObject object, FEFEMPage page) {
		super(parent, style, toolkit, object, page);

		page.getEditor().getSelectionProvider()
				.registerDelegatedSelectionProvider(this.getViewer());
	}

	/**
	 * @param parent
	 * @param style
	 * @param toolkit
	 * @param viewer
	 * @param page
	 */
	public EMFPropertyComboListViewComposite(Composite parent, int style,
			FormToolkit toolkit, Viewer viewer, FEFEMPage page) {
		super(parent, style, toolkit, viewer, page);

		page.getEditor().getSelectionProvider()
				.registerDelegatedSelectionProvider(this.getViewer());

	}

	@Override
	protected void createWidgets(FormToolkit toolkit) {
		this.setLayout(getGridLayout());

		createLabel(toolkit);

		combo = new Combo(this, SWT.READ_ONLY);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		viewer = new ComboViewer(combo);

		toolkit.adapt(combo);
		toolkit.adapt(this);
	}

	protected GridLayout getGridLayout() {
		return new GridLayout(2, false);
	}

	@Override
	protected void bindFeatureToWidget() {
		if (getPage().getEditor().getModel() == null
				|| getPage().getEditor().getEditingDomain() == null
				|| getEObject() == null) {
			return;
		}

		viewer.setContentProvider(getContentProvider());
		if (getLabelProvider() != null) {
			viewer.setLabelProvider(getLabelProvider());
		}
		viewer.setInput(getCandidatesObservable());

		selectItem(getDefaultSelectionIndex());
	}

	protected IObservableList candidatesObservable;

	protected IObservableList getCandidatesObservable() {
		if (candidatesObservable == null) {
			if (!isDetailComposite()) {
				candidatesObservable = EMFObservables.observeList(this
						.getEObject(), getFeature());

			} else {
				IObservableValue selectionObservable = ViewersObservables
						.observeSingleSelection(this.getMasterViewer());

				candidatesObservable = EMFObservables.observeDetailList(Realm
						.getDefault(), selectionObservable, getFeature());

			}
		}
		return candidatesObservable;
	}

	protected ObservableListContentProvider getContentProvider() {
		return new ObservableListContentProvider() {
			@Override
			public Object[] getElements(Object inputElement) {
				Object[] array = super.getElements(inputElement);
				if (sortElements()) {
					Arrays.sort(array, getComparator());
				}
				return array;
			}
		};
	}

	protected int getDefaultSelectionIndex() {
		return -1;
	}

	protected boolean sortElements() {
		return false;
	}

	protected Comparator<Object> getComparator() {
		return new Comparator<Object>() {
			public int compare(Object o1, Object o2) {
				return o1.toString().toLowerCase().compareTo(
						o2.toString().toLowerCase());
			}
		};
	}

	@Override
	protected IObservableValue getTargetObservable() {
		return ViewersObservables.observeSingleSelection(viewer);
	}

	public ComboViewer getViewer() {
		return viewer;
	}

	protected abstract ILabelProvider getLabelProvider();

	@Override
	public Control getRepresentativeControl() {
		return getViewer().getControl();
	}

	protected List<Control> getWidgetsList() {
		List<Control> list = new ArrayList<Control>();
		list.add(combo);
		return list;
	}

	protected int indexOf(Object obj) {
		int index = -1;

		for (int i = 0; i < getViewer().getCombo().getItemCount(); i++) {
			Object o = getViewer().getElementAt(i);
			if (obj.equals(o)) {
				index = i;
				break;
			}
		}

		return index;
	}

	protected void selectItem(int index) {
		// deselect current item
		combo.deselectAll();
		if (index != -1 && combo.getItemCount() > 0) {
			combo.select(index);
		}
	}
}
