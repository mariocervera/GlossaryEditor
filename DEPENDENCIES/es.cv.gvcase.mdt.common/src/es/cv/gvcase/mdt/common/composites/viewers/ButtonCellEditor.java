/*******************************************************************************
 * Copyright (c) 2009 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana. All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Marc Gil Sendra (Prodevelop) - initial API implementation
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.composites.viewers;

import java.text.MessageFormat;

import org.eclipse.gmf.runtime.common.ui.services.properties.extended.IPropertyAction;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;

/**
 * @author mgil
 * 
 *         A Cell Editor with one Button. The button is only shown when
 *         selecting the Cell
 */
public abstract class ButtonCellEditor extends CellEditor {
	/**
	 * The cell editor control itself
	 */
	protected Composite editor;

	/**
	 * Font used by all controls
	 */
	protected Font font;

	/**
	 * The label part of the editor
	 */
	protected Control label;

	/**
	 * Array of the editor's buttons
	 */
	protected Button button;

	/**
	 * The value of the cell editor; initially null
	 */
	protected Object value = null;

	/**
	 * Internal layout manager for button cell editors
	 */
	protected class ButtonCellLayout extends Layout {

		protected Point computeSize(Composite composite, int wHint, int hHint,
				boolean flushCache) {
			if (wHint != SWT.DEFAULT && hHint != SWT.DEFAULT) {
				return new Point(wHint, hHint);
			}
			return button.computeSize(SWT.DEFAULT, SWT.DEFAULT, flushCache);
		}

		protected void layout(Composite composite, boolean flushCache) {
			button.setBounds(editor.getClientArea());
		}
	}

	/**
	 * @param parent
	 *            The parent control
	 */
	public ButtonCellEditor(Composite parent) {
		this(parent, SWT.NONE);
	}

	/**
	 * @param parent
	 *            The parent control
	 * @param style
	 *            The style bits
	 */
	public ButtonCellEditor(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected Control createControl(Composite parent) {
		font = parent.getFont();
		Color bg = parent.getBackground();

		// create the cell editor
		editor = new Composite(parent, getStyle());
		editor.setFont(font);
		editor.setBackground(bg);
		editor.setLayout(new ButtonCellLayout());

		// create the label
		if (isModifiable()) {
			label = (new Text(editor, SWT.LEFT));
		} else {
			label = (new Label(editor, SWT.LEFT));
		}
		label.setFont(font);
		label.setBackground(bg);
		updateLabel(value);

		// create button
		button = new Button(editor, SWT.DOWN);
		button.setText(getLabelButton());
		button.setFont(font);

		// selection listener
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent event) {
				if (getButtonAction() == null) {
					return;
				}
				Object newValue = getButtonAction().execute(editor);
				if (newValue != null) {
					boolean newValidState = isCorrect(newValue);
					if (newValidState) {
						markDirty();
						doSetValue(newValue);
					} else {
						setErrorMessage(MessageFormat.format(getErrorMessage(),
								new Object[] { newValue.toString() }));
					}
					fireApplyEditorValue();
				}
			}
		});

		// key listener
		button.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.character == '\u001b') { // Escape char
					fireCancelEditor();
				}
			}
		});

		setValueValid(true);

		return editor;
	}

	protected boolean isModifiable() {
		return false;
	}

	@Override
	protected Object doGetValue() {
		return value;
	}

	@Override
	protected void doSetFocus() {
		button.setFocus();
	}

	@Override
	protected void doSetValue(Object value) {
		this.value = value;
		updateLabel(value);
	}

	protected void updateLabel(Object val) {
		if (label == null)
			return;

		String text = ""; //$NON-NLS-1$
		if (val != null) {
			text = val.toString();
		}
		if (label instanceof Label) {
			((Label) label).setText(text);
		} else if (label instanceof Text) {
			((Text) label).setText(text);
		}
	}

	protected abstract IPropertyAction getButtonAction();

	protected String getLabelButton() {
		return "Button";
	}

	public Label getLabel() {
		return (label != null && label instanceof Label) ? (Label) label : null;
	}

	public Text getText() {
		return (label != null && label instanceof Text) ? (Text) label : null;
	}

}
