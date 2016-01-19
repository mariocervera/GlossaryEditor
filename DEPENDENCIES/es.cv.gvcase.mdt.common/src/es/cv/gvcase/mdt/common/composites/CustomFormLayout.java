/*******************************************************************************
 * Copyright (c) 2012 Conselleria de Infraestructuras y Transporte, Generalitat
 * de la Comunitat Valenciana. All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Marc Gil Sendra (Integranova) - initial API implementation
 *******************************************************************************/
package es.cv.gvcase.mdt.common.composites;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Scrollable;

/**
 * A Custom Form Layout that doesn't take into account controls that are
 * indicated as NOT visible
 * 
 * @author mgil
 */
public class CustomFormLayout extends Layout {
	/**
	 * marginWidth specifies the number of pixels of horizontal margin that will
	 * be placed along the left and right edges of the layout.
	 * 
	 * The default value is 0.
	 */
	public int marginWidth = 0;

	/**
	 * marginHeight specifies the number of pixels of vertical margin that will
	 * be placed along the top and bottom edges of the layout.
	 * 
	 * The default value is 0.
	 */
	public int marginHeight = 0;

	/**
	 * marginLeft specifies the number of pixels of horizontal margin that will
	 * be placed along the left edge of the layout.
	 * 
	 * The default value is 0.
	 * 
	 * @since 3.1
	 */
	public int marginLeft = 0;

	/**
	 * marginTop specifies the number of pixels of vertical margin that will be
	 * placed along the top edge of the layout.
	 * 
	 * The default value is 0.
	 * 
	 * @since 3.1
	 */
	public int marginTop = 0;

	/**
	 * marginRight specifies the number of pixels of horizontal margin that will
	 * be placed along the right edge of the layout.
	 * 
	 * The default value is 0.
	 * 
	 * @since 3.1
	 */
	public int marginRight = 0;

	/**
	 * marginBottom specifies the number of pixels of vertical margin that will
	 * be placed along the bottom edge of the layout.
	 * 
	 * The default value is 0.
	 * 
	 * @since 3.1
	 */
	public int marginBottom = 0;

	/**
	 * spacing specifies the number of pixels between the edge of one control
	 * and the edge of its neighbouring control.
	 * 
	 * The default value is 0.
	 * 
	 * @since 3.0
	 */
	public int spacing = 0;

	/**
	 * Constructs a new instance of this class.
	 */
	public CustomFormLayout() {
	}

	/*
	 * Computes the preferred height of the form with respect to the preferred
	 * height of the control.
	 * 
	 * Given that the equations for top (T) and bottom (B) of the control in
	 * terms of the height of the form (X) are: T = AX + B B = CX + D
	 * 
	 * The equation for the height of the control (H) is bottom (B) minus top
	 * (T) or (H = B - T) or:
	 * 
	 * H = (CX + D) - (AX + B)
	 * 
	 * Solving for (X), the height of the form, we get:
	 * 
	 * X = (H + B - D) / (C - A)
	 * 
	 * When (A = C), (C - A = 0) and the equation has no solution for X. This is
	 * a special case meaning that the control does not constrain the height of
	 * the form. In this case, we need to arbitrarily define the height of the
	 * form (X):
	 * 
	 * Case 1: A = C, A = 0, C = 0
	 * 
	 * Let X = D, the distance from the top of the form to the bottom edge of
	 * the control. In this case, the control was attached to the top of the
	 * form and the form needs to be large enough to show the bottom edge of the
	 * control.
	 * 
	 * Case 2: A = C, A = 1, C = 1
	 * 
	 * Let X = -B, the distance from the bottom of the form to the top edge of
	 * the control. In this case, the control was attached to the bottom of the
	 * form and the only way that the control would be visible is if the offset
	 * is negative. If the offset is positive, there is no possible height for
	 * the form that will show the control as it will always be below the bottom
	 * edge of the form.
	 * 
	 * Case 3: A = C, A != 0, C != 0 and A != 1, C != 0
	 * 
	 * Let X = D / (1 - C), the distance from the top of the form to the bottom
	 * edge of the control. In this case, since C is not 0 or 1, it must be a
	 * fraction, U / V. The offset D is the distance from CX to the bottom edge
	 * of the control. This represents a fraction of the form (1 - C)X. Since
	 * the height of a fraction of the form is known, the height of the entire
	 * form can be found by setting (1 - C)X = D. We solve this equation for X
	 * in terms of U and V, giving us X = (U * D) / (U - V). Similarly, if the
	 * offset D is negative, the control is positioned above CX. The offset -B
	 * is the distance from the top edge of the control to CX. We can find the
	 * height of the entire form by setting CX = -B. Solving in terms of U and V
	 * gives us X = (-B * V) / U.
	 */
	int computeHeight(Control control, CustomFormData data, boolean flushCache) {
		CustomFormAttachment top = data.getTopAttachment(control, spacing,
				flushCache);
		CustomFormAttachment bottom = data.getBottomAttachment(control,
				spacing, flushCache);
		CustomFormAttachment height = bottom.minus(top);
		if (height.numerator == 0) {
			if (bottom.numerator == 0)
				return bottom.offset;
			if (bottom.numerator == bottom.denominator)
				return -top.offset;
			if (bottom.offset <= 0) {
				return -top.offset * top.denominator / bottom.numerator;
			}
			int divider = bottom.denominator - bottom.numerator;
			return bottom.denominator * bottom.offset / divider;
		}
		return height.solveY(data.getHeight(control, flushCache));
	}

	protected Point computeSize(Composite composite, int wHint, int hHint,
			boolean flushCache) {
		Point size = layout(composite, false, 0, 0, wHint, hHint, flushCache);
		if (wHint != SWT.DEFAULT)
			size.x = wHint;
		if (hHint != SWT.DEFAULT)
			size.y = hHint;
		return size;
	}

	protected boolean flushCache(Control control) {
		Object data = control.getLayoutData();
		if (data != null)
			((CustomFormData) data).flushCache();
		return true;
	}

	String getName() {
		String string = getClass().getName();
		int index = string.lastIndexOf('.');
		if (index == -1)
			return string;
		return string.substring(index + 1, string.length());
	}

	/*
	 * Computes the preferred height of the form with respect to the preferred
	 * height of the control.
	 */
	int computeWidth(Control control, CustomFormData data, boolean flushCache) {
		CustomFormAttachment left = data.getLeftAttachment(control, spacing,
				flushCache);
		CustomFormAttachment right = data.getRightAttachment(control, spacing,
				flushCache);
		CustomFormAttachment width = right.minus(left);
		if (width.numerator == 0) {
			if (right.numerator == 0)
				return right.offset;
			if (right.numerator == right.denominator)
				return -left.offset;
			if (right.offset <= 0) {
				return -left.offset * left.denominator / left.numerator;
			}
			int divider = right.denominator - right.numerator;
			return right.denominator * right.offset / divider;
		}
		return width.solveY(data.getWidth(control, flushCache));
	}

	protected void layout(Composite composite, boolean flushCache) {
		Rectangle rect = composite.getClientArea();
		int x = rect.x + marginLeft + marginWidth;
		int y = rect.y + marginTop + marginHeight;
		int width = Math.max(0, rect.width - marginLeft - 2 * marginWidth
				- marginRight);
		int height = Math.max(0, rect.height - marginTop - 2 * marginHeight
				- marginBottom);
		layout(composite, true, x, y, width, height, flushCache);
	}

	Point layout(Composite composite, boolean move, int x, int y, int width,
			int height, boolean flushCache) {
		Control[] children = composite.getChildren();
		for (int i = 0; i < children.length; i++) {
			Control child = children[i];
			CustomFormData data = (CustomFormData) child.getLayoutData();
			if (data == null)
				child.setLayoutData(data = new CustomFormData());
			if (flushCache)
				data.flushCache();
			data.cacheLeft = data.cacheRight = data.cacheTop = data.cacheBottom = null;
		}
		boolean[] flush = null;
		Rectangle[] bounds = null;
		int w = 0, h = 0;
		for (int i = 0; i < children.length; i++) {
			Control child = children[i];
			if (child.isVisible()) {
				CustomFormData data = (CustomFormData) child.getLayoutData();
				if (width != SWT.DEFAULT) {
					data.needed = false;
					CustomFormAttachment left = data.getLeftAttachment(child,
							spacing, flushCache);
					CustomFormAttachment right = data.getRightAttachment(child,
							spacing, flushCache);
					int x1 = left.solveX(width), x2 = right.solveX(width);
					if (data.height == SWT.DEFAULT && !data.needed) {
						int trim = 0;
						// TEMPORARY CODE
						if (child instanceof Scrollable) {
							Rectangle rect = ((Scrollable) child).computeTrim(
									0, 0, 0, 0);
							trim = rect.width;
						} else {
							trim = child.getBorderWidth() * 2;
						}
						data.cacheWidth = data.cacheHeight = -1;
						int currentWidth = Math.max(0, x2 - x1 - trim);
						data.computeSize(child, currentWidth, data.height,
								flushCache);
						if (flush == null)
							flush = new boolean[children.length];
						flush[i] = true;
					}
					w = Math.max(x2, w);
					if (move) {
						if (bounds == null)
							bounds = new Rectangle[children.length];
						bounds[i] = new Rectangle(0, 0, 0, 0);
						bounds[i].x = x + x1;
						bounds[i].width = x2 - x1;
					}
				} else {
					w = Math.max(computeWidth(child, data, flushCache), w);
				}
			}
		}
		for (int i = 0; i < children.length; i++) {
			Control child = children[i];
			if (child.isVisible()) {
				CustomFormData data = (CustomFormData) child.getLayoutData();
				if (height != SWT.DEFAULT) {
					int y1 = data.getTopAttachment(child, spacing, flushCache)
							.solveX(height);
					int y2 = data.getBottomAttachment(child, spacing,
							flushCache).solveX(height);
					h = Math.max(y2, h);
					if (move) {
						bounds[i].y = y + y1;
						bounds[i].height = y2 - y1;
					}
				} else {
					h = Math.max(computeHeight(child, data, flushCache), h);
				}
			}
		}
		for (int i = 0; i < children.length; i++) {
			Control child = children[i];
			if (child.isVisible()) {
				CustomFormData data = (CustomFormData) child.getLayoutData();
				if (flush != null && flush[i])
					data.cacheWidth = data.cacheHeight = -1;
				data.cacheLeft = data.cacheRight = data.cacheTop = data.cacheBottom = null;
			}
		}
		if (move) {
			for (int i = 0; i < children.length; i++) {
				Control child = children[i];
				if (child.isVisible()) {
					child.setBounds(bounds[i]);
				}
			}
		}
		w += marginLeft + marginWidth * 2 + marginRight;
		h += marginTop + marginHeight * 2 + marginBottom;
		return new Point(w, h);
	}

	/**
	 * Returns a string containing a concise, human-readable description of the
	 * receiver.
	 * 
	 * @return a string representation of the layout
	 */
	public String toString() {
		String string = getName() + " {";
		if (marginWidth != 0)
			string += "marginWidth=" + marginWidth + " ";
		if (marginHeight != 0)
			string += "marginHeight=" + marginHeight + " ";
		if (marginLeft != 0)
			string += "marginLeft=" + marginLeft + " ";
		if (marginRight != 0)
			string += "marginRight=" + marginRight + " ";
		if (marginTop != 0)
			string += "marginTop=" + marginTop + " ";
		if (marginBottom != 0)
			string += "marginBottom=" + marginBottom + " ";
		if (spacing != 0)
			string += "spacing=" + spacing + " ";
		string = string.trim();
		string += "}";
		return string;
	}
}
