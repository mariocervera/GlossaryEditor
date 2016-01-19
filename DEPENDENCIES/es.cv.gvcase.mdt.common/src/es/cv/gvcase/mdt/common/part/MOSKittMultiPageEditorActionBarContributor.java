/*******************************************************************************
 * Copyright (c) 2009, 2010 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.part;

import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IActionBars2;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;

import es.cv.gvcase.mdt.common.util.MDTUtil;

/**
 * A special implementation of a {@link MultiPageEditorActionBarContributor} to
 * switch between action bar contributions for {@link MOSKittMultiPageEditor}
 * with several different editors nested.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 */
public class MOSKittMultiPageEditorActionBarContributor extends
		MultiPageEditorActionBarContributor {

	private MOSKittMultiPageEditor multiPageEditor = null;

	private static MOSKittMultiPageEditorActionBarContributor INSTANCE = null;

	public static MOSKittMultiPageEditorActionBarContributor getDefault() {
		return INSTANCE;
	}

	public MOSKittMultiPageEditorActionBarContributor() {
		if (INSTANCE == null) {
			INSTANCE = this;
		}
		return;
	}

	/**
	 * The parent action bars with which this contributor was initialized.
	 */
	private IActionBars2 myActionBars2;

	/**
	 * Caching of the active editor action bars.
	 */
	private SubActionBarsExt myActiveEditorActionBars;

	/**
	 * Caching of the last active editor.
	 */
	private IEditorPart myActiveEditor;

	/**
	 * Property listener to react upon the editor's properties changing.
	 */
	private IPropertyListener myEditorPropertyChangeListener = null;

	/**
	 * Getter for the Property listener.
	 * 
	 * @return
	 */
	protected IPropertyListener getMyEditorPtopertyChangeListener() {
		if (myEditorPropertyChangeListener == null) {
			myEditorPropertyChangeListener = new IPropertyListener() {

				public void propertyChanged(Object source, int propId) {
					if (myActiveEditorActionBars != null) {
						if (myActiveEditorActionBars.getContributor() instanceof MOSKittActionBarContributor) {
							((MOSKittActionBarContributor) myActiveEditorActionBars
									.getContributor()).update();
						}
					}
				}
			};
		}
		return myEditorPropertyChangeListener;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.ui.part.EditorActionBarContributor#init(org.eclipse.ui.
	 * IActionBars)
	 */
	@Override
	public void init(IActionBars bars) {
		super.init(bars);
		assert bars instanceof IActionBars2;
		myActionBars2 = (IActionBars2) bars;
	}

	public void setMultiPageEditor(MOSKittMultiPageEditor multiPageEditor) {
		this.multiPageEditor = multiPageEditor;
	}

	/**
	 * Get rid of the installed IPartListener
	 */
	@Override
	public void dispose() {
		doDispose();
		return;
	}

	protected void doDispose() {
		// do the real dispose only if the contributor is no longer in use by
		// any other editor
		if (!checkContributorUsed(this)) {
			super.dispose();
			if (myActiveEditorActionBars != null) {
				myActiveEditorActionBars.deactivate();
				myActiveEditorActionBars.dispose();
				myActiveEditorActionBars = null;
			}
			sharedEditorsContributor = null;
			myActiveEditor = null;
			INSTANCE = null;
		}
	}

	protected boolean isAffectingPart(IWorkbenchPart part) {
		if (part.getSite() instanceof IEditorSite) {
			IEditorSite editorSite = (IEditorSite) part.getSite();
			if (editorSite.getActionBarContributor() != null
					&& editorSite.getActionBarContributor().equals(
							MOSKittMultiPageEditorActionBarContributor.this)) {
				return true;
			}
		}
		return false;
	}

	protected boolean isLastPart(IWorkbenchPart part) {
		if (part.getSite() instanceof IEditorSite) {
			IEditorSite editorSite = (IEditorSite) part.getSite();
			if (editorSite.getActionBarContributor() instanceof MOSKittMultiPageEditorActionBarContributor) {
				MOSKittMultiPageEditorActionBarContributor contributor = (MOSKittMultiPageEditorActionBarContributor) editorSite
						.getActionBarContributor();
				return !checkContributorUsed(contributor);
			}
		}
		return false;
	}

	protected boolean checkContributorUsed(
			MOSKittMultiPageEditorActionBarContributor contributor) {
		if (contributor == null) {
			return false;
		}
		try {
			IWorkbenchWindow ww = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow();
			if (ww == null || ww.getActivePage() == null) {
				return false;
			}
			IEditorReference[] editorReferences = ww.getActivePage()
					.getEditorReferences();
			for (IEditorReference reference : editorReferences) {
				IEditorPart editorPart = (IEditorPart) reference.getPart(false);
				IEditorActionBarContributor actionBarContributor = (IEditorActionBarContributor) ((IEditorSite) editorPart
						.getEditorSite()).getActionBarContributor();
				if (editorPart != null
						&& MOSKittMultiPageEditorActionBarContributor.this
								.equals(actionBarContributor)) {
					return true;
				}
			}
		} catch (NullPointerException ex) {
			return false;
		}
		return false;
	}

	protected boolean isMyEditorActive() {
		IEditorPart activeEditorPart = MDTUtil.getActiveEditor();
		if (multiPageEditor != null && multiPageEditor.equals(activeEditorPart)) {
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.part.MultiPageEditorActionBarContributor#setActiveEditor
	 * (org.eclipse.ui.IEditorPart)
	 */
	@Override
	public void setActiveEditor(IEditorPart part) {
		if (part instanceof MOSKittMultiPageEditor) {
			part = ((MOSKittMultiPageEditor) part).getActiveEditor();
		}
		if (myActiveEditor != null) {
			myActiveEditor
					.removePropertyListener(getMyEditorPtopertyChangeListener());
		}
		// we are better NOT doing this!
		// super.setActiveEditor(part);
		setActivePage(part);
		myActiveEditor = part;
		if (myActiveEditor instanceof IEditingDomainProvider) {
			myActiveEditor
					.addPropertyListener(getMyEditorPtopertyChangeListener());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.part.MultiPageEditorActionBarContributor#setActivePage
	 * (org.eclipse.ui.IEditorPart)
	 */
	@Override
	public void setActivePage(IEditorPart activeEditor) {
		if (activeEditor instanceof MOSKittMultiPageEditor) {
			activeEditor = ((MOSKittMultiPageEditor) activeEditor)
					.getActiveEditor();
		}
		setActiveActionBars(getDiagramSubActionBars(activeEditor), activeEditor);
	}

	/**
	 * Switches the active action bars.
	 */
	private void setActiveActionBars(SubActionBarsExt actionBars,
			IEditorPart activeEditor) {
		if (myActiveEditorActionBars != null
				&& !myActiveEditorActionBars.equals(actionBars)) {
			myActiveEditorActionBars.deactivate();
		}
		myActiveEditorActionBars = actionBars;
		if (myActiveEditorActionBars != null) {
			myActiveEditorActionBars.setEditorPart(activeEditor);
			myActiveEditorActionBars.activate();
			myActiveEditorActionBars.updateActionBars();
		}
	}

	SubActionBarsExt subActionBarsExt = null;

	/**
	 * @return the sub cool bar manager for the diagram editor.
	 */
	public SubActionBarsExt getDiagramSubActionBars(IEditorPart editorPart) {
		if (editorPart instanceof CachedResourcesDiagramEditor) {
			CachedResourcesDiagramEditor diagramEditor = (CachedResourcesDiagramEditor) editorPart;
			String editorID = diagramEditor.getEditorID();
			IEditorActionBarContributor editorActionBarContributor = getEditorActionBarContributorForEditor(editorPart);
			if (subActionBarsExt == null) {
				subActionBarsExt = new SubActionBarsExt(getPage(),
						myActionBars2, editorActionBarContributor, editorID);
			} else {
				if (subActionBarsExt.getContributor() == null) {
					subActionBarsExt = new SubActionBarsExt(getPage(),
							myActionBars2, editorActionBarContributor, editorID);
				}
			}
			return subActionBarsExt;
		}
		return null;
	}

	/**
	 * Shared ActionBarsContributor for all the editors in a MultiPageEditor.
	 */
	private MOSKittActionBarContributor sharedEditorsContributor = null;

	/**
	 * Gets the actual {@link IEditorActionBarContributor} for the an editor
	 * part. <br>
	 * If the editor is a CachedResourcesDiagramEditor a shared instance of
	 * AcionBarsContributor will be provided.
	 * 
	 * @param editor
	 * @return
	 */
	protected IEditorActionBarContributor getEditorActionBarContributorForEditor(
			IEditorPart editor) {
		if (editor instanceof CachedResourcesDiagramEditor) {
			String editorID = ((CachedResourcesDiagramEditor) editor)
					.getEditorID();
			Class editorClass = ((CachedResourcesDiagramEditor) editor)
					.getClass();
			if (sharedEditorsContributor == null) {
				sharedEditorsContributor = new MOSKittActionBarContributor();
			}
			sharedEditorsContributor.setEditorClass(editorClass);
			sharedEditorsContributor.setEditorID(editorID);
			sharedEditorsContributor.setActiveEditor(editor);
			return sharedEditorsContributor;
		} else {
			return (IEditorActionBarContributor) editor
					.getAdapter(IEditorActionBarContributor.class);
		}
	}

}
