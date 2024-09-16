import { sm2 } from 'sm-crypto';

// 公钥
const PUBLIC_KEY = '04981070e26f624917f2717bcaadc000c928c91b49c9c218df33260cafa1d2243c2427fd3486884a67d390751ff4956e35466fb4b925a666229b22d36c26267d67'
const PRIVATE_KEY = '057ab3e1e512e970023c16c545289ecf37dd2cb202daa24c42936f21daa061ac'

// 可配置参数
// 1 - C1C3C2；	0 - C1C2C3；  默认为1
const cipherMode = 1

//加密
export function doSM2Encrypt(str) {
    let msg = str
    if (typeof str !== 'string') {
        msg = JSON.stringify(str)
    }
    // console.log(msg,'加密前')
    let publicKey = PUBLIC_KEY
    // 加密结果
    let encryptData = sm2.doEncrypt(msg, publicKey, cipherMode)
    //Base64编码 自行选择是否使用
    //let baseEncode = Base64.encode(encryptData)
    // 加密后的密文前需要添加04，后端才能正常解密 (不添加04，后端处理也可以)
    let encrypt = '04' + encryptData
    return encrypt
}

// 解密
export function doSM2DecryptStr(enStr) {
    let msg = enStr
    if (typeof enStr !== 'string') {
        msg = JSON.stringify(enStr)
    }
    let privateKey = PRIVATE_KEY
    let enval = enStr.substring(2)
    // 解密结果
    let doDecrypt = sm2.doDecrypt(enval , privateKey, cipherMode)
    console.log("doDecrypt=>", doDecrypt)
    // 解密后类型转换
    return doDecrypt;
}