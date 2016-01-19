/*******************************************************************************
 * Copyright (c) 2012 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana. All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Marc Gil Sendra (Prodevelop) [mgil@prodevelop.es] - initial API implementation
 ******************************************************************************/
package es.cv.gvcase.emf.ui.common.swt;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

/**
 * This class allows to update an SWT widget when a certain time is spend. This
 * is usually useful to be used with widgets that can receive several events to
 * be updated, but only the last one should be treated.
 * 
 * A typical use is with searchable buttons: you can type what you want to
 * search, but only when you stop typing a certain time, the search will be
 * done.
 * 
 * A sample execution could be the following one: <br>
 * <br>
 * 
 * <pre>
 * private AsynchSWTUpdater searchAsynchUpdater = AsynchSWTUpdater.getInstance();
 * 
 * searchText.addListener(SWT.Modify, new Listener() {
 * 	public void handleEvent(Event e) {
 * 		ISWTUpdater updater = new ISWTUpdater() {
 * 			public void execute() {
 * 				doFilterTree();
 * 			}
 * 		};
 * 		searchAsynchUpdater.addExecution(e, updater);
 * 	}
 * });
 * </pre>
 * 
 * @see es.cv.gvcase.emf.ui.common.swt.ISWTUpdater
 * 
 * @author Marc Gil (mgil@prodevelop.es)
 */
public class AsynchSWTUpdater {
	private Event event;
	private Display display;
	private int milliseconds;

	/**
	 * Get a new instance of a Asynch SWT Updater. The waiting time before
	 * execute the updater function is set as 500 milliseconds
	 */
	public static AsynchSWTUpdater getInstance() {
		return getInstance(500);
	}

	/**
	 * Get a new instance of a Asynch SWT Updater. You can specify the waiting
	 * time before execute the updater function
	 */
	public static AsynchSWTUpdater getInstance(int milliseconds) {
		return new AsynchSWTUpdater(milliseconds);
	}

	/**
	 * Creates a new instance for the Asynch SWT Updater
	 */
	private AsynchSWTUpdater(int milliseconds) {
		this.display = Display.getCurrent();
		this.event = null;
		this.milliseconds = milliseconds;
	}

	/**
	 * Add a new execution for this update, indicating the Event that fired the
	 * widget update and the ISWTUpdater interface with the specification of the
	 * update
	 */
	public void addExecution(final Event e, final ISWTUpdater updater) {
		event = e;
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(milliseconds);
					if (e.equals(event)) {
						display.asyncExec(new Runnable() {
							public void run() {
								updater.execute();
							}
						});
					}
				} catch (InterruptedException e1) {
				}
			}
		};
		thread.start();
	}
}
