/******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation 
 ****************************************************************************/
package es.cv.gvcase.mdt.common.actions;

import java.util.Iterator;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.gmf.runtime.common.core.service.IOperation;
import org.eclipse.gmf.runtime.emf.ui.services.action.AbstractModelActionFilterProvider;
import org.eclipse.jface.viewers.IStructuredSelection;

public class DuplicateActionFilterProvider extends
		AbstractModelActionFilterProvider {

	/**
	 * Never enable Duplicate Action
	 */
	protected boolean doTestAttribute(Object target, String name, String value) {
		return false;
	}

	protected boolean doProvides(IOperation operation) {
		return true;
	}

	/**
	 * Finds my editing domain by adating the current selection to
	 * <code>EObject</code>.
	 */
	protected TransactionalEditingDomain getEditingDomain(Object target) {

		TransactionalEditingDomain result = null;
		IStructuredSelection selection = getStructuredSelection();

		if (selection != null && !selection.isEmpty()) {

			for (Iterator i = selection.iterator(); i.hasNext()
					&& result == null;) {
				Object next = i.next();

				if (next instanceof IAdaptable) {
					EObject element = (EObject) ((IAdaptable) next)
							.getAdapter(EObject.class);

					if (element != null) {
						result = TransactionUtil.getEditingDomain(element);
					}
				}
			}
		}

		return result;
	}
}
