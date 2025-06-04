package com.danfielden.crm;

import java.io.IOException;
import java.util.Base64;

public class ImageConvertor {

    public static byte[] base64StringToByteArr(String b64Img) {
        return Base64.getDecoder().decode(b64Img);
    }

}
