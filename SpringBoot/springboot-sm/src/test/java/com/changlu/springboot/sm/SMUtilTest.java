package com.changlu.springboot.sm;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.BCUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import com.changlu.springboot.sm.util.SM2Util;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;

public class SMUtilTest {

    // 生成的一组私钥、公钥进行测试
    private String privateKey = "308193020100301306072a8648ce3d020106082a811ccf5501822d047930770201010420410ed06116014ad4460d2018069d94debeef593fd0e3bdc3d57ab8a84f8125e2a00a06082a811ccf5501822da14403420004171cc684596a7fd7dd26ea34ed5a7ea84b8ac124ed8e53432da4a0d1b287adbaea1f320a6db4763190dae57587ea8adf1f4254a250653b419dc64d2df9067d97";
    private String publicKey = "3059301306072a8648ce3d020106082a811ccf5501822d03420004171cc684596a7fd7dd26ea34ed5a7ea84b8ac124ed8e53432da4a0d1b287adbaea1f320a6db4763190dae57587ea8adf1f4254a250653b419dc64d2df9067d97";
    private String publicKeyQ = "04171cc684596a7fd7dd26ea34ed5a7ea84b8ac124ed8e53432da4a0d1b287adbaea1f320a6db4763190dae57587ea8adf1f4254a250653b419dc64d2df9067d97";
    private String privateKeyD = "410ed06116014ad4460d2018069d94debeef593fd0e3bdc3d57ab8a84f8125e2";

    // 随机生成的密钥对加密或解密
    @Test
    public void testRandomSM() {
        String text = "我是一段测试aaaa";
        // 随机生成
        KeyPair pair = SecureUtil.generateKeyPair("SM2");
        System.out.println("privateKey=>" + pair.getPrivate());
        System.out.println("publicKey=>" + pair.getPublic());
        System.out.println(pair.getPrivate().getFormat());
        System.out.println(pair.getPublic().getFormat());
        byte[] privateKey = pair.getPrivate().getEncoded();
        byte[] publicKey = pair.getPublic().getEncoded();
        SM2 sm2 = SmUtil.sm2(privateKey, publicKey);
        // 直接默认随机
//        SM2 sm2 = SmUtil.sm2();
        // 此时内部生成的是随机的公钥、密钥
        String encryptStr = sm2.encryptBcd(text, KeyType.PublicKey);
        String decryptStr = StrUtil.utf8Str(sm2.decryptFromBcd(encryptStr, KeyType.PrivateKey));
        System.out.println("encryptStr=>" + encryptStr);
        System.out.println("decryptStr=>" + decryptStr);
    }


    // 案例1：生成服务端公钥、私钥，前端js公钥、私钥
    @Test
    public void testDiySM() {
        String text = "我是一段测试aaaa";
        // 生成密钥对
        KeyPair keyPair = SM2Util.generateKeyPair();
        // 服务器端使用
        // 生成私钥
        String privateKey = HexUtil.encodeHexStr(keyPair.getPrivate().getEncoded());
        // 生成公钥
        String publicKey = HexUtil.encodeHexStr(keyPair.getPublic().getEncoded());
        System.out.println("privateKey=>" + privateKey);
        System.out.println("publicKey=>" + publicKey);

        // 前端使用
        // 生成公钥 Q，以Q值做为js端的加密公钥
        String publicKeyQ = HexUtil.encodeHexStr(((BCECPublicKey) keyPair.getPublic()).getQ().getEncoded(false));
        System.out.println("公钥Q:"+ publicKeyQ);
        // 生成私钥 D，以D值做为js端的解密私钥
        String privateKeyD = HexUtil.encodeHexStr(BCUtil.encodeECPrivateKey(keyPair.getPrivate()));
        System.out.println("私钥D:"+ privateKeyD);

        // 服务端加解密
        String encodeStr = SM2Util.encrypt(text, privateKey, publicKey);
        String formatStr = SM2Util.decrypt(encodeStr, privateKey, publicKey);
        System.out.println("encodeStr=>" + encodeStr);
        System.out.println("formatStr=>" + formatStr);
    }

    // 案例2：客户端加密，服务端完成解密
    @Test
    public void testDecrypt() {
        String encodeStr = "042690ad14344234388b0b9466d05e46af83ff423c0e0195999fedc28d85b650cde76d3995547a7b071cccef5be1b8a81480de5026940b75e388c23015fea55dec348417ee2e20a82d52c170b3ab040d14cde02531ec649b781e7e4339a9c8e64556c87950ab97";
        String formatStr = SM2Util.decrypt(encodeStr, privateKey, publicKey);
        System.out.println("formatStr=>" + formatStr);
    }

    // 案例3:服务端进行加密
    @Test
    public void testEncrypt() {
        String str = "changlu test test";
        String encodeStr = SM2Util.encrypt(str, privateKey, publicKey);
        System.out.println("encodeStr=>" + encodeStr);
    }

    // 案例2：客户端加密，服务端完成解密
    @Test
    public void testDecrypt2() {
        String encodeStr = "04b0ac2effc5d326b010e6c0507fbab7d7692784167d1f49ba3d53e1e095742f519e49b381d7e97b684e1258aecab853d05dbf8dac3765c929b076c43c4fa069515e9d69ebec48b51ba2e8730f18ccd4367f7f36a2ded725c971986181584f04897065932b79dd2859781fdc38916c27c9fe";
        String formatStr = SM2Util.decrypt(encodeStr, privateKeyD, publicKeyQ);
        System.out.println("formatStr=>" + formatStr);
    }

    // 案例3:服务端进行加密
    @Test
    public void testEncrypt2() {
        String str = "changlu test test";
        String encodeStr = SM2Util.encrypt(str, privateKeyD, publicKeyQ);
        System.out.println("encodeStr=>" + encodeStr);
    }

}
