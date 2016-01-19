/***************************************************************************
 * Copyright (c) 2008, 2010 Conselleria de Infraestructuras y Transporte,
 * Generalitat de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – initial API and implementation
 * 				 Francisco Javier Cano Muñoz (Prodevelop) - prepared the action to be available from the toolbar.
 *
 ******************************************************************************/

package es.cv.gvcase.mdt.common.actions;

import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gmf.runtime.diagram.ui.actions.DiagramAction;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import es.cv.gvcase.mdt.common.Activator;
import es.cv.gvcase.mdt.common.commands.OpenUpperDiagramCommand;
import es.cv.gvcase.mdt.common.util.MDTUtil;
import es.cv.gvcase.mdt.common.util.MultiDiagramUtil;

/**
 * "Open upper" context menu action. Will be available in any diagram that has a
 * possible parent {@link Diagram}. <br>
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 */
public class OpenUpperDiagramAction extends DiagramAction {

	/** The Constant ACTION_TEXT. */
	private static final String ACTION_TEXT = "Open upper";

	/**
	 * Instantiates a new open upper diagram action.
	 * 
	 * @param page
	 *            the page
	 * @param view
	 *            the view
	 */
	public OpenUpperDiagramAction(IWorkbenchPage page) {
		super(page);
		setId(OpenUpperDiagramContributionItemProvider.ACTION_OPEN_UPPER_DIAGRAM);
		setText("Open upper");
		setToolTipText("Open upper diagram");

		ImageDescriptor enabledImage = getImageDescriptor("icons/openUpperEnabled.gif");
		ImageDescriptor disabledImage = getImageDescriptor("icons/openUpperDisabled.png");

		setImageDescriptor(enabledImage);
		setDisabledImageDescriptor(disabledImage);
		setHoverImageDescriptor(enabledImage);
	}

	protected ImageDescriptor getImageDescriptor(String imagePath) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
				imagePath);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gmf.runtime.diagram.ui.actions.DiagramAction#createTargetRequest
	 * ()
	 */
	@Override
	protected Request createTargetRequest() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gmf.runtime.diagram.ui.actions.DiagramAction#isSelectionListener
	 * ()
	 */
	@Override
	protected boolean isSelectionListener() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gmf.runtime.common.ui.action.AbstractActionHandler#init()
	 */
	@Override
	public void init() {
		super.init();
		setText(ACTION_TEXT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gmf.runtime.diagram.ui.actions.DiagramAction#refresh()
	 */
	@Override
	public void refresh() {
		super.refresh();
		setText(ACTION_TEXT);
		updateSelection();
	}

	/**
	 * We need to set the newly selected {@link Diagram} as the diagram on which
	 * to perform this action.
	 */
	private void updateSelection() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return getUpperDiagrams() != null && !getUpperDiagrams().isEmpty();
	}

	@Override
	protected boolean calculateEnabled() {
		return isEnabled();
	}

	protected List<Diagram> getUpperDiagrams() {
		Diagram diagram = MDTUtil
				.getDiagramFomEditor(MDTUtil.getActiveEditor());
		if (diagram != null) {
			return MultiDiagramUtil.getUpperDiagrams(diagram);
		}
		return Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gmf.runtime.diagram.ui.actions.DiagramAction#getCommand()
	 */
	@Override
	protected Command getCommand() {
		if (!isEnabled()) {
			return null;
		}
		return new OpenUpperDiagramCommand(getUpperDiagrams()).toGEFCommand();
	}

}
