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
 * A Custom Spinner to be used with float values. Additionally add listeners
 * when a key (distinct of Return key) is pressed, a mouse button (left button)
 * is pressed and focus is lost. In all of these cases a handleTextModified
 * function is called
 * 
 * @author mgil
 */
@SuppressWarnings("restriction")
public abstract class CustomFloatSpinnerWidget extends Spinner {
	public static final String EXP_NUMERIC_PATTERN = "^[-\\d][\\d]*\\.?[\\d]*"; //$NON-NLS-1$
	public static final Pattern FLOAT_PATTERN = Pattern
			.compile(EXP_NUMERIC_PATTERN);

	private KeyListener keyListener = null;
	private MouseListener mouseListener = null;
	private FocusListener focusListener = null;

	private int mult = 10;

	public CustomFloatSpinnerWidget(Composite parent, int min, int max) {
		super(parent, SWT.BORDER);

		configureMult();
		setMinimum(min);
		setMaximum(max * mult);
		setIncrement((int) mult / 2);
		setPageIncrement(10 * mult);
		setDigits(getNumberOfDigits());

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

	protected void configureMult() {
		String mult = "1";
		for (int i = 0; i < getNumberOfDigits(); i++) {
			mult += "0";
		}
		this.mult = Integer.parseInt(mult);
	}

	/**
	 * Get the number of digits
	 */
	public int getNumberOfDigits() {
		return 1;
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
		float val = getSelection() / (float) mult;
		return FLOAT_PATTERN.matcher(String.valueOf(val)).matches()
				&& val >= getMinimum() && val <= getMaximum();
	}

	/**
	 * Get the number witch will be used to convert the integer value to float
	 * and float value to integer
	 */
	public int getMult() {
		return mult;
	}

	protected abstract void handleTextModified(Object receiver);

}
