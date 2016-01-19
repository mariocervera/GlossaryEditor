package es.cv.gvcase.fefem.common.databinding;

import java.util.Date;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.nebula.widgets.datechooser.DateChooserCombo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * Thanks to Pascal Leclerq See
 * <link>http://www.instantiations.com/forum/viewtopic.php?f=1&t=2096</link>
 * 
 * Contributions from gvCASE project (Jose Manuel García-Valladolid CIT/Indra
 * SL) - Added updateListener
 */
public class DateChooserComboObservableValue extends AbstractObservableValue
		implements IObservableValue {

	/**
	 * The Control being observed here.
	 */
	protected final DateChooserCombo dateChooserCombo;

	/**
	 * The "old" selection before a selection event is fired.
	 */
	protected Date currentSelection;

	/**
	 * Old value
	 */
	private Date oldValue;

	/**
	 * Observe the selection property of the provided CDateTime control.
	 * 
	 * @param dateChooserCombo
	 *            the control to observe
	 */
	public DateChooserComboObservableValue(DateChooserCombo dateChooserCombo) {
		super(SWTObservables.getRealm(dateChooserCombo.getDisplay()));
		this.dateChooserCombo = dateChooserCombo;
		currentSelection = dateChooserCombo.getValue();

		dateChooserCombo.addDisposeListener(disposeListener);
		dateChooserCombo.addListener(SWT.Modify, updateListener);
	}

	private DisposeListener disposeListener = new DisposeListener() {
		public void widgetDisposed(DisposeEvent e) {
			DateChooserComboObservableValue.this.dispose();
		}
	};

	private Listener updateListener = new Listener() {
		public void handleEvent(Event event) {
			Date newValue = dateChooserCombo.getValue();

			if (newValue == null || !newValue.equals(oldValue)) {
				fireValueChange(Diffs.createValueDiff(oldValue, newValue));
				oldValue = newValue;
			}
		}
	};

	public Object getValueType() {
		return Date.class;
	}

	protected Object doGetValue() {
		return dateChooserCombo.getValue();
	}

	protected void doSetValue(Object value) {
		oldValue = (Date) value;
		dateChooserCombo.setValue(oldValue);
		currentSelection = oldValue;
	}

}
