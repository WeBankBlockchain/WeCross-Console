package com.webank.wecross.console;

import com.webank.wecross.console.common.TarUtils;
import org.junit.Assert;
import org.junit.Test;

public class TarUtilsTest {
    @Test
    public void encodeTest() throws Exception {
        String name = "sacc";
        String str =
                TarUtils.generateTarGzInputStreamEncodedString(
                        "classpath:contracts/chaincode/" + name);
        System.out.println(str);
        Assert.assertTrue(str.length() != 0);
    }
}
