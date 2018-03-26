package cn.edu.ustc.sse.hanyizhao.ssl.desandaes;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.InsetsUIResource;
import java.awt.*;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by HanYizhao on 2016/5/2.
 */
public class Main {

    public static ResourceBundle getStringResource() {
        if (rb == null) {
            rb = ResourceBundle.getBundle("cn.edu.ustc.sse.hanyizhao.ssl.desandaes.resource.string",
                    new XMLResourceBundleControl());
        }
        return rb;
    }

    private static ResourceBundle rb;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        if (Tools.shouldHandleDPI()) {
            for (Map.Entry<Object, Object> i : UIManager.getDefaults().entrySet()) {
                if (i.getKey() instanceof String) {
                    {
                        Object key = i.getKey();
                        Object value = UIManager.get(key);
                        if (value != null) {
                            if (!(value instanceof Color) && !(value instanceof Font) && !(((String) key).contains("Icon"))) {
                                if (value instanceof InsetsUIResource) {
                                    InsetsUIResource oldUi = (InsetsUIResource) value;
                                    InsetsUIResource newUi = new InsetsUIResource(Tools.HighResolution(oldUi.top),
                                            Tools.HighResolution(oldUi.left),
                                            Tools.HighResolution(oldUi.bottom),
                                            Tools.HighResolution(oldUi.right));
                                    UIManager.put(i.getKey(), newUi);
                                }
                            }
                        }
                    }
                    if (((String) i.getKey()).endsWith(".font")) {
                        Object value = UIManager.get(i.getKey());
                        if (value instanceof FontUIResource) {
                            FontUIResource fontValue = (FontUIResource) value;
                            if (fontValue.getSize() < 15) {
                                FontUIResource newFont = new FontUIResource(fontValue.getName(), fontValue.getStyle(), Tools.HighResolution(fontValue.getSize()));
                                UIManager.put(i.getKey(), newFont);
                            }
                        }
                    }
                }
            }
            UIDefaults defaults = UIManager.getDefaults();
            defaults.remove("SplitPane.border");
            defaults.remove("ScrollPane.border");
        }
        MainFrame frame = new MainFrame();
        frame.setVisible(true);
    }
}
