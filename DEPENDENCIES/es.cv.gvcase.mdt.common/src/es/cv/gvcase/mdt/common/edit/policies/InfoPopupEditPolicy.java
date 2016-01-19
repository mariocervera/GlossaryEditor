/*******************************************************************************
 * Copyright (c) 2008 Conselleria de Infraestructuras y Transporte, Generalitat 
 * de la Comunitat Valenciana . All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Marc Gil Sendra (Prodevelop) – Initial implementation.
 *
 ******************************************************************************/
package es.cv.gvcase.mdt.common.edit.policies;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.DiagramAssistantEditPolicy;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;

public abstract class InfoPopupEditPolicy extends DiagramAssistantEditPolicy {

	public static String ID = "InfoPopupRole";

	private IFigure myBalloon = null;
	private Label label = null;

	@Override
	protected void hideDiagramAssistant() {
		if (getBalloon() != null) {
			teardownPopupBar();
		}
	}

	@Override
	protected boolean isDiagramAssistant(Object object) {
		return object instanceof RoundedRectangleWithTail;
	}

	@Override
	protected boolean isDiagramAssistantShowing() {
		return getBalloon() != null;
	}

	@Override
	protected void showDiagramAssistant(Point referencePoint) {
		if (getBalloon() != null && getBalloon().getParent() != null) {
			return;
		}

		initContent();

		// the feedback layer figures do not recieve mouse events so do not use
		// it for popup bars
		IFigure layer = getLayer(LayerConstants.HANDLE_LAYER);
		layer.add(getBalloon());

		if (referencePoint == null) {
			referencePoint = getHostFigure().getBounds().getCenter();
		}

		Point thePoint = getBalloonPosition(referencePoint);

		getBalloon().setLocation(thePoint);

		// dismiss the popup bar after a delay
		if (!shouldAvoidHidingDiagramAssistant()) {
			hideDiagramAssistantAfterDelay(getDisappearanceDelay());
		}
	}

	/**
	 * For editparts that consume the entire viewport, statechart, structure,
	 * communication, we want to display the popup bar at the mouse location.
	 * 
	 * @param referencePoint
	 *            The reference point which may be used to determine where the
	 *            diagram assistant should be located. This is most likely the
	 *            current mouse location.
	 * @return Point
	 */
	private Point getBalloonPosition(Point referencePoint) {
		int BALLOON_Y_OFFSET = 0;
		double myBallonOffsetPercent = 0.5;
		int ACTION_WIDTH_HGT = 25;

		Point thePoint = new Point();
		Dimension theoffset = new Dimension();
		Rectangle rcBounds = getHostFigure().getBounds().getCopy();

		getHostFigure().translateToAbsolute(rcBounds);
		getBalloon().translateToRelative(rcBounds);

		theoffset.height = -(BALLOON_Y_OFFSET + ACTION_WIDTH_HGT);
		theoffset.width = (int) (rcBounds.width * myBallonOffsetPercent);

		thePoint.x = rcBounds.x + theoffset.width;
		thePoint.y = rcBounds.y + theoffset.height;
		adjustToFitInViewport(thePoint);

		return thePoint;
	}

	/**
	 * Uses the balloon location passed in and its size to determine if the
	 * balloon will appear outside the viewport. If so, the balloon location
	 * will be modified accordingly.
	 * 
	 * @param balloonLocation
	 *            the suggested balloon location passed in and potentially
	 *            modified when this method completes
	 */
	private void adjustToFitInViewport(Point balloonLocation) {
		Control control = getHost().getViewer().getControl();
		if (control instanceof FigureCanvas) {
			Rectangle viewportRect = ((FigureCanvas) control).getViewport()
					.getClientArea();
			Rectangle balloonRect = new Rectangle(balloonLocation, getBalloon()
					.getSize());

			int yDiff = viewportRect.y - balloonRect.y;
			if (yDiff > 0) {
				// balloon is above the viewport, shift down
				balloonLocation.translate(0, yDiff);
			}
			int xDiff = balloonRect.right() - viewportRect.right();
			if (xDiff > 0) {
				// balloon is to the right of the viewport, shift left
				balloonLocation.translate(-xDiff, 0);
			}
		}
	}

	/**
	 * initialize the popup bars
	 */
	private void initContent() {
		myBalloon = new RoundedRectangleWithTail();
	}

	private void teardownPopupBar() {
		// the feedback layer figures do not recieve mouse events
		IFigure layer = getLayer(LayerConstants.HANDLE_LAYER);
		if (myBalloon.getParent() != null) {
			layer.remove(myBalloon);
		}
		myBalloon = null;
	}

	private class RoundedRectangleWithTail extends RoundedRectangle {

		private int myCornerDimension = 10;

		public RoundedRectangleWithTail() {
			this.setFill(true);
			this.setBackgroundColor(getRectangleBackgroundColor());
			this.setForegroundColor(getRectangleForegroundColor());
			this.setVisible(true);
			this.setEnabled(true);
			this.setOpaque(true);

			this.setSize(1, 1);

			label = new Label();
			label.setForegroundColor(getLabelForegroundColor());
			label.setText(getLabelText());

			this.setLayoutManager(new GridLayout());
		}

		@Override
		public void paintFigure(Graphics graphics) {
			Rectangle theBounds = this.getBounds().getCopy();
			graphics.setLineWidth(2);
			graphics.setForegroundColor(ColorConstants.black);
			graphics.setAlpha(getAlphaChannel());
			
			GridData gd = new GridData();
			gd.verticalAlignment = GridData.CENTER;
			gd.horizontalAlignment = GridData.CENTER;
			gd.horizontalIndent = 0;
			gd.horizontalSpan = 1;
			gd.verticalSpan = 1;
			gd.grabExcessHorizontalSpace = true;
			gd.grabExcessVerticalSpace = true;

			this.add(label, gd);

			int widthFont = graphics.getFontMetrics().getAverageCharWidth();
			int heightFont = graphics.getFontMetrics().getHeight();

			int maxWidth = 0;
			String[] splits = label.getText().split("\n");
			for (int i = 0; i < splits.length; i++) {
				String s = splits[i];
				int lengthSplit = s.length() * widthFont;
				if (lengthSplit > maxWidth)
					maxWidth = lengthSplit;
			}

			int maxHeight = heightFont * splits.length + 20;
			maxWidth += 20;
			theBounds.height = maxHeight;
			theBounds.width = maxWidth;
			this.setBounds(theBounds);

			graphics.fillRoundRectangle(theBounds, myCornerDimension,
					myCornerDimension);
			graphics.drawRoundRectangle(theBounds, myCornerDimension,
					myCornerDimension);
		}
	}

	protected IFigure getBalloon() {
		return myBalloon;
	}

	protected Color getRectangleBackgroundColor() {
		return ColorConstants.white;
	}

	protected Color getRectangleForegroundColor() {
		return ColorConstants.black;
	}
	
	protected Color getLabelForegroundColor() {
		return ColorConstants.black;
	}
	
	protected int getAlphaChannel() {
		return 0;
	}

	protected abstract String getLabelText();
}
