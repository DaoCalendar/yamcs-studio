/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.xygraph.figures;

import org.csstudio.swt.xygraph.linearscale.Range;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

/**
 * The grid in the plot area.
 * 
 * @author Xihui Chen
 *
 */
public class Grid extends Figure implements IAxisListener {

    private Axis axis;

    public Grid(Axis axis) {
        axis.addListener(this);
        this.axis = axis;
        axis.setGrid(this);

    }

    @Override
    protected void paintFigure(Graphics graphics) {
        super.paintFigure(graphics);
        graphics.pushState();
        if (axis.isShowMajorGrid()) {
            graphics.setLineStyle(axis.isDashGridLine() ? SWT.LINE_DASH : SWT.LINE_SOLID);
            graphics.setForegroundColor(axis.getMajorGridColor());
            graphics.setLineWidth(1);
            for (int pos : axis.getScaleTickLabels().getTickLabelPositions()) {
                if (axis.isHorizontal())
                    graphics.drawLine(axis.getBounds().x + pos, bounds.y + bounds.height,
                            axis.getBounds().x + pos, bounds.y);
                else
                    graphics.drawLine(bounds.x, axis.getBounds().y + axis.getBounds().height - pos,
                            bounds.x + bounds.width,
                            axis.getBounds().y + axis.getBounds().height - pos);
            }
        }
        graphics.popState();
    }

    @Override
    public void axisRevalidated(Axis axis) {
        if (axis.isShowMajorGrid())
            repaint();
    }

    @Override
    public void axisRangeChanged(Axis axis, Range old_range, Range new_range) {
        // do nothing
    }

    @Override
    public void axisForegroundColorChanged(Axis axis, Color oldColor,
            Color newColor) {
        // TODO Auto-generated method stub

    }

    @Override
    public void axisTitleChanged(Axis axis, String oldTitle, String newTitle) {
        // TODO Auto-generated method stub

    }

    @Override
    public void axisAutoScaleChanged(Axis axis, boolean oldAutoScale,
            boolean newAutoScale) {
        // TODO Auto-generated method stub

    }

    @Override
    public void axisLogScaleChanged(Axis axis, boolean old, boolean logScale) {
        // TODO Auto-generated method stub

    }

}
