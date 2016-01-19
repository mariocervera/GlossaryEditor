/***************************************************************************
 * Copyright (c) 2008, 2009 Conselleria de Infraestructuras y Transporte,
 * Generalitat de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – initial api implementation
 *
 ******************************************************************************/
package es.cv.gvcase.ide.navigator.search.engine;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.search.core.engine.AbstractModelSearchQuery;
import org.eclipse.emf.search.core.engine.IModelSearchQuery;
import org.eclipse.emf.search.core.eval.IModelSearchQueryEvaluator;
import org.eclipse.emf.search.core.parameters.IModelSearchQueryParameters;
import org.eclipse.emf.search.core.results.AbstractModelSearchResultEntry;
import org.eclipse.emf.search.core.results.IModelResultEntry;
import org.eclipse.emf.search.core.results.IModelSearchResult;
import org.eclipse.emf.search.core.scope.IModelSearchScope;
import org.eclipse.search.ui.ISearchResult;

import es.cv.gvcase.ide.navigator.search.evaluator.ElementInDiagramSearchQueryEvaluator;
import es.cv.gvcase.ide.navigator.search.results.ElementInDiagramSearchResult;
import es.cv.gvcase.ide.navigator.search.results.ElementInDiagramSearchResultEntry;
import es.cv.gvcase.mdt.common.util.MDTUtil;

/**
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 * 
 */
public class ElementInDiagramSearchQuery extends AbstractModelSearchQuery {

	private IModelSearchResult modelSearchResult = null;

	public ElementInDiagramSearchQuery(String expr,
			IModelSearchQueryParameters parameters) {
		super(expr, parameters);
		modelSearchResult = new ElementInDiagramSearchResult(this);
	}

	protected AbstractModelSearchResultEntry buildSearchResultEntryHierarchy(
			Object resource, AbstractModelSearchResultEntry intermediate,
			Object container) {
		if (container instanceof EObject) {
			AbstractModelSearchResultEntry entryContainer = new ElementInDiagramSearchResultEntry(
					null, resource, container, false);
			entryContainer.addChildren(intermediate);
			intermediate.setParent(entryContainer);
			return buildSearchResultEntryHierarchy(resource, entryContainer,
					((EObject) container).eContainer());
		} else {
			return intermediate;
		}
	}

	@Override
	public IModelResultEntry buildSearchResultEntryHierarchy(Object resource,
			Object o) {
		AbstractModelSearchResultEntry e = new ElementInDiagramSearchResultEntry(
				null, resource, o, true);
		if (o instanceof EObject) {
			EObject c = (EObject) o;
			AbstractModelSearchResultEntry resultHierarchy = buildSearchResultEntryHierarchy(
					resource, e, c.eContainer());
			return resultHierarchy;
		}
		// Just in case we could deal with some nested exotic objects without
		// containment notions ^^
		return e;
	}

	@Override
	public String getBundleSymbolicName() {
		return es.cv.gvcase.ide.navigator.Activator.getDefault().getBundle()
				.getSymbolicName();
	}

	@Override
	public IModelSearchQueryEvaluator<IModelSearchQuery, ?> getEvaluator() {
		evaluator = getModelSearchParameters().getEvaluator();
		return evaluator != null ? evaluator
				: (evaluator = new ElementInDiagramSearchQueryEvaluator<IModelSearchQuery, Object>());
	}

	@Override
	public String getLabel() {
		// String scopeLabel = getModelSearchScope().getLabel();
		// String queryName = getName();
		// String queryLabel =
		// Messages.getString("EcoreTextualModelSearchQueryEvaluator.Label");
		// int matches = getModelSearchResult().getTotalMatches();
		// String matchesLabel =
		// Messages.getString("EcoreModelSearchQuery.matchesMessage2") +
		// (matches>1?Messages.getString("EcoreModelSearchQuery.matchesMessage3"):"");//$NON-NLS-1$
		// //$NON-NLS-2$ //$NON-NLS-3$
		// return scopeLabel + " " + queryName + " " + queryLabel + " : \'" +
		// getQueryExpression() + "\' (" + matches + " " + matchesLabel + ")";
		// //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		String label = "Search for " + getElementsNames() + " in "
				+ getSearchParticipants() + ".";
		return label;
	}

	protected String getElementsNames() {
		String names = "";
		Iterator iterator = getModelSearchParameters().getParticipantElements()
				.iterator();
		for (; iterator.hasNext();) {
			Object o = iterator.next();
			if (o instanceof ENamedElement) {
				names += ((ENamedElement) o).getName();
			} else {
				names += MDTUtil.getObjectNameOrEmptyString(o);
			}
			if (iterator.hasNext()) {
				names += ", ";
			}
		}
		return names;
	}

	protected String getSearchParticipants() {
		String participants = "";
		Iterator iterator = getModelSearchScope().getParticipants().iterator();
		for (; iterator.hasNext();) {
			Object o = iterator.next();
			if (o instanceof Resource) {
				participants += ((Resource) o).getURI().toString();
			} else {
				participants += MDTUtil.getObjectNameOrEmptyString(o);
			}
			if (iterator.hasNext()) {
				participants += ", ";
			}
		}
		return participants;
	}

	@Override
	protected boolean getMatchesNotificationMode() {
		// XXX: will need fixing
		return Boolean.TRUE;
	}

	@Override
	protected IModelSearchScope<?, ?> getModelSearchScope() {
		return getModelSearchParameters().getScope();
	}

	@Override
	public String getQueryImagePath() {
		return "icons/full/ovr16/relatedDiagram.png";
	}

	@Override
	public String getResultImagePath() {
		return "icons/full/ovr16/relatedDiagram.png";
	}

	@Override
	public IStatus search(Object resource, boolean notify,
			IProgressMonitor monitor) {
		try {
			if (monitor.isCanceled()) {
				return Status.CANCEL_STATUS;
			}

			((IModelSearchQueryEvaluator) getEvaluator()).eval(this, resource,
					notify);

			monitor.setTaskName(getLabel());

			if (monitor.isCanceled()) {
				return Status.CANCEL_STATUS;
			}
		} catch (Exception e) {
			return Status.CANCEL_STATUS;
		}
		return Status.OK_STATUS;
	}

	public IStatus search(Object resource, boolean notify) {
		try {
			((ElementInDiagramSearchQueryEvaluator) getEvaluator()).eval(this,
					resource, notify);
		} catch (Exception e) {
			return Status.CANCEL_STATUS;
		}
		return Status.OK_STATUS;
	}

	public IStatus search(Object resource) {
		search(resource, false);
		return Status.OK_STATUS;
	}

	@Override
	public IModelSearchResult getModelSearchResult() {
		return modelSearchResult;
	}

	@Override
	public ISearchResult getSearchResult() {
		return modelSearchResult;
	}

	public Collection<String> getTargetMetaModelIDs() {
		return Collections.emptyList();
	}

}
