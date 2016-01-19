/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana. All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Mario Cervera Ubeda (Integranova) - initial API implementation
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.composites;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class DetailComposite.
 */
public class DetailComposite extends Composite {

	/**
	 * The standard label width when labels for sections line up on the left
	 * hand side of the composite.
	 */
	public static final int STANDARD_LABEL_WIDTH = 85;

	/** Element whose details will be showed by this composite. */
	private EObject element;

	/** The sheet page. */
	private TabbedPropertySheetPage sheetPage;

	/** The EMF edit domain. */
	private EditingDomain EMFEditDomain;

	/**
	 * Instantiates a new detail composite.
	 * 
	 * @param parent
	 *            the parent
	 * @param style
	 *            the style
	 */
	public DetailComposite(Composite parent, int style) {
		super(parent, style);
	}

	/**
	 * Sets the element.
	 * 
	 * @param element
	 *            the new element
	 */
	public void setElement(EObject element) {
		this.element = element;
	}

	/**
	 * Gets the element.
	 * 
	 * @return the element
	 */
	public EObject getElement() {
		return element;
	}

	/**
	 * Sets the sheet page.
	 * 
	 * @param sheetPage
	 *            the new sheet page
	 */
	public void setSheetPage(TabbedPropertySheetPage sheetPage) {
		this.sheetPage = sheetPage;
	}

	/**
	 * Gets the sheet page.
	 * 
	 * @return the sheet page
	 */
	public TabbedPropertySheetPage getSheetPage() {
		return sheetPage;
	}

	/**
	 * Sets the eMF edit domain.
	 * 
	 * @param eMFEditDomain
	 *            the new eMF edit domain
	 */
	public void setEMFEditDomain(EditingDomain eMFEditDomain) {
		EMFEditDomain = eMFEditDomain;
	}

	/**
	 * Gets the eMF edit domain.
	 * 
	 * @return the eMF edit domain
	 */
	public EditingDomain getEMFEditDomain() {
		return EMFEditDomain;
	}

	/**
	 * Creates the widgets.
	 * 
	 * @param composite
	 *            the composite
	 * @param widgetFactory
	 *            the widget factory
	 */
	public void createWidgets(Composite composite,
			TabbedPropertySheetWidgetFactory widgetFactory) {
		// Do nothing
	}

	/**
	 * Sets the section data.
	 * 
	 * @param composite
	 *            the new section data
	 */
	public void setSectionData(Composite composite) {
		// Do nothing
	}

	/**
	 * Hook listeners.
	 */
	public void hookListeners() {
		// Do nothing
	}

	/**
	 * Load data.
	 */
	public void loadData() {
		// Do nothing
	}

	/**
	 * Get the standard label width when labels for sections line up on the left
	 * hand side of the composite. We line up to a fixed position, but if a
	 * string is wider than the fixed position, then we use that widest string.
	 * 
	 * @param parent
	 *            The parent composite used to create a GC.
	 * @param labels
	 *            The list of labels.
	 * 
	 * @return the standard label width.
	 */
	protected int getStandardLabelWidth(Composite parent, String[] labels) {
		int standardLabelWidth = STANDARD_LABEL_WIDTH + 65;
		GC gc = new GC(parent);
		int indent = gc.textExtent("XXX").x; //$NON-NLS-1$
		for (int i = 0; i < labels.length; i++) {
			int width = gc.textExtent(labels[i]).x;
			if (width + indent > standardLabelWidth) {
				standardLabelWidth = width + indent;
			}
		}
		gc.dispose();
		return standardLabelWidth;
	}

	/**
	 * Write error message on status bar.
	 * 
	 * @param message
	 *            the message
	 */
	protected void writeErrorMessageOnStatusBar(String message) {
		IViewSite site = (IViewSite) PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActivePart()
				.getSite();

		IActionBars bars = site.getActionBars();

		if (bars != null) {
			bars.getStatusLineManager().setErrorMessage(message);
		}

	}

}
