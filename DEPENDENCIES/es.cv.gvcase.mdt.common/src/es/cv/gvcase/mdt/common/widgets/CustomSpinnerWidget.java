/*******************************************************************************
 * Copyright (c) 2012 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Marc Gil Sendra (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.widgets;

import java.util.regex.Pattern;

import org.eclipse.emf.ecoretools.tabbedproperties.internal.utils.ColorRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

/**
 * A custom Spinner that add listeners when a key (distinct of Return key) is
 * pressed, a mouse button (left button) is pressed and focus is lost. In all of
 * these cases a handleTextModified function is called
 * 
 * @author mgil
 */
@SuppressWarnings("restriction")
public abstract class CustomSpinnerWidget extends Spinner {
	public static final String ABS_NUMERICS_PATTERN = "^[-\\d][\\d]*"; //$NON-NLS-1$   
	public static final Pattern INTEGER_PATTERN = Pattern
			.compile(ABS_NUMERICS_PATTERN);

	private KeyListener keyListener = null;
	private MouseListener mouseListener = null;
	private FocusListener focusListener = null;

	public CustomSpinnerWidget(Composite parent, int min, int max, int inc,
			int pageInc) {
		super(parent, SWT.BORDER);

		setMinimum(min);
		setMaximum(max);
		setIncrement(inc);
		setPageIncrement(pageInc);

		new TabbedPropertySheetWidgetFactory().adapt(this);

		// listen when press any key
		addKeyListener(getKeyListener());

		// listen when click left button of the mouse
		addMouseListener(getMouseListener());

		// listen when focus is lost
		addFocusListener(getFocusListener());

		// listen when modify the value
		addListener(SWT.Modify, new Listener() {
			public void handleEvent(Event e) {
				verifyField(e);
			}
		});
	}

	@Override
	protected void checkSubclass() {
	}

	protected KeyListener getKeyListener() {
		if (keyListener == null) {
			keyListener = new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.character == '\r' || e.character == '\n') {
						if (isTextValid()) {
							handleTextModified(getData());
						}
					}
				}
			};
		}
		return keyListener;
	}

	protected MouseListener getMouseListener() {
		if (mouseListener == null) {
			mouseListener = new MouseAdapter() {
				@Override
				public void mouseUp(MouseEvent e) {
					if (e.button == 1) {
						if (isTextValid()) {
							handleTextModified(getData());
						}
					}
				}
			};
		}
		return mouseListener;
	}

	protected FocusListener getFocusListener() {
		if (focusListener == null) {
			focusListener = new FocusAdapter() {
				@Override
				public void focusLost(FocusEvent e) {
					if (isTextValid()) {
						handleTextModified(getData());
					}
				}
			};
		}
		return focusListener;
	}

	protected void verifyField(Event e) {
		if (isTextValid()) { //$NON-NLS-1$
			setBackground(null);
			e.doit = true;
		} else {
			setBackground(ColorRegistry.COLOR_ERROR);
			e.doit = false;
		}
	}

	protected boolean isTextValid() {
		int value = getSelection();
		return INTEGER_PATTERN.matcher(String.valueOf(value)).matches()
				&& value >= getMinimum() && value <= getMaximum();
	}

	protected abstract void handleTextModified(Object receiver);

}
