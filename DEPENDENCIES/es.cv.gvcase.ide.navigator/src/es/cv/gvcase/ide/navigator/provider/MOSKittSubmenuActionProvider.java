/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Francisco Javier Cano Muñoz (Prodevelop) – Initial API 
 * implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.ide.navigator.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.MenuManager;

/**
 * Specialization of {@link MOSKittCommonActionProvider} to be used in menu and
 * submenu contributions.
 * 
 * @author <a href="mailto:fjcano@prodevelop.es">Francisco Javier Cano Muñoz</a>
 */
public class MOSKittSubmenuActionProvider extends MOSKittCommonActionProvider {

	/**
	 * Organizes the given {@link Collection} of {@link IAction}s.
	 * 
	 * @param createActions
	 *            {@link Collection} of IActions to organize
	 * @param token
	 *            {@link String} that the {@link StringTokenizer} will use to
	 *            trim each IAction's text.
	 * 
	 * @return a {@link Map} associating Strings to Collections of IActions.
	 */
	protected Map<String, Collection<IAction>> extractSubmenuActions(
			Collection<IAction> createActions, String token) {
		Map<String, Collection<IAction>> createSubmenuActions = new LinkedHashMap<String, Collection<IAction>>();
		if (createActions != null) {
			for (Iterator<IAction> actions = createActions.iterator(); actions
					.hasNext();) {
				IAction action = actions.next();
				StringTokenizer st = new StringTokenizer(action.getText(),
						token);
				if (st.countTokens() == 2) {
					String text = st.nextToken().trim();
					Collection<IAction> submenuActions = createSubmenuActions
							.get(text);
					if (submenuActions == null) {
						createSubmenuActions.put(text,
								submenuActions = new ArrayList<IAction>());
					}
					action.setText(st.nextToken().trim());
					submenuActions.add(action);
					actions.remove();
				}
			}
		}
		return createSubmenuActions;
	}

	/**
	 * Organizes the given {@link Collection} of {@link IAction}s.
	 * 
	 * @param createActions
	 *            {@link Collection} of IActions to organize
	 * @param token
	 *            {@link String} that the {@link StringTokenizer} will use to
	 *            trim each IAction's text.
	 * 
	 * @return a {@link Map} associating Strings to Collections of IActions.
	 */
	protected Map<String, Collection<IAction>> extractSubmenuActions(
			Collection<IAction> createActions, String token,
			ICreateChildSubmenuFilter filter) {
		Map<String, Collection<IAction>> createSubmenuActions = new LinkedHashMap<String, Collection<IAction>>();
		if (createActions != null) {
			for (Iterator<IAction> actions = createActions.iterator(); actions
					.hasNext();) {
				IAction action = actions.next();
				StringTokenizer st = new StringTokenizer(action.getText(),
						token);
				if (st.countTokens() == 2) {
					String text = st.nextToken().trim();
					boolean show = true;
					if (filter != null) {
						show = filter.showAction(text);
					}
					if (show) {
						Collection<IAction> submenuActions = createSubmenuActions
								.get(text);
						if (submenuActions == null) {
							createSubmenuActions.put(text,
									submenuActions = new ArrayList<IAction>());
						}
						action.setText(st.nextToken().trim());
						submenuActions.add(action);
					}
					actions.remove();
				}
			}
		}
		return createSubmenuActions;
	}

	/**
	 * Fills an {@link IContributionManager} with the given {@link Collection}
	 * of {@link IAction}s.
	 * 
	 * @param manager
	 *            the manager
	 * @param actions
	 *            the actions
	 * @param contributionID
	 *            the contribution id
	 */
	protected void populateManager(IContributionManager manager,
			Collection<? extends IAction> actions, String contributionID) {
		if (actions != null) {
			for (IAction action : actions) {
				if (contributionID != null) {
					manager.insertBefore(contributionID, action);
				} else {
					manager.add(action);
				}
			}
		}
	}

	/**
	 * Fills a {@link IContributionManager} with two levels of menus, as
	 * specified by the {@link Map} of {@link String}s to {@link Collection}s of
	 * {@link IAction}s.
	 * 
	 * @param manager
	 *            the manager
	 * @param submenuActions
	 *            the submenu actions
	 * @param contributionID
	 *            the contribution id
	 */
	protected void populateManager(IContributionManager manager,
			Map<String, Collection<IAction>> submenuActions,
			String contributionID) {
		if (submenuActions != null) {
			for (Map.Entry<String, Collection<IAction>> entry : submenuActions
					.entrySet()) {
				MenuManager submenuManager = new MenuManager(entry.getKey());
				if (contributionID != null) {
					manager.insertBefore(contributionID, submenuManager);
				} else {
					manager.add(submenuManager);
				}
				populateManager(submenuManager, entry.getValue(), null);
			}
		}
	}

}
