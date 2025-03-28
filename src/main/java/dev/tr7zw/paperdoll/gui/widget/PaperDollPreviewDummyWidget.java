package dev.tr7zw.paperdoll.gui.widget;

import dev.tr7zw.paperdoll.PaperDollShared;
import dev.tr7zw.trender.gui.client.RenderContext;
import dev.tr7zw.trender.gui.widget.WWidget;

public class PaperDollPreviewDummyWidget extends WWidget {

    public PaperDollPreviewDummyWidget() {
        super();
        setSize(0, 0);
    }

    @Override
    public void paint(RenderContext context, int x, int y, int mouseX, int mouseY) {
        PaperDollShared.instance.renderer.render(0);
    }

}
