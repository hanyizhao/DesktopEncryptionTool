package cn.edu.ustc.sse.hanyizhao.ssl.desandaes;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * Created by HanYizhao on 2016/5/3.
 */
public class Tools {

    public static void moveToCenter(Window window, boolean force) {
        if (force) {
            Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
            window.setLocation((d.width - window.getWidth()) / 2,
                    (d.height - window.getHeight()) / 2);
        } else {
            window.setLocationByPlatform(true);
        }
    }

    public static void setIcon(URL file, AbstractButton com) {
        ImageIcon ico = new ImageIcon(file);
        FontMetrics fontMetrics = com.getFontMetrics(com.getFont());
        Image temp = ico.getImage().getScaledInstance(fontMetrics.getHeight(), fontMetrics.getHeight(), Image.SCALE_SMOOTH);
        ico = new ImageIcon(temp);
        com.setIcon(ico);
    }

    public static boolean shouldHandleDPI() {
        boolean result = false;
        String version = System.getProperty("java.version");
        if (version != null) {
            int i = version.indexOf(".");
            if (i != -1) {
                version = version.substring(0, i);
            }
            try {
                result = Integer.parseInt(version) < 9;
            } catch (Exception ignored) {
            }

        }
        return result;
    }

    public static int HighResolution(int original) {
        if (shouldHandleDPI()) {
            double a = Toolkit.getDefaultToolkit().getScreenSize().getWidth() * original / 1366;
            return (int) Math.round(a);
        } else {
            return original;
        }
    }
}
