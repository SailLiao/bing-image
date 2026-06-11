package top.sailliao.bing.test.kx;

import com.alibaba.fastjson2.JSON;
import fy.trust.openapi.sdk.TrustClient;
import fy.trust.openapi.sdk.dto.ResponseResult;

public class KxTest {

    public static void main(String[] args) {
        // TrustClient client = new TrustClient("sdkconfig.properties");
        TrustClient client = new TrustClient("sdkconfig-yc.properties");

        // 文件
        ResponseResult result = client.addFile("testfile1", "D:\\Git\\Liaozifan\\bing-image\\src\\main\\java\\top\\sailliao\\bing\\test\\input.jpeg", "12345");
        System.out.println(JSON.toJSONString(result));
        // {"code":200,"data":{"evidenceName":"test","orderNo":"FYNF-CZ011871","notarizationNum":"CZ011871","certificateUrl":"https://evidence.bnpo.gov.cn:4430/forensics-project-admin/minio/download/820ae57c-7067-428d-8a60-b0512937b08e.pdf","outOrderNo":"123"},"message":"Success","sign":"i8wGKmXQESX1Gb3pFGYSU1YD4lk1j+4R/+/A4vL1T2R6uXT5IhDJgUsbJnaNwjNqxMFTZm5/cSv/SiO4RTU7CKSgpn6HifbF9pe5YX2lRn1Ihp9Lv7RVExlXegdUOdZ2AJw0EVMqYPpc/EMV1CsHC6ifh8PLG8O94nE7p1eUt30=","success":true}

        // 添加hash存证
        /*ResponseResult result = client.addHash("test", "a23a15c32c5c098fc861d3713c95c87ea0eb02f844398414902d81301b107344", "123", "123", "123", "123");
        System.out.println(JSON.toJSONString(result));*/
        // {"code":200,"data":{"evidenceName":"test","orderNo":"FYNF-CZ011872","notarizationNum":"CZ011872","outOrderNo":"123"},"message":"Success","sign":"rNjjbCWgZSjff/2k62vOhlnNz9y0eNTSAbNLRd6/9M8kdOSP+HPe/eqjeQwauYl0GBSy1MFNQ5bU5NlYLAdmxEuhikcXNsCfXuTwKiuLpsEp3sFRTuhXyRpKSKCooPSL2gmOYXuOy0olWeTn1nyD6m/5R4YsEHpQQYHx0h1Et9o=","success":true}

        // {"fileHash": "a23a15c32c5c098fc861d3713c95c87ea0eb02f844398414902d81301b107363", "fileName": "input.jpeg", "fileSize": 163254}
        /*ResponseResult result = client.verifyHash("a23a15c32c5c098fc861d3713c95c87ea0eb02f844398414902d81301b107363");
        System.out.println(JSON.toJSONString(result));*/
        // {"code":200,"data":{"list":[{"evidenceName":"test","notarizationNum":"CZ011871","createTime":"2026-05-21 10:31:11","type":"FILE"}],"hash":"a23a15c32c5c098fc861d3713c95c87ea0eb02f844398414902d81301b107363"},"message":"Success","sign":"G5zA58XdEqpRMC54mDtyy2PiXYt2WNsTPZBFu13FOzA/cOO6om/YSG0s2ztQhxDskziuEHrnkY9jFqhyvzv2fYE7528ZrxjRTayB4EVa25/iW9Dh318783nFna1ROCaX7T0CT3oBfHtFr5m0aLzMNPlxSKwzQD6OuLbfoDL92Fc=","success":true}

    }

}
