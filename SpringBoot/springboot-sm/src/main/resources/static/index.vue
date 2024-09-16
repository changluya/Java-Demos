<template>
  <div></div>
</template>

<script>
import { doSM2Encrypt, doSM2DecryptStr } from '@/utils/sm2'

export default {
  name: 'Index',
  data() {
    return {
    }
  },
  created() {
    this.testEncode()
  },
  methods: {
    testEncode() {
      // 案例1：前端进行加密
      let str = "123456"
      let encodeStr = doSM2Encrypt(str)
      console.log("前端明文加密之后=>", encodeStr)
      // 案例2：前端自己加密之后的内容进行解密
      let originStr = doSM2DecryptStr(encodeStr)
      console.log("前端自行加密，解密之后=>", originStr)
      // 案例3：服务器端内容加密后进行解密
      let testEncodeStr = '04c719fa9dff41a22b8119a3f1ba984303d19c295f1f6a6b8196c7a330cf0e9e1830cbd3cd949c49be0681fd2fa9abca3ed14f8f8d4111c552ef7603793c0a2ae344e9072b7dcaefaf5785634a624d4ca7addcf4ab9ff37abe4ec69847ee5e24a65ce74e8a5b1aecdc467d18b36fba22a8e8'
      originStr = doSM2DecryptStr(testEncodeStr)
      console.log("服务器端进行加密，解密之后=>", originStr)
    }
  }
}
</script>
<style scoped>

</style>
