package es.cv.gvcase.fefem.common.composites;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import es.cv.gvcase.emf.ui.common.dialogs.SelectMultipleValuesDialog;
import es.cv.gvcase.fefem.common.FEFEMPage;
import es.cv.gvcase.fefem.common.providers.EObjectLabelProvider;

public abstract class EMFPropertyMultiValuedComposite extends
		EMFPropertyComposite {

	protected Text text;
	protected Button button;

	public EMFPropertyMultiValuedComposite(Composite parent, int style,
			FormToolkit toolkit, EObject eObject, FEFEMPage page) {
		super(parent, style, toolkit, eObject, page);
	}

	public EMFPropertyMultiValuedComposite(Composite parent, int style,
			FormToolkit toolkit, Viewer viewer, FEFEMPage page) {
		super(parent, style, toolkit, viewer, page);
	}

	@Override
	protected void createWidgets(FormToolkit toolkit) {
		this.setLayout(new GridLayout(3, false));

		createLabel(toolkit);
		text = toolkit.createText(this, "", SWT.BORDER | SWT.READ_ONLY
				| SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		text.setLayoutData(gd);

		button = toolkit.createButton(this, "...", SWT.PUSH);

		hookListeners();

		toolkit.adapt(this);
	}

	protected void hookListeners() {
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SelectMultipleValuesDialog dialog = new SelectMultipleValuesDialog(
						getEObject(), getFeature(), getChoices(),
						getLabelProvider(), getLabelText());
				dialog.open();
				if (dialog.getReturnCode() == SelectMultipleValuesDialog.OK) {
					List<Object> list = dialog.getResult();
					text.setData(list);
					text.setText(getParseResult(list));
				}
			}
		});
	}

	public String getParseResult(List<Object> list) {
		String value = "";
		for (Iterator<Object> it = list.iterator(); it.hasNext();) {
			Object next = it.next();
			value += getLabelProvider().getText(next);
			if (it.hasNext()) {
				value += getSeparator();
			}
		}
		return value;
	}

	public ILabelProvider getLabelProvider() {
		return new EObjectLabelProvider();
	}

	@Override
	public Control getRepresentativeControl() {
		return text;
	}

	@Override
	protected IObservableValue getTargetObservable() {
		return SWTObservables.observeText(text, SWT.Modify);
	}

	@Override
	protected UpdateValueStrategy getTargetToModelUpdateValueStrategy() {
		return new UpdateValueStrategy() {
			public Object convert(Object value) {
				return text.getData();
			}
		};
	}

	@Override
	protected UpdateValueStrategy getModelToTargetUpdateValueStrategy() {
		return new UpdateValueStrategy() {
			@SuppressWarnings("unchecked")
			public Object convert(Object value) {
				String text = "";
				if (value instanceof List<?>) {
					List<Object> list = (List<Object>) value;
					text = getParseResult(list);
				} else {
					List<Object> list = new ArrayList<Object>();
					list.add(value);
					text = getParseResult(list);
				}
				return text;
			}
		};
	}

	@Override
	protected List<Control> getWidgetsList() {
		List<Control> list = new ArrayList<Control>();
		list.add(text);
		list.add(button);
		return list;
	}

	public Text getText() {
		return text;
	}

	public Button getButton() {
		return button;
	}

	protected String getSeparator() {
		return ", ";
	}

	protected abstract List<?> getChoices();
}
