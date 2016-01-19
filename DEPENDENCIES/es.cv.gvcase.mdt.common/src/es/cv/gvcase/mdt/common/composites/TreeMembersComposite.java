/*******************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana. All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Marc Gil Sendra (Prodevelop) - initial API implementation
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.composites;

import java.util.Collection;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

import es.cv.gvcase.emf.ui.common.providers.GroupableTreeArrayContentProvider;
import es.cv.gvcase.emf.ui.common.providers.PackagedLabelProvider;
import es.cv.gvcase.emf.ui.common.providers.TreeArrayContentProvider;
import es.cv.gvcase.emf.ui.common.utils.PackagingNode;
import es.cv.gvcase.mdt.common.util.MDTUtil;

/**
 * The Class TreeMembersComposite. Provides a MembersComposite showing the
 * elements with a Tree mode
 */
public class TreeMembersComposite extends MembersComposite {

	/**
	 * Instantiates a new members composite.
	 * 
	 * @param parent
	 *            the parent
	 * @param style
	 *            the style
	 * @param string
	 * @param baseLabelProvider
	 * @param structuralFeature
	 */
	public TreeMembersComposite(Composite parent, int style,
			TabbedPropertySheetWidgetFactory widgetFactory,
			EStructuralFeature structuralFeature, String labelText) {
		super(parent, style, widgetFactory, structuralFeature, labelText);
	}

	@Override
	protected StructuredViewer getNewViewer(Composite parent) {
		return new TreeViewer(parent);
	}

	@Override
	public IContentProvider getContentProvider() {
		return new GroupableTreeArrayContentProvider(
				new TreeArrayContentProvider());
	}

	@Override
	public IBaseLabelProvider getLabelProvider() {
		return new PackagedLabelProvider(MDTUtil.getLabelProvider());
	}

	@Override
	protected ViewerFilter[] getFilters() {
		return new ViewerFilter[] { new ViewerFilter() {
			@Override
			public boolean select(Viewer viewer, Object parentElement,
					Object element) {
				Collection<?> input = ((Collection<?>) getMemberElementsViewer()
						.getInput());

				if (input == null) {
					return true;
				}

				if (!(element instanceof PackagingNode)) {
					return input != null ? !input.contains(element) : true;
				} else {
					boolean contains = true;
					for (Object o : ((PackagingNode) element)
							.getContainedNodes()) {
						contains &= input.contains(o);
					}
					return !contains;
				}
			}
		} };
	}
}
