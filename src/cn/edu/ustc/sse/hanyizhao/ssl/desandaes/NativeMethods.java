package cn.edu.ustc.sse.hanyizhao.ssl.desandaes;

/**
 * Created by HanYizhao on 2016/5/2.
 * 本地方法
 */
public class NativeMethods {
    /**
     * 判断文件是否加密
     *
     * @param canonicalPath 文件绝对路径(GBK编码)
     * @param length        字节数
     * @return -1 错误，0，未加密，1，DES，2，AES256
     */
    public static native int isFileEncrypted(byte[] canonicalPath, int length);

    /**
     * 加密解密文件
     *
     * @param filePath         源文件路径(GBK编码)
     * @param filePathLength   路径长度
     * @param targetPath       新文件路径
     * @param targetPathLength 路径长度
     * @param password         密码(GBK)
     * @param passwordLength   密码字节数
     * @param aes              1 AES， 0 DES
     * @param encrypt          1 加密 2 解密
     * @return NULL 成功，否则返回错误信息
     */
    public static native String encryptFile(byte[] filePath, int filePathLength,
                                            byte[] targetPath, int targetPathLength, byte[] password, int passwordLength, int aes, int encrypt);
}
