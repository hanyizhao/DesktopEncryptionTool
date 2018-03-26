package cn.edu.ustc.sse.hanyizhao.ssl.desandaes;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.ResourceBundle;

/**
 * 与{@link NativeMethods}中的方法相对应的Java实现。
 * <p>
 * {@link NativeMethods}中的方法为使用了SSL的C++实现。
 * </p>
 */
public class MyNativeMethods {
    private static int TAG_LENGTH = 32;
    private static int SALT_LENGTH = 16;
    private static int KEY_LENGTH = 32;
    private static int ID_LENGTH = 32;
    private static int FILE_BUFFER_SIZE = 4194304;

    private static byte[] tag_DES = new byte[]{(byte) 0xE7, (byte) 0xD4, (byte) 0x0E, (byte) 0xBE, (byte) 0x7A, (byte) 0xE5, (byte) 0x3F, (byte) 0xAB, (byte) 0x51, (byte) 0x44, (byte) 0x13, (byte) 0xEA, (byte) 0xD3, (byte) 0x22, (byte) 0x97, (byte) 0x47, (byte) 0xBA, (byte) 0xC7, (byte) 0x02, (byte) 0x75, (byte) 0xF1, (byte) 0x30, (byte) 0x23, (byte) 0x17, (byte) 0xCE, (byte) 0xBE, (byte) 0x25, (byte) 0x54, (byte) 0x1B, (byte) 0xB1, (byte) 0x3C, (byte) 0x97};
    private static byte[] tag_AES = new byte[]{(byte) 0xBA, (byte) 0x53, (byte) 0x89, (byte) 0x44, (byte) 0x6E, (byte) 0xD9, (byte) 0xA8, (byte) 0x11, (byte) 0x49, (byte) 0xDD, (byte) 0xDF, (byte) 0xC4, (byte) 0xA6, (byte) 0x5D, (byte) 0x47, (byte) 0xF7, (byte) 0x69, (byte) 0xBA, (byte) 0x74, (byte) 0x49, (byte) 0x7D, (byte) 0xF4, (byte) 0xBC, (byte) 0x47, (byte) 0x5A, (byte) 0x77, (byte) 0x7B, (byte) 0xB2, (byte) 0x05, (byte) 0xB3, (byte) 0x32, (byte) 0x30};
    private static byte[] salt = new byte[]{(byte) 0x3E, (byte) 0xE6, (byte) 0xAE, (byte) 0x4F, (byte) 0x4E, (byte) 0xCD, (byte) 0x20, (byte) 0x6E, (byte) 0x87, (byte) 0x66, (byte) 0x2E, (byte) 0xA3, (byte) 0x14, (byte) 0xDE, (byte) 0x78, (byte) 0x98};

    private enum CipherType {
        DES, AES
    }

    /**
     * 判断文件是否加密
     *
     * @param canonicalPath 文件路径
     * @return -1 错误，0，未加密，1，DES，2，AES256
     */
    public static OneFile.Status isFileEncrypted(String canonicalPath) {
        OneFile.Status result = OneFile.Status.ERROR;
        RandomAccessFile rf = null;
        byte[] buffer = new byte[TAG_LENGTH];
        try {
            rf = new RandomAccessFile(canonicalPath, "r");
            long length = rf.length();
            if (length > (TAG_LENGTH + ID_LENGTH)) {
                rf.seek(0);
                rf.readFully(buffer);
                if (Arrays.equals(buffer, tag_DES)) {
                    result = OneFile.Status.DES;
                } else if (Arrays.equals(buffer, tag_AES)) {
                    result = OneFile.Status.AES;
                } else {
                    result = OneFile.Status.NORMAL;
                }
            } else {
                result = OneFile.Status.NORMAL;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (rf != null) {
                try {
                    rf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * 初始化 key iv id。
     * 通过password生成32字节key，16字节iv，32字节id。
     *
     * @param password 密码原文
     * @param key      密码与SALT链接进行SHA256散列，结果再次散列，为key
     * @param iv       key的后16字节的倒序为iv
     * @param id       key与SALT链接，进行SHA256散列，结果再次散列，为id
     */
    private static void initKeyIVID(byte[] password, byte[] key, byte[] iv, byte[] id) throws NoSuchAlgorithmException {

        byte[] temp;
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        //计算key
        md.update(password);
        md.update(salt);
        temp = md.digest();
        md.reset();
        temp = md.digest(temp);
        System.arraycopy(temp, 0, key, 0, KEY_LENGTH);
        //计算iv
        for (int i = 31; i > 15; i--) {
            iv[31 - i] = key[i];
        }
        //计算id
        md.reset();
        md.update(temp);
        md.update(salt);
        temp = md.digest();
        md.reset();
        temp = md.digest(temp);
        System.arraycopy(temp, 0, id, 0, ID_LENGTH);
    }

    /**
     * 加密解密文件
     *
     * @param filePath   源文件路径()
     * @param targetPath 新文件路径
     * @param password   密码(GBK? 有必要吗？)
     * @param isAES      1 AES， 0 DES (只有加密的时候才有效)
     * @param isEncrypt  1 加密 2 解密
     * @return NULL 成功，否则返回错误信息
     */
    public static String encryptFile(String filePath, String targetPath, byte[] password, boolean isAES, boolean isEncrypt) {
        byte[] key = new byte[KEY_LENGTH];
        byte[] iv = new byte[16];
        byte[] id = new byte[ID_LENGTH];
        try {
            initKeyIVID(password, key, iv, id);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            // "没有SHA-256散列算法"
            return e.getMessage();
        }
        CipherType type = isAES ? CipherType.AES : CipherType.DES;
        try {
            if (isEncrypt) {
                doEncryptFile(filePath, targetPath, type, key, iv, id);
            } else {
                doDecryptFile(filePath, targetPath, key, iv, id);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return null;
    }

    /**
     * 加密文件
     *
     * @param filePath   源文件路径
     * @param targetPath 新文件路径
     * @param type       加密算法
     * @param key        密钥
     * @param iv         IV
     * @param id         用于标识密码的ID
     * @throws Exception 任何异常
     */
    private static void doEncryptFile(String filePath, String targetPath, CipherType type, byte[] key, byte[] iv, byte[] id) throws Exception {
        ResourceBundle rb = Main.getStringResource();
        if (filePath.equals(targetPath)) {
            throw new Exception(rb.getString("error:target_file_and_source_file_are_the_same"));
        }
        RandomAccessFile rm = null;
        FileOutputStream bw = null;
        try {
            rm = new RandomAccessFile(filePath, "r");
            long length = rm.length();
            //有可能已经加密，进行验证
            if (length > TAG_LENGTH + ID_LENGTH) {
                byte[] temp = new byte[TAG_LENGTH];
                rm.readFully(temp);
                if (Arrays.equals(temp, tag_AES) || Arrays.equals(temp, tag_DES)) {
                    throw new Exception(rb.getString("error:file_has_been_encrypted_do_not_encrypt_again"));
                }
            }
            //处理新文件
            bw = new FileOutputStream(targetPath, false);
            //写入标记与ID
            bw.write(type == CipherType.DES ? tag_DES : tag_AES);
            bw.write(id);
            //开始加密
            rm.seek(0);
            //基础读写次数
            long times = length / FILE_BUFFER_SIZE;
            Cipher cipher = Cipher.getInstance(type == CipherType.DES ? "DES/CBC/PKCS5Padding" : "AES/CBC/PKCS5Padding");
            if (type == CipherType.DES) {
                byte[] temp = new byte[8];
                System.arraycopy(iv, 0, temp, 0, 8);
                iv = temp;
                byte[] temp2 = new byte[8];
                System.arraycopy(key, 0, temp, 0, 8);
                key = temp2;
            }
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, type == CipherType.DES ? "DES" : "AES"), new IvParameterSpec(iv));
            byte[] temp = new byte[FILE_BUFFER_SIZE];
            for (long i = 0; i < times; i++) {
                rm.readFully(temp);
                bw.write(cipher.update(temp));
            }
            //判断是否有剩余
            int tempLength = (int) (length - times * FILE_BUFFER_SIZE);
            if (tempLength > 0) {
                byte[] newTemp = new byte[tempLength];
                rm.readFully(newTemp);
                bw.write(cipher.update(newTemp));
            }
            //完成加密的最后步骤
            bw.write(cipher.doFinal());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (rm != null) {
                try {
                    rm.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 解密文件
     *
     * @param filePath   源文件路径
     * @param targetPath 新文件路径
     * @param key        密钥
     * @param iv         IV
     * @param id         用于标识密码的ID
     * @throws Exception 任何异常
     */
    private static void doDecryptFile(String filePath, String targetPath, byte[] key, byte[] iv, byte[] id) throws Exception {
        ResourceBundle rb = Main.getStringResource();
        if (filePath.equals(targetPath)) {
            throw new Exception(rb.getString("error:target_file_and_source_file_are_the_same"));
        }
        RandomAccessFile rm = null;
        FileOutputStream bw = null;
        CipherType type;
        try {
            rm = new RandomAccessFile(filePath, "r");
            long length = rm.length();
            //读取tag和ID，进行验证
            if (length > TAG_LENGTH + ID_LENGTH) {
                byte[] temp = new byte[TAG_LENGTH];
                rm.readFully(temp);

                if (Arrays.equals(temp, tag_AES)) {
                    type = CipherType.AES;
                } else if (Arrays.equals(temp, tag_DES)) {
                    type = CipherType.DES;
                } else {
                    throw new Exception(rb.getString("error:this_file_is_not_encrypted_no_need"));
                }
                byte[] temp2 = new byte[ID_LENGTH];
                rm.readFully(temp2);
                if (!Arrays.equals(temp2, id)) {
                    throw new Exception(rb.getString("error:wrong_password"));
                }
            } else {
                throw new Exception(rb.getString("error:this_file_is_not_encrypted_no_need"));
            }
            //通过验证之后，开始解密
            bw = new FileOutputStream(targetPath, false);
            //基础读写次数
            long times = (length - (ID_LENGTH + TAG_LENGTH)) / FILE_BUFFER_SIZE;
            Cipher cipher = Cipher.getInstance(type == CipherType.DES ? "DES/CBC/PKCS5Padding" : "AES/CBC/PKCS5Padding");
            if (type == CipherType.DES) {
                byte[] temp = new byte[8];
                System.arraycopy(iv, 0, temp, 0, 8);
                iv = temp;
                byte[] temp2 = new byte[8];
                System.arraycopy(key, 0, temp, 0, 8);
                key = temp2;
            }
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, type == CipherType.DES ? "DES" : "AES"), new IvParameterSpec(iv));
            byte[] temp = new byte[FILE_BUFFER_SIZE];
            for (long i = 0; i < times; i++) {
                rm.readFully(temp);
                bw.write(cipher.update(temp));
            }
            //判断是否有剩余
            int tempLength = (int) (length - (TAG_LENGTH + ID_LENGTH) - times * FILE_BUFFER_SIZE);
            if (tempLength > 0) {
                byte[] newTemp = new byte[tempLength];
                rm.readFully(newTemp);
                bw.write(cipher.update(newTemp));
            }
            //完成解密的最后步骤
            bw.write(cipher.doFinal());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (rm != null) {
                try {
                    rm.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
