/*******************************************************************************
 * Copyright (c) 2010 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Marc Gil Sendra (Prodevelop) – Initial implementation
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.sections;

import java.util.regex.Pattern;

import org.eclipse.emf.ecoretools.tabbedproperties.internal.utils.ColorRegistry;
import org.eclipse.emf.ecoretools.tabbedproperties.sections.AbstractTabbedPropertySection;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;

import es.cv.gvcase.mdt.common.sections.descriptions.ControlDescriptionDecoratorProvider;

/**
 * Provides an Spinner Section to be used in the Property Tabs
 * 
 * @author mgil
 */
public abstract class AbstractSpinnerPropertySection extends
		AbstractTabbedPropertySection {

	/** The text control for the section. */
	private Spinner spinner;

	/** The label used with to identify the Section */
	private CLabel nameLabel;

	/**
	 * A helper to listen for events that indicate that a text field has been
	 * changed.
	 */
	private KeyListener keyListener = null;

	/**
	 * A helper to listen for events that indicate that a text field has been
	 * clicked.
	 */
	private MouseListener mouseListener = null;

	/**
	 * A helper to listen for events when control get or lose the focus
	 */
	private FocusListener focusListener = null;

	/** Predefined string pattern value for numerics and absolute with '-' : -25 */
	public static final String ABS_NUMERICS_PATTERN = "^[-\\d][\\d]*"; //$NON-NLS-1$   

	/** The Pattern used to check an Integer value */
	public static final Pattern INTEGER_PATTERN = Pattern
			.compile(ABS_NUMERICS_PATTERN);

	@Override
	protected EditingDomain getEditingDomain() {
		try {
			return super.getEditingDomain();
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}

	protected void createWidgets(Composite composite) {
		spinner = new Spinner(composite, getStyle());
		spinner.setMinimum(getMinimumValue());
		spinner.setMaximum(getMaximumValue());
		spinner.setIncrement(getIncrement());
		spinner.setPageIncrement(getPageIncrement());

		nameLabel = getWidgetFactory().createCLabel(composite, getLabelText());

		// add control decorators that show the description of features
		ControlDescriptionDecoratorProvider.addDescriptionDecoration(spinner,
				getFeature());
	}

	/**
	 * check if this section should be enabled or not
	 */
	protected boolean isEnabled() {
		return true;
	}

	/**
	 * get the Minimum Value of the Spinner
	 */
	protected int getMinimumValue() {
		return Integer.MIN_VALUE;
	}

	/**
	 * get the Maximum Value of the Spinner
	 */
	protected int getMaximumValue() {
		return Integer.MAX_VALUE;
	}

	/**
	 * get the Increment Value of the Spinner when press the Up/Down arrows
	 */
	protected int getIncrement() {
		return 1;
	}

	/**
	 * get the Increment Value of the Spinner when press the Up/Down keys
	 */
	protected int getPageIncrement() {
		return 10;
	}

	protected void setSectionData(Composite composite) {
		FormData data = new FormData();
		data.left = new FormAttachment(0, getStandardLabelWidth(composite,
				new String[] { getLabelText() }));
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
		data.bottom = new FormAttachment(100, 0);
		spinner.setLayoutData(data);

		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(spinner,
				-ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(spinner, 0, SWT.TOP);
		nameLabel.setLayoutData(data);
	}

	protected void hookListeners() {
		// listen when press any key
		spinner.addKeyListener(getKeyListener());

		// listen when click left button of the mouse
		spinner.addMouseListener(getMouseListener());

		// listen when focus is lost
		spinner.addFocusListener(getFocusListener());

		// listen when modify the value
		spinner.addListener(SWT.Modify, new Listener() {
			public void handleEvent(Event e) {
				verifyField(e);
			}
		});
	}

	public KeyListener getKeyListener() {
		if (keyListener == null) {
			keyListener = new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.character == '\r' || e.character == '\n') {
						handleTextModified();
					}
				}
			};
		}
		return keyListener;
	}

	public MouseListener getMouseListener() {
		if (mouseListener == null) {
			mouseListener = new MouseAdapter() {
				@Override
				public void mouseUp(MouseEvent e) {
					if (e.button == 1) {
						handleTextModified();
					}
				}
			};
		}
		return mouseListener;
	}

	public FocusListener getFocusListener() {
		if (focusListener == null) {
			focusListener = new FocusAdapter() {
				@Override
				public void focusLost(FocusEvent e) {
					handleTextModified();
				}
			};
		}
		return focusListener;
	}

	public void refresh() {
		spinner.setEnabled(isEnabled());
		spinner.setSelection(getFeatureAsInteger());
	}

	/**
	 * Handle the text modified event. When there is any problem while creating
	 * and executing the command, the view will be only refreshed
	 */
	protected void handleTextModified() {
		if (isTextValid()) {
			createCommand(getOldFeatureValue(), getNewFeatureValue(spinner
					.getSelection()));
		} else {
			refresh();
		}
	}

	/**
	 * Handle action when the focus is gained Default action is to do nothing.
	 * Clients may override this method if they desire a particular action to be
	 * executed when the text control gain the focus.
	 */
	protected void focusIn() {
		// Do nothing
	}

	/**
	 * Handle action when the focus is lost. Default action is to do nothing.
	 * Clients may override this method if they desire a particular action to be
	 * executed when the text control lost the focus.
	 */
	protected void focusOut() {
		// Do nothing
	}

	/**
	 * Get the style of the text widget. By default, this is a single line text
	 * 
	 * @return the style
	 */
	protected int getStyle() {
		return SWT.SINGLE | SWT.BORDER;
	}

	/**
	 * @return the text
	 */
	protected Spinner getSpinner() {
		return spinner;
	}

	/**
	 * @return the nameLabel
	 */
	public CLabel getNameLabel() {
		return nameLabel;
	}

	protected void verifyField(Event e) {
		if (isTextValid()) { //$NON-NLS-1$
			setErrorMessage(null);
			spinner.setBackground(null);
			e.doit = true;
		} else {
			setErrorMessage("Invalid value");
			spinner.setBackground(ColorRegistry.COLOR_ERROR);
			e.doit = false;
		}
	}

	protected Integer getFeatureAsInteger() {
		return getFeatureInteger();
	}

	protected Object getOldFeatureValue() {
		return getFeatureInteger();
	}

	protected Object getNewFeatureValue(int value) {
		return new Integer(value);
	}

	protected boolean isTextValid() {
		int value = spinner.getSelection();
		return INTEGER_PATTERN.matcher(String.valueOf(value)).matches()
				&& value >= getMinimumValue() && value <= getMaximumValue();
	}

	/**
	 * Get the text value of the feature for the text field for the section.
	 * 
	 * @return the text value of the feature.
	 */
	protected abstract Integer getFeatureInteger();

}
