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
import org.eclipse.swt.widgets.Control;

/**
 * A Custom Form Data to be used with Custom Form Layout
 * 
 * @author mgil
 */
public class CustomFormData {
	/**
	 * width specifies the preferred width in pixels. This value is the wHint
	 * passed into Control.computeSize(int, int, boolean) to determine the
	 * preferred size of the control.
	 * 
	 * The default value is SWT.DEFAULT.
	 * 
	 * @see Control#computeSize(int, int, boolean)
	 */
	public int width = SWT.DEFAULT;
	/**
	 * height specifies the preferred height in pixels. This value is the hHint
	 * passed into Control.computeSize(int, int, boolean) to determine the
	 * preferred size of the control.
	 * 
	 * The default value is SWT.DEFAULT.
	 * 
	 * @see Control#computeSize(int, int, boolean)
	 */
	public int height = SWT.DEFAULT;
	/**
	 * left specifies the attachment of the left side of the control.
	 */
	public CustomFormAttachment left;
	/**
	 * right specifies the attachment of the right side of the control.
	 */
	public CustomFormAttachment right;
	/**
	 * top specifies the attachment of the top of the control.
	 */
	public CustomFormAttachment top;
	/**
	 * bottom specifies the attachment of the bottom of the control.
	 */
	public CustomFormAttachment bottom;

	int cacheWidth = -1, cacheHeight = -1;
	int defaultWhint, defaultHhint, defaultWidth = -1, defaultHeight = -1;
	int currentWhint, currentHhint, currentWidth = -1, currentHeight = -1;
	CustomFormAttachment cacheLeft, cacheRight, cacheTop, cacheBottom;
	boolean isVisited, needed;

	/**
	 * Constructs a new instance of FormData using default values.
	 */
	public CustomFormData() {
	}

	/**
	 * Constructs a new instance of FormData according to the parameters. A
	 * value of SWT.DEFAULT indicates that no minimum width or no minimum height
	 * is specified.
	 * 
	 * @param width
	 *            a minimum width for the control
	 * @param height
	 *            a minimum height for the control
	 */
	public CustomFormData(int width, int height) {
		this.width = width;
		this.height = height;
	}

	void computeSize(Control control, int wHint, int hHint, boolean flushCache) {
		if (cacheWidth != -1 && cacheHeight != -1)
			return;
		if (wHint == this.width && hHint == this.height) {
			if (defaultWidth == -1 || defaultHeight == -1
					|| wHint != defaultWhint || hHint != defaultHhint) {
				Point size = control.computeSize(wHint, hHint, flushCache);
				defaultWhint = wHint;
				defaultHhint = hHint;
				defaultWidth = size.x;
				defaultHeight = size.y;
			}
			cacheWidth = defaultWidth;
			cacheHeight = defaultHeight;
			return;
		}
		if (currentWidth == -1 || currentHeight == -1 || wHint != currentWhint
				|| hHint != currentHhint) {
			Point size = control.computeSize(wHint, hHint, flushCache);
			currentWhint = wHint;
			currentHhint = hHint;
			currentWidth = size.x;
			currentHeight = size.y;
		}
		cacheWidth = currentWidth;
		cacheHeight = currentHeight;
	}

	void flushCache() {
		cacheWidth = cacheHeight = -1;
		defaultHeight = defaultWidth = -1;
		currentHeight = currentWidth = -1;
	}

	int getWidth(Control control, boolean flushCache) {
		needed = true;
		computeSize(control, width, height, flushCache);
		return cacheWidth;
	}

	int getHeight(Control control, boolean flushCache) {
		computeSize(control, width, height, flushCache);
		return cacheHeight;
	}

	CustomFormAttachment getBottomAttachment(Control control, int spacing,
			boolean flushCache) {
		if (cacheBottom != null)
			return cacheBottom;
		if (isVisited)
			return cacheBottom = new CustomFormAttachment(0, getHeight(control,
					flushCache));
		if (bottom == null) {
			if (top == null)
				return cacheBottom = new CustomFormAttachment(0, getHeight(
						control, flushCache));
			return cacheBottom = getTopAttachment(control, spacing, flushCache)
					.plus(getHeight(control, flushCache));
		}
		Control bottomControl = bottom.control;
		if (bottomControl != null) {
			if (bottomControl.isDisposed()) {
				bottom.control = bottomControl = null;
			} else {
				if (bottomControl.getParent() != control.getParent()) {
					bottomControl = null;
				}
			}
		}
		if (bottomControl == null)
			return cacheBottom = bottom;
		isVisited = true;
		CustomFormData bottomData = (CustomFormData) bottomControl
				.getLayoutData();
		CustomFormAttachment bottomAttachment = bottomData.getBottomAttachment(
				bottomControl, spacing, flushCache);
		switch (bottom.alignment) {
		case SWT.BOTTOM:
			cacheBottom = bottomAttachment.plus(bottom.offset);
			break;
		case SWT.CENTER: {
			CustomFormAttachment topAttachment = bottomData.getTopAttachment(
					bottomControl, spacing, flushCache);
			CustomFormAttachment bottomHeight = bottomAttachment
					.minus(topAttachment);
			cacheBottom = bottomAttachment.minus(bottomHeight.minus(
					getHeight(control, flushCache)).divide(2));
			break;
		}
		default: {
			CustomFormAttachment topAttachment = bottomData.getTopAttachment(
					bottomControl, spacing, flushCache);
			cacheBottom = topAttachment.plus(bottom.offset - spacing);
			break;
		}
		}
		isVisited = false;
		return cacheBottom;
	}

	CustomFormAttachment getLeftAttachment(Control control, int spacing,
			boolean flushCache) {
		if (cacheLeft != null)
			return cacheLeft;
		if (isVisited)
			return cacheLeft = new CustomFormAttachment(0, 0);
		if (left == null) {
			if (right == null)
				return cacheLeft = new CustomFormAttachment(0, 0);
			return cacheLeft = getRightAttachment(control, spacing, flushCache)
					.minus(getWidth(control, flushCache));
		}
		Control leftControl = left.control;
		if (leftControl != null) {
			if (leftControl.isDisposed()) {
				left.control = leftControl = null;
			} else {
				if (leftControl.getParent() != control.getParent()) {
					leftControl = null;
				}
			}
		}
		if (leftControl == null)
			return cacheLeft = left;
		isVisited = true;
		CustomFormData leftData = (CustomFormData) leftControl.getLayoutData();
		CustomFormAttachment leftAttachment = leftData.getLeftAttachment(
				leftControl, spacing, flushCache);
		switch (left.alignment) {
		case SWT.LEFT:
			cacheLeft = leftAttachment.plus(left.offset);
			break;
		case SWT.CENTER: {
			CustomFormAttachment rightAttachment = leftData.getRightAttachment(
					leftControl, spacing, flushCache);
			CustomFormAttachment leftWidth = rightAttachment
					.minus(leftAttachment);
			cacheLeft = leftAttachment.plus(leftWidth.minus(
					getWidth(control, flushCache)).divide(2));
			break;
		}
		default: {
			CustomFormAttachment rightAttachment = leftData.getRightAttachment(
					leftControl, spacing, flushCache);
			cacheLeft = rightAttachment.plus(left.offset + spacing);
		}
		}
		isVisited = false;
		return cacheLeft;
	}

	String getName() {
		String string = getClass().getName();
		int index = string.lastIndexOf('.');
		if (index == -1)
			return string;
		return string.substring(index + 1, string.length());
	}

	CustomFormAttachment getRightAttachment(Control control, int spacing,
			boolean flushCache) {
		if (cacheRight != null)
			return cacheRight;
		if (isVisited)
			return cacheRight = new CustomFormAttachment(0, getWidth(control,
					flushCache));
		if (right == null) {
			if (left == null)
				return cacheRight = new CustomFormAttachment(0, getWidth(
						control, flushCache));
			return cacheRight = getLeftAttachment(control, spacing, flushCache)
					.plus(getWidth(control, flushCache));
		}
		Control rightControl = right.control;
		if (rightControl != null) {
			if (rightControl.isDisposed()) {
				right.control = rightControl = null;
			} else {
				if (rightControl.getParent() != control.getParent()) {
					rightControl = null;
				}
			}
		}
		if (rightControl == null)
			return cacheRight = right;
		isVisited = true;
		CustomFormData rightData = (CustomFormData) rightControl
				.getLayoutData();
		CustomFormAttachment rightAttachment = rightData.getRightAttachment(
				rightControl, spacing, flushCache);
		switch (right.alignment) {
		case SWT.RIGHT:
			cacheRight = rightAttachment.plus(right.offset);
			break;
		case SWT.CENTER: {
			CustomFormAttachment leftAttachment = rightData.getLeftAttachment(
					rightControl, spacing, flushCache);
			CustomFormAttachment rightWidth = rightAttachment
					.minus(leftAttachment);
			cacheRight = rightAttachment.minus(rightWidth.minus(
					getWidth(control, flushCache)).divide(2));
			break;
		}
		default: {
			CustomFormAttachment leftAttachment = rightData.getLeftAttachment(
					rightControl, spacing, flushCache);
			cacheRight = leftAttachment.plus(right.offset - spacing);
			break;
		}
		}
		isVisited = false;
		return cacheRight;
	}

	CustomFormAttachment getTopAttachment(Control control, int spacing,
			boolean flushCache) {
		if (cacheTop != null)
			return cacheTop;
		if (isVisited)
			return cacheTop = new CustomFormAttachment(0, 0);
		if (top == null) {
			if (bottom == null)
				return cacheTop = new CustomFormAttachment(0, 0);
			return cacheTop = getBottomAttachment(control, spacing, flushCache)
					.minus(getHeight(control, flushCache));
		}
		Control topControl = top.control;
		if (topControl != null) {
			if (topControl.isDisposed()) {
				top.control = topControl = null;
			} else {
				if (topControl.getParent() != control.getParent()) {
					topControl = null;
				}
			}
		}
		if (topControl == null)
			return cacheTop = top;
		isVisited = true;
		CustomFormData topData = (CustomFormData) topControl.getLayoutData();
		CustomFormAttachment topAttachment = topData.getTopAttachment(
				topControl, spacing, flushCache);
		switch (top.alignment) {
		case SWT.TOP:
			cacheTop = topAttachment.plus(top.offset);
			break;
		case SWT.CENTER: {
			CustomFormAttachment bottomAttachment = topData
					.getBottomAttachment(topControl, spacing, flushCache);
			CustomFormAttachment topHeight = bottomAttachment
					.minus(topAttachment);
			cacheTop = topAttachment.plus(topHeight.minus(
					getHeight(control, flushCache)).divide(2));
			break;
		}
		default: {
			CustomFormAttachment bottomAttachment = topData
					.getBottomAttachment(topControl, spacing, flushCache);
			cacheTop = bottomAttachment.plus(top.offset + spacing);
			break;
		}
		}
		isVisited = false;
		return cacheTop;
	}

	/**
	 * Returns a string containing a concise, human-readable description of the
	 * receiver.
	 * 
	 * @return a string representation of the FormData object
	 */
	public String toString() {
		String string = getName() + " {";
		if (width != SWT.DEFAULT)
			string += "width=" + width + " ";
		if (height != SWT.DEFAULT)
			string += "height=" + height + " ";
		if (left != null)
			string += "left=" + left + " ";
		if (right != null)
			string += "right=" + right + " ";
		if (top != null)
			string += "top=" + top + " ";
		if (bottom != null)
			string += "bottom=" + bottom + " ";
		string = string.trim();
		string += "}";
		return string;
	}
}
