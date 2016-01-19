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
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;

import es.cv.gvcase.emf.ui.common.composites.EObjectChooserComposite;
import es.cv.gvcase.emf.ui.common.dialogs.ChooseDialog;
import es.cv.gvcase.emf.ui.common.utils.DataPath;
import es.cv.gvcase.mdt.common.util.MDTUtil;

/**
 * An abstract cell editor that uses a dialog. Dialog cell editors usually have
 * a label control on the left and a button on the right. Pressing the button
 * opens a dialog window (for example, a color dialog or a file dialog) to
 * change the cell editor's value. The cell editor's value is the value of the
 * dialog.
 * <p>
 * Subclasses may override the following methods:
 * <ul>
 * <li><code>createButton</code>: creates the cell editor's button control</li>
 * <li><code>createContents</code>: creates the cell editor's 'display value'
 * control</li>
 * <li><code>updateContents</code>: updates the cell editor's 'display value'
 * control after its value has changed</li>
 * <li><code>openDialogBox</code>: opens the dialog box when the end user
 * presses the button</li>
 * </ul>
 * </p>
 */
public class DialogCellEditor extends org.eclipse.jface.viewers.CellEditor {

	/**
	 * Image registry key for three dot image (value
	 * <code>"cell_editor_dots_button_image"</code>).
	 */
	public static final String CELL_EDITOR_IMG_DOTS_BUTTON = "cell_editor_dots_button_image";//$NON-NLS-1$

	/**
	 * The editor control.
	 */
	private Composite editor;

	/**
	 * The current contents.
	 */
	private Control contents;

	/**
	 * The label that gets reused by <code>updateLabel</code>.
	 */
	private Label defaultLabel;

	/**
	 * The button.
	 */
	private Button button;

	/**
	 * The unset button.
	 */
	private Button unsetButton;

	/**
	 * The list of Paths of selected elements
	 */
	private List<DataPath> paths;

	private ChooseDialog dialog;

	/**
	 * Listens for 'focusLost' events and fires the 'apply' event as long as the
	 * focus wasn't lost because the dialog was opened.
	 */
	private FocusListener buttonFocusListener;
	private FocusListener unsetButtonFocusListener;

	/**
	 * The value of this cell editor; initially <code>null</code>.
	 */
	private Object value = null;

	private boolean includeUnsetButton = false;

	protected ILabelProvider labelProvider;

	static {
		ImageRegistry reg = JFaceResources.getImageRegistry();
		reg.put(CELL_EDITOR_IMG_DOTS_BUTTON, ImageDescriptor.createFromFile(
				DialogCellEditor.class, "images/dots_button.gif"));//$NON-NLS-1$
	}

	/**
	 * Internal class for laying out the dialog.
	 */
	private class DialogCellLayout extends Layout {
		public void layout(Composite editor, boolean force) {
			Rectangle bounds = editor.getClientArea();
			Point buttonSize = button.computeSize(SWT.DEFAULT, SWT.DEFAULT,
					force);
			if (contents != null) {
				if (includeUnsetButton()) {
					contents.setBounds(0, 0, bounds.width - (buttonSize.x * 2),
							bounds.height);
				} else {
					contents.setBounds(0, 0, bounds.width - buttonSize.x,
							bounds.height);
				}
			}

			if (includeUnsetButton()) {
				button.setBounds(bounds.width - (buttonSize.x * 2), 0,
						buttonSize.x, bounds.height);
				unsetButton.setBounds(bounds.width - buttonSize.x, 0,
						buttonSize.x, bounds.height);
			} else {
				button.setBounds(bounds.width - buttonSize.x, 0, buttonSize.x,
						bounds.height);
			}
		}

		public Point computeSize(Composite editor, int wHint, int hHint,
				boolean force) {
			if (wHint != SWT.DEFAULT && hHint != SWT.DEFAULT) {
				return new Point(wHint, hHint);
			}
			Point contentsSize = contents.computeSize(SWT.DEFAULT, SWT.DEFAULT,
					force);
			Point buttonSize = null;
			if (includeUnsetButton()) {
				buttonSize = unsetButton.computeSize(SWT.DEFAULT, SWT.DEFAULT,
						force);
			} else {
				buttonSize = button
						.computeSize(SWT.DEFAULT, SWT.DEFAULT, force);
			}
			// Just return the button width to ensure the button is not clipped
			// if the label is long.
			// The label will just use whatever extra width there is
			Point result = new Point(buttonSize.x, Math.max(contentsSize.y,
					buttonSize.y));
			return result;
		}
	}

	protected boolean includeUnsetButton() {
		return includeUnsetButton;
	}

	/**
	 * Default DialogCellEditor style
	 */
	private static final int defaultStyle = SWT.NONE;

	/**
	 * Creates a new dialog cell editor with no control
	 * 
	 * @since 2.1
	 */
	public DialogCellEditor() {
		setStyle(defaultStyle);
	}

	/**
	 * Creates a new dialog cell editor parented under the given control. The
	 * cell editor value is <code>null</code> initially, and has no validator.
	 * 
	 * @param parent
	 *            the parent control
	 */
	protected DialogCellEditor(Composite parent) {
		this(parent, defaultStyle);
	}

	/**
	 * Creates a new dialog cell editor parented under the given control. The
	 * cell editor value is <code>null</code> initially, and has no validator.
	 * 
	 * @param parent
	 *            the parent control
	 * @param style
	 *            the style bits
	 * @since 2.1
	 */
	protected DialogCellEditor(Composite parent, int style) {
		super(parent, style);
		if (this.labelProvider == null) {
			this.labelProvider = getLabelProvider();
		}
	}

	public DialogCellEditor(Composite composite, ILabelProvider labelProvider) {
		this(composite);
		this.labelProvider = labelProvider;
	}

	/**
	 * Creates the button for this cell editor under the given parent control.
	 * <p>
	 * The default implementation of this framework method creates the button
	 * display on the right hand side of the dialog cell editor. Subclasses may
	 * extend or reimplement.
	 * </p>
	 * 
	 * @param parent
	 *            the parent control
	 * @return the new button control
	 */
	protected Button createButton(Composite parent, String label) {
		Button result = new Button(parent, SWT.DOWN);
		result.setText(label); //$NON-NLS-1$
		return result;
	}

	/**
	 * Creates the controls used to show the value of this cell editor.
	 * <p>
	 * The default implementation of this framework method creates a label
	 * widget, using the same font and background color as the parent control.
	 * </p>
	 * <p>
	 * Subclasses may reimplement. If you reimplement this method, you should
	 * also reimplement <code>updateContents</code>.
	 * </p>
	 * 
	 * @param cell
	 *            the control for this cell editor
	 * @return the underlying control
	 */
	protected Control createContents(Composite cell) {
		defaultLabel = new Label(cell, SWT.LEFT);
		defaultLabel.setFont(cell.getFont());
		defaultLabel.setBackground(cell.getBackground());
		return defaultLabel;
	}

	/*
	 * (non-Javadoc) Method declared on CellEditor.
	 */
	protected Control createControl(Composite parent) {

		Font font = parent.getFont();
		Color bg = parent.getBackground();

		editor = new Composite(parent, getStyle());
		editor.setFont(font);
		editor.setBackground(bg);
		editor.setLayout(new DialogCellLayout());

		contents = createContents(editor);
		updateContents(value);

		button = createButton(editor, "..."); //$NON-NLS-1$
		button.setFont(font);

		button.addKeyListener(new KeyAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.swt.events.KeyListener#keyReleased(org.eclipse.swt
			 * .events.KeyEvent)
			 */
			public void keyReleased(KeyEvent e) {
				if (e.character == '\u001b') { // Escape
					fireCancelEditor();
				}
			}
		});

		button.addFocusListener(getButtonFocusListener());

		button.addSelectionListener(new SelectionAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse
			 * .swt.events.SelectionEvent)
			 */
			public void widgetSelected(SelectionEvent event) {
				// Remove the button's focus listener since it's guaranteed
				// to lose focus when the dialog opens
				button.removeFocusListener(getButtonFocusListener());

				Object newValue = openDialogBox(editor);

				// Re-add the listener once the dialog closes
				button.addFocusListener(getButtonFocusListener());

				if (newValue instanceof EObjectChooserComposite.NullObject) {
					return;
				}

				boolean newValidState = isCorrect(newValue);
				if (newValidState) {
					markDirty();
					doSetValue(newValue);
				} else {
					// try to insert the current value into the error
					// message.
					setErrorMessage(MessageFormat.format(getErrorMessage(),
							new Object[] { newValue.toString() }));
				}
				fireApplyEditorValue();
			}
		});

		if (includeUnsetButton()) {
			unsetButton = createButton(editor, "-"); //$NON-NLS-1$
			unsetButton.setFont(font);

			unsetButton.addKeyListener(new KeyAdapter() {
				/*
				 * (non-Javadoc)
				 * 
				 * @see
				 * org.eclipse.swt.events.KeyListener#keyReleased(org.eclipse
				 * .swt .events.KeyEvent)
				 */
				public void keyReleased(KeyEvent e) {
					if (e.character == '\u001b') { // Escape
						fireCancelEditor();
					}
				}
			});

			unsetButton.addFocusListener(getUnsetButtonFocusListener());

			unsetButton.addSelectionListener(new SelectionAdapter() {
				/*
				 * (non-Javadoc)
				 * 
				 * @see
				 * org.eclipse.swt.events.SelectionListener#widgetSelected(org
				 * .eclipse .swt.events.SelectionEvent)
				 */
				public void widgetSelected(SelectionEvent event) {
					// Remove the button's focus listener since it's guaranteed
					// to lose focus when the dialog opens
					unsetButton
							.removeFocusListener(getUnsetButtonFocusListener());

					Object newValue = unset();

					if (newValue instanceof EObjectChooserComposite.NullObject) {
						return;
					}

					boolean newValidState = isCorrect(newValue);
					if (newValidState) {
						markDirty();
						doSetValue(newValue);
					} else {
						// try to insert the current value into the error
						// message.
						setErrorMessage(MessageFormat.format(getErrorMessage(),
								new Object[] { newValue.toString() }));
					}
					fireApplyEditorValue();
				}
			});
		}

		setValueValid(true);

		return editor;
	}

	/**
	 * This method is used to return a value to unset the associated Feature. Bu
	 * default returns null
	 * 
	 * @return
	 */
	protected Object unset() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * Override in order to remove the button's focus listener if the celleditor
	 * is deactivating.
	 * 
	 * @see org.eclipse.jface.viewers.CellEditor#deactivate()
	 */
	public void deactivate() {
		if (button != null && !button.isDisposed()) {
			button.removeFocusListener(getButtonFocusListener());
		}
		if (includeUnsetButton()) {
			if (unsetButton != null && !unsetButton.isDisposed()) {
				unsetButton.removeFocusListener(getUnsetButtonFocusListener());
			}
		}

		super.deactivate();
	}

	/*
	 * (non-Javadoc) Method declared on CellEditor.
	 */
	protected Object doGetValue() {
		return value;
	}

	protected void doSetFocus() {
	}

	/**
	 * Return a listener for button focus.
	 * 
	 * @return FocusListener
	 */
	private FocusListener getButtonFocusListener() {
		if (buttonFocusListener == null) {
			buttonFocusListener = new FocusListener() {

				/*
				 * (non-Javadoc)
				 * 
				 * @see
				 * org.eclipse.swt.events.FocusListener#focusGained(org.eclipse
				 * .swt.events.FocusEvent)
				 */
				public void focusGained(FocusEvent e) {
					// Do nothing
				}

				/*
				 * (non-Javadoc)
				 * 
				 * @see
				 * org.eclipse.swt.events.FocusListener#focusLost(org.eclipse
				 * .swt.events.FocusEvent)
				 */
				public void focusLost(FocusEvent e) {
					DialogCellEditor.this.focusLost();
				}
			};
		}

		return buttonFocusListener;
	}

	/**
	 * Return a listener for unset button focus.
	 * 
	 * @return FocusListener
	 */
	private FocusListener getUnsetButtonFocusListener() {
		if (unsetButtonFocusListener == null) {
			unsetButtonFocusListener = new FocusListener() {

				/*
				 * (non-Javadoc)
				 * 
				 * @see
				 * org.eclipse.swt.events.FocusListener#focusGained(org.eclipse
				 * .swt.events.FocusEvent)
				 */
				public void focusGained(FocusEvent e) {
					// Do nothing
				}

				/*
				 * (non-Javadoc)
				 * 
				 * @see
				 * org.eclipse.swt.events.FocusListener#focusLost(org.eclipse
				 * .swt.events.FocusEvent)
				 */
				public void focusLost(FocusEvent e) {
					DialogCellEditor.this.focusLost();
				}
			};
		}

		return unsetButtonFocusListener;
	}

	/*
	 * (non-Javadoc) Method declared on CellEditor.
	 */
	protected void doSetValue(Object value) {
		this.value = value;
		updateContents(value);
	}

	/**
	 * Returns the default label widget created by <code>createContents</code>.
	 * 
	 * @return the default label widget
	 */
	protected Label getDefaultLabel() {
		return defaultLabel;
	}

	protected ILabelProvider getLabelProvider() {
		return MDTUtil.getLabelProvider();
	}

	/**
	 * Opens a dialog box under the given parent control and returns the
	 * dialog's value when it closes, or <code>null</code> if the dialog was
	 * canceled or no selection was made in the dialog.
	 * <p>
	 * This framework method must be implemented by concrete subclasses. It is
	 * called when the user has pressed the button and the dialog box must pop
	 * up.
	 * </p>
	 * 
	 * @param cellEditorWindow
	 *            the parent control cell editor's window so that a subclass can
	 *            adjust the dialog box accordingly
	 * @return the selected value, or <code>null</code> if the dialog was
	 *         canceled or no selection was made in the dialog
	 */
	protected Object openDialogBox(Control cellEditorWindow) {
		dialog = createDialog();
		if (dialog.open() == ChooseDialog.OK) {
			Object[] res = dialog.getResult();
			if (res.length == 1) {
				paths = dialog.getPaths();
				if (res[0] instanceof EObjectChooserComposite.NullObject) {
					return null;
				} else {
					return res[0];
				}
			}
		}
		return new EObjectChooserComposite.NullObject();
	}

	protected ChooseDialog createDialog() {
		Object[] objects = getChoices();
		ChooseDialog cd = new ChooseDialog(null, objects,
				isSingleObjectSelection(), getGroupedContentProvider(),
				getResourceContentProvider()) {
			@Override
			protected boolean checkSelectionValid(Object o) {
				return super.checkSelectionValid(o)
						&& DialogCellEditor.this.checkSelectionValid(o);
			}

			@Override
			public List<?> getAllowedTypesInThePath() {
				return DialogCellEditor.this.getAllowedTypesInThePath();
			}

			@Override
			protected List getInitialElementSelections() {
				List list = getInitialSelection();
				if (!list.isEmpty()) {
					return list;
				} else {
					return super.getInitialElementSelections();
				}
			}

			@Override
			protected boolean selectAfterRefreshTree() {
				return DialogCellEditor.this.selectAfterRefreshTree();
			}
		};
		cd.setLabelProvider(labelProvider);
		cd.setContentProvider(getTreeContentProvider());

		return cd;
	}

	protected boolean selectAfterRefreshTree() {
		return true;
	}

	public List<DataPath> getPaths() {
		return paths;
	}

	public DataPath getPathForElement(Object o) {
		for (DataPath dp : paths) {
			if (dp.getElement() == o) {
				return dp;
			}
		}

		return null;
	}

	protected ITreeContentProvider getTreeContentProvider() {
		return null;
	}

	protected boolean checkSelectionValid(Object o) {
		return true;
	}

	protected boolean isSingleObjectSelection() {
		return true;
	}

	protected List<?> getInitialSelection() {
		return Collections.emptyList();
	}

	protected List<?> getAllowedTypesInThePath() {
		return Collections.singletonList(Object.class);
	}

	protected ITreeContentProvider getGroupedContentProvider() {
		return null;
	}

	protected ITreeContentProvider getResourceContentProvider() {
		return null;
	}

	protected Object[] getChoices() {
		return null;
	}

	/**
	 * Updates the controls showing the value of this cell editor.
	 * <p>
	 * The default implementation of this framework method just converts the
	 * passed object to a string using <code>toString</code> and sets this as
	 * the text of the label widget.
	 * </p>
	 * <p>
	 * Subclasses may reimplement. If you reimplement this method, you should
	 * also reimplement <code>createContents</code>.
	 * </p>
	 * 
	 * @param value
	 *            the new value of this cell editor
	 */
	protected void updateContents(Object value) {
		if (defaultLabel != null && labelProvider != null) {
			defaultLabel.setText(labelProvider.getText(value));
		}
	}

}
