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
    private String privateKey = "308193020100301306072a8648ce3d020106082a811ccf5501822d047930770201010420057ab3e1e512e970023c16c545289ecf37dd2cb202daa24c42936f21daa061aca00a06082a811ccf5501822da14403420004981070e26f624917f2717bcaadc000c928c91b49c9c218df33260cafa1d2243c2427fd3486884a67d390751ff4956e35466fb4b925a666229b22d36c26267d67";
    private String publicKey = "3059301306072a8648ce3d020106082a811ccf5501822d03420004981070e26f624917f2717bcaadc000c928c91b49c9c218df33260cafa1d2243c2427fd3486884a67d390751ff4956e35466fb4b925a666229b22d36c26267d67";
    private String publicKeyQ = "04981070e26f624917f2717bcaadc000c928c91b49c9c218df33260cafa1d2243c2427fd3486884a67d390751ff4956e35466fb4b925a666229b22d36c26267d67";
    private String privateKeyD = "057ab3e1e512e970023c16c545289ecf37dd2cb202daa24c42936f21daa061ac";

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
        String encodeStr = "04badafbddce5f728fb11c2007f2230b2fcd0ecf019ac4536370c75dc2e222ca696d20033ab8f76965bd1a9e2691b7a6e4e62d71627874cedd6138453444e1868881e69dbcd3ca13818d6db061561fb87da14e061d9d1c82d550322b2e04c60bcca7998ac51059";
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

}
