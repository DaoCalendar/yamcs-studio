package org.yamcs.studio.ui;

import org.eclipse.jface.viewers.OwnerDrawLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TableItem;

/**
 * A table column with SWT.CENTER and just an image, will not be centered on at least Windows
 * platforms. Work around this issue by custom drawing.
 *
 * @see https://bugs.eclipse.org/bugs/show_bug.cgi?id=26045
 */
public abstract class CenteredImageLabelProvider extends OwnerDrawLabelProvider {

    @Override
    public void measure(Event event, Object element) {
    }

    @Override
    public void paint(Event event, Object element) {
        Image img = getImage(element);
        if (img != null) {
            Rectangle bounds = ((TableItem) event.item).getBounds(event.index);
            Rectangle imgBounds = img.getBounds();

            bounds.width /= 2;
            bounds.width -= imgBounds.width / 2;
            bounds.height /= 2;
            bounds.height -= imgBounds.height / 2;

            int x = bounds.width > 0 ? bounds.x + bounds.width : bounds.x;
            int y = bounds.height > 0 ? bounds.y + bounds.height : bounds.y;
            event.gc.drawImage(img, x, y);
        }
    }

    @Override
    protected void erase(Event event, Object element) {
        // NOP. Prevent background-recoloring upon selection
    }

    public abstract Image getImage(Object element);
}
