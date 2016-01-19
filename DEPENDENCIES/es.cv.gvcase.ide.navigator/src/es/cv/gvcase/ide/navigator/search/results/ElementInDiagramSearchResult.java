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
package es.cv.gvcase.ide.navigator.search.results;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.search.core.engine.IModelSearchQuery;
import org.eclipse.emf.search.core.results.AbstractModelResultEntryVisitor;
import org.eclipse.emf.search.core.results.IModelResultEntry;
import org.eclipse.emf.search.core.results.IModelResultEntryVisitor;
import org.eclipse.emf.search.core.results.IModelSearchResult;
import org.eclipse.emf.search.core.results.ModelSearchResult;
import org.eclipse.emf.search.core.results.ModelSearchResultEvent;
import org.eclipse.emf.search.utils.ModelSearchImagesUtil;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResultListener;
import org.eclipse.search.ui.SearchResultEvent;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.IEditorMatchAdapter;
import org.eclipse.search.ui.text.IFileMatchAdapter;
import org.eclipse.search.ui.text.Match;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.osgi.framework.Bundle;

/**
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 *
 */
public class ElementInDiagramSearchResult extends AbstractTextSearchResult
		implements IModelSearchResult, IEditorMatchAdapter, IFileMatchAdapter {
	/**
	 * Currently considered search query
	 */
	private IModelSearchQuery searchQuery;

	/**
	 * Current Search Entries
	 */
	private Map<Object, Collection<Object>> searchEntries;

	/**
	 * Search result listeners
	 */
	private Collection<ISearchResultListener> searchResultListeners;

	/**
	 * Constructor
	 * 
	 * @param query
	 *            model search query to consider
	 */
	public ElementInDiagramSearchResult(IModelSearchQuery query) {
		searchQuery = query;
		searchEntries = new HashMap<Object, Collection<Object>>();
		searchResultListeners = new ArrayList<ISearchResultListener>();
	}

	/**
	 * Inserts & merges an entry sequence into an existing entry sequence
	 * hierarchy.
	 */
	private void insert2(Collection<Object> currentEntrySubHierarchyCollection,
			IModelResultEntry entryToInsert, boolean notify) {
		boolean alreadyExist = false;
		for (Object currentEntrySubHierarchy : currentEntrySubHierarchyCollection) {
			if (alreadyExist = currentEntrySubHierarchy.equals(entryToInsert)) {
				insert3(currentEntrySubHierarchy, entryToInsert.getResults(),
						notify);
				break;
			}
		}
		if (!alreadyExist) {
			currentEntrySubHierarchyCollection.add(entryToInsert);
			if (notify) {
				fireItemAdded(entryToInsert);
			}
		}
	}

	/**
	 * Inserts & merges an entry into an existing entry sequence hierarchy.
	 */
	private void insert3(Object currentEntrySubHierarchy,
			Collection<Object> entrySubtreeToInsertCollection, boolean notify) {
		for (Object e2i : entrySubtreeToInsertCollection) {
			if (e2i instanceof IModelResultEntry
					&& currentEntrySubHierarchy instanceof IModelResultEntry) {
				insert2(((IModelResultEntry) currentEntrySubHierarchy)
						.getResults(), (IModelResultEntry) e2i, notify);
			}
		}
	}

	/**
	 * Inserts newly builded matched object model result thread hierarchy into
	 * the previously built total model results hierarchy.
	 * 
	 * @param file
	 *            The file model result was matched from
	 * @param entry
	 *            A newly created model result entry.
	 * 
	 * @return inserted {@link IModelResultEntry} entry
	 */
	public IModelResultEntry insert(Object file, IModelResultEntry entry,
			boolean notify) {
		if (searchEntries.get(file) == null) {
			searchEntries.put(file, new ArrayList<Object>());
		}
		insert2(searchEntries.get(file), entry, notify);
		return entry;
	}

	/**
	 * Computes the total amount of matches for all non zero result resources.
	 * 
	 * @return Total matches number
	 */
	public int getTotalMatches() {
		int result = 0;
		for (Object f : searchEntries.keySet()) {
			result += getMatchesNumberForFile(f);
		}
		return result;
	}

	/**
	 * Computes the total amount of matches for all non zero result resources.
	 * 
	 * @param eCollection
	 *            A collection to collect the matching results from
	 * 
	 * @return Total matches number
	 */
	public int getMatches(Collection<Object> eCollection) {
		int result = 0;
		synchronized (eCollection) {
			try {
				for (Object e : eCollection) {
					if (e instanceof IModelResultEntry) {
						IModelResultEntry entry = (IModelResultEntry) e;
						result += (entry.wasMatchedAtleastOnce() ? 1 : 0)
								+ getMatches(entry.getResults());
					}
				}
			} catch (Throwable e) {
				// ugly
			}
		}
		return result;
	}

	/**
	 * Computes matches number for given resource.
	 * 
	 * @param file
	 *            resource for which compute match number
	 */
	public int getMatchesNumberForFile(Object file) {
		return getMatches(searchEntries.get(file));
	}

	/**
	 * Computes matches number for given resource.
	 * 
	 * @param file
	 *            resource for which compute match number
	 */
	public Match[] getMatchesForFile(Object file) {
		if (searchEntries.get(file) != null
				&& !searchEntries.get(file).isEmpty()) {
			return searchEntries.get(file).toArray(new Match[0]);
		}
		return new Match[0];
	}

	/**
	 * Called any time a new model search result is added in the existing
	 * hierarchy
	 */
	private IModelResultEntry fireItemAdded(IModelResultEntry item) {
		SearchResultEvent searchResultEvent = new ModelSearchResultEvent(this);
		for (ISearchResultListener listener : searchResultListeners) {
			listener.searchResultChanged(searchResultEvent);
		}
		return item;
	}

	/**
	 * Get root items to be displayed in search result page contributed to
	 * org.eclipse.search.searchResultViewPages extension point.
	 * 
	 * @return A collection of result hierarchy tree roots
	 */
	public Map<Object, Collection<Object>> getRootResultHierarchies() {
		return searchEntries;
	}

	/**
	 * Basically transforms result hierarchical trees with potential
	 * intermediate non matched items into a list of matched only result
	 * entries.
	 * 
	 * @param visitor
	 *            A 'flattener' visitor to be applied
	 */
	private Collection<IModelResultEntry> accept(
			IModelResultEntryVisitor<IModelResultEntry> visitor) {
		for (Collection<Object> entries : searchEntries.values()) {
			for (Object entry : entries) {
				if (entry instanceof IModelResultEntry) {
					accept2(visitor, (IModelResultEntry) entry);
				}
			}
		}
		return visitor.getResults();
	}

	/**
	 * Applies the visitor to a particular result hierarchy tree root among all
	 * first level possibles.
	 * 
	 * @param visitor
	 *            A 'flattener' visitor to be applied
	 * @param entry
	 *            A particular result hierarchy tree root
	 */
	private void accept2(IModelResultEntryVisitor<IModelResultEntry> visitor,
			IModelResultEntry entry) {
		visitor.visit(entry);
		for (Object e : entry.getResults()) {
			if (e instanceof IModelResultEntry) {
				accept2(visitor, (IModelResultEntry) e);
			}
		}
	}

	/**
	 * Specialized visitor which role is to walk result hierarchy subtrees and
	 * to return a list of result hierarchy tree matched items only
	 */
	private class DefaultFlattenerModelResultEntryVisitor extends
			AbstractModelResultEntryVisitor<IModelResultEntry> {
	}

	/**
	 * Specialized visitor which role is to walk result hierarchy subtrees and
	 * to return a list of result hierarchy tree matched items only
	 */
	private class FileSpecifiedFlattenerModelResultEntryVisitor extends
			AbstractModelResultEntryVisitor<IModelResultEntry> {
		private Object target;

		public FileSpecifiedFlattenerModelResultEntryVisitor(Object file) {
			target = file;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean isValid(IModelResultEntry entry) {
			return super.isValid(entry) && entry.getTarget().equals(target);
		}
	}

	/**
	 * Get result hierarchy tree in a flattened form (i.e a list of all (and
	 * only) matched items).
	 * 
	 * @return A list of result hierarchy tree matched items only
	 */
	public Collection<IModelResultEntry> getResultsFlatenned() {
		return accept(new DefaultFlattenerModelResultEntryVisitor());
	}

	/**
	 * Get result hierarchy tree in a flattened form (i.e a list of all (and
	 * only) matched items).
	 * 
	 * @return A list of result hierarchy tree matched items only
	 */
	public Collection<IModelResultEntry> getResultsFlatennedForFile(Object file) {
		return accept(new FileSpecifiedFlattenerModelResultEntryVisitor(file));
	}

	/**
	 * {@inheritDoc}
	 */
	public void addListener(ISearchResultListener l) {
		if (!searchResultListeners.contains(l)) {
			searchResultListeners.add(l);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeListener(ISearchResultListener l) {
		searchResultListeners.remove(l);
	}

	/**
	 * {@inheritDoc}
	 */
	public void clean() {
		searchEntries = new HashMap<Object, Collection<Object>>();
	}

	/**
	 * {@inheritDoc}
	 */
	public ImageDescriptor getImageDescriptor() {
		Bundle bundle = Platform.getBundle(searchQuery.getBundleSymbolicName());
		return ModelSearchImagesUtil.getImageDescriptor(bundle, searchQuery
				.getResultImagePath());
	}

	/**
	 * {@inheritDoc}
	 */
	public Image getImage() {
		Bundle bundle = Platform.getBundle(searchQuery.getBundleSymbolicName());
		return ModelSearchImagesUtil.getImage(bundle, searchQuery
				.getResultImagePath());
	}

	/**
	 * {@inheritDoc}
	 */
	public ISearchQuery getQuery() {
		return searchQuery;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getLabel() {
		return getQuery().getLabel(); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	public String getTooltip() {
		return "Message_from_ElementInDiagramSearchResult#getToolTip()";
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isShownInEditor(Match match, IEditorPart editor) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public IFile getFile(Object element) {
		if (element instanceof IModelResultEntry) {
			Object f = ((IModelResultEntry) element).getFile();
			return f instanceof IFile ? (IFile) f : null;
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Object> getResultsObjectsAsList() {
		ArrayList<Object> lst = new ArrayList<Object>();
		for (IModelResultEntry e : getResultsFlatenned()) {
			lst.add(e.getSource());
		}
		return lst;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Object> getResultsObjectsForFileAsList(Object file) {
		ArrayList<Object> lst = new ArrayList<Object>();
		for (IModelResultEntry e : getResultsFlatennedForFile(file)) {
			lst.add(e.getSource());
		}
		return lst;
	}

	@Override
	public IEditorMatchAdapter getEditorMatchAdapter() {
		return this;
	}

	@Override
	public IFileMatchAdapter getFileMatchAdapter() {
		return this;
	}

	public Match[] computeContainedMatches(AbstractTextSearchResult result,
			IEditorPart editor) {
		IEditorInput editorInput = editor.getEditorInput();
		Object adapted = editorInput.getAdapter(IFile.class);
		if (adapted instanceof IFile) {
			return computeContainedMatches(result, ((IFile) adapted));
		}
		return new Match[0];
	}

	public Match[] computeContainedMatches(AbstractTextSearchResult result,
			IFile file) {
		if (result instanceof ModelSearchResult) {
			return ((ModelSearchResult) result).getMatchesForFile(file);
		}
		return new Match[0];
	}
}
