package plugins.perrine.easyclemv0.ec_clem.roi;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.Color;

@Singleton
public class ColorPicker {

    @Inject
    public ColorPicker() {}

    private Color[] colors = new Color[] {
            Color.BLUE,
            Color.CYAN,
            Color.GRAY,
            Color.GREEN,
            Color.MAGENTA,
            Color.ORANGE,
            Color.PINK,
            Color.RED,
            Color.YELLOW
    };

    private int nextColor = 0;

    public synchronized Color get() {
        Color color = colors[nextColor];
        nextColor = (nextColor + 1) % colors.length;
        return color;
    }
}
