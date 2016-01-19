package es.cv.gvcase.mdt.common.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

import es.cv.gvcase.emf.ui.common.composites.EObjectChooserComposite;
import es.cv.gvcase.emf.ui.common.providers.TreeArrayContentProvider;
import es.cv.gvcase.mdt.common.util.MDTUtil;

public class GroupedContentProvider extends TreeArrayContentProvider {
	private Object[] elements;

	public GroupedContentProvider(Object[] elements) {
		this.elements = elements;
	}

	public void setElements(Object[] elements) {
		this.elements = elements;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		List<Object> list = new ArrayList<Object>();
		for (Object o : elements) {
			if (o == null) {
				list.add(new EObjectChooserComposite.NullObject());
				continue;
			}
			if (o instanceof EObject) {
				EObject eo = (EObject) o;
				if (!list.contains(eo.eContainer())) {
					list.add(eo.eContainer());
					continue;
				}
			}
		}

		Collections.sort(list, new SortElements());
		return list.toArray();
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof EObject) {
			List<EObject> list = new ArrayList<EObject>();
			for (EObject eo : ((EObject) parentElement).eContents()) {
				for (Object o : elements) {
					if (eo.equals(o)) {
						list.add(eo);
					}
				}
			}

			Collections.sort(list, new SortElements());
			return list.toArray();
		} else {
			return new Object[0];
		}
	}

	public boolean hasChildren(Object element) {
		if (element instanceof EObject) {
			for (EObject eo : ((EObject) element).eContents()) {
				for (Object o : elements) {
					if (eo.equals(o)) {
						return true;
					}
				}
			}
			return false;
		} else {
			return false;
		}
	}

	private class SortElements implements Comparator<Object> {
		public int compare(Object o1, Object o2) {
			String name1 = MDTUtil.getLabelProvider().getText(o1);
			String name2 = MDTUtil.getLabelProvider().getText(o2);

			if (name1.toLowerCase().compareTo(name2.toLowerCase()) < 0)
				return -1;
			else if (name1.toLowerCase().compareTo(name2.toLowerCase()) == 0)
				return 0;
			else
				return 1;
		}
	}
}
