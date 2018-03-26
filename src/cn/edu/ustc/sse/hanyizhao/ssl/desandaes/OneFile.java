package cn.edu.ustc.sse.hanyizhao.ssl.desandaes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by HanYizhao on 2016/5/2.
 */
public class OneFile {
    public String canonicalPath;
    public String fileName;
    public String filePath;
    public Status status;

    public OneFile(File f) {
        try {
            this.canonicalPath = f.getCanonicalPath();
        } catch (IOException e1) {
            e1.printStackTrace();
            this.canonicalPath = f.getAbsolutePath();
        }
        this.fileName = f.getName();
        this.filePath = f.getParent();
        this.status = MyNativeMethods.isFileEncrypted(this.canonicalPath);

    }

    public static boolean hasFile(File file, List<OneFile> list) {
        boolean result = false;
        Path newPath = file.toPath();
        for (OneFile f : list) {
            try {
                if (Files.isSameFile(newPath, Paths.get(f.canonicalPath))) {
                    result = true;
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public enum Status {
        NORMAL, AES, DES, PROCESSING, ERROR;

        @Override
        public String toString() {
            ResourceBundle rb = Main.getStringResource();
            String result = "XXX";
            switch (this) {
                case AES:
                    result = rb.getString("already_encrypted_aes");
                    break;
                case DES:
                    result = rb.getString("already_encrypted_des");
                    break;
                case NORMAL:
                    result = rb.getString("unencrypted");
                    break;
                case PROCESSING:
                    result = rb.getString("processing...");
                    break;
                case ERROR:
                    result = rb.getString("error");
                    break;
            }
            return result;
        }
    }
}
