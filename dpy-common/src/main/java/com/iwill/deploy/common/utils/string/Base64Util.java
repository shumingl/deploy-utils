package com.iwill.deploy.common.utils.string;

import org.apache.commons.codec.binary.Base64;

import java.nio.charset.Charset;

public class Base64Util {
    public static String encode(byte[] input) {
        return Base64.encodeBase64String(input);
    }

    public static byte[] decode(String inputString) {
        return Base64.decodeBase64(inputString);
    }

    public String encode(String inputString, String encode) {
        try {
            return Base64.encodeBase64String(inputString.getBytes(encode));
        } catch (Exception e) {
            return null;
        }
    }

    public String decode(String inputString, String encode) {
        try {
            return new String(Base64.decodeBase64(inputString.getBytes(encode)), encode);
        } catch (Exception e) {
            return null;
        }
    }

    public String decode(String inputString, Charset charset) {
        try {
            return new String(Base64.decodeBase64(inputString.getBytes(charset)), charset);
        } catch (Exception e) {
            return null;
        }
    }
}

