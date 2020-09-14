package com.hg.lib.ras;

/**
 * DES加密实用类
 *
 * @author zhouyw
 */
public class DescUtil {
    private final static String key = "YZJWTJMKEY";

    /**
     * 将字符串加密
     *
     * @param inputStr
     * @return
     * @throws Exception
     */
    public static String EncryPtDESC(String inputStr) throws Exception {
        byte[] inputData = inputStr.getBytes("UTF-8");
        inputData = DESCoder.encrypt(inputData, key);
        return DESCoder.encryptBASE64(inputData);
    }

    /**
     * 字符串解密
     *
     * @param inputStr
     * @return
     * @throws Exception
     * @throws Exception
     */
    public static String DncryPtDESC(String inputStr) throws Exception {
        byte[] inputData = DESCoder.decryptBASE64(inputStr);
        byte[] outputData = DESCoder.decrypt(inputData, key);
        return new String(outputData);
    }
}
