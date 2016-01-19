/***************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte,
 * Generalitat de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Miguel Llacer San Fernando - (Prodevelop)
 *
 **************************************************************************/
package es.cv.gvcase.fefem.common.databinding;

import java.util.Date;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * The Class CDateTimeObservableValue.
 */
public class CDateTimeObservableValue extends AbstractObservableValue implements
		IObservableValue {

	/** The Control being observed here. */
	protected final CDateTime cDateTime;

	/** Flag to prevent infinite recursion in {@link #doSetValue(Object)}. */
	protected boolean updating = false;

	/** The "old" selection before a selection event is fired. */
	protected Date currentSelection;

	/** Old value. */
	private Date oldValue;

	/**
	 * Instantiates a new c date time observable value.
	 * 
	 * @param cDateTime
	 *            the c date time
	 */
	public CDateTimeObservableValue(CDateTime cDateTime) {
		super(SWTObservables.getRealm(cDateTime.getDisplay()));
		this.cDateTime = cDateTime;
		currentSelection = cDateTime.getSelection();

		cDateTime.addDisposeListener(disposeListener);
		cDateTime.addListener(SWT.Modify, updateListener);
	}

	/** The dispose listener. */
	private DisposeListener disposeListener = new DisposeListener() {
		public void widgetDisposed(DisposeEvent e) {
			CDateTimeObservableValue.this.dispose();
		}
	};

	/** The update listener. */
	private Listener updateListener = new Listener() {
		public void handleEvent(Event event) {
			if (!updating) {
				Date newValue = cDateTime.getSelection();

				if (newValue == null)
					return;

				if (!newValue.equals(oldValue)) {
					fireValueChange(Diffs.createValueDiff(oldValue, newValue));
					oldValue = newValue;
				}
			}
		}
	};

	@Override
	protected Object doGetValue() {
		return cDateTime.getSelection();
	}

	public Object getValueType() {
		return Date.class;
	}

	protected void doSetValue(Object value) {
		Date newValue;
		try {
			updating = true;
			oldValue = cDateTime.getSelection();
			newValue = (Date) value;
			cDateTime.setSelection(newValue);
			currentSelection = newValue;
			fireValueChange(Diffs.createValueDiff(oldValue, newValue));

		} finally {
			updating = false;
		}
	}

}
