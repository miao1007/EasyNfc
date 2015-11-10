/*
 * Copyright (c) 2015. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package miao1007.github.com.utils;

import okio.ByteString;

/**
 * Created by leon on 8/23/15.
 * 数据传输单元转换封装
 */
public class APDUUtils {

  /**
   * Eg: getBitInByte(0x80,7) == true
   * value: 1 0 0 0 0 0 0 0
   * index: 7 6 5 4 3 2 1 0
   */
  public static boolean getBitInByte(byte b, int index) {
    return ((b >>> index) & 1) == 1;
  }

  /**
   * eg "10000000" => (byte)0x80
   */
  public static byte binaryStringToByte(String bString) {
    return ((byte) Integer.parseInt(bString, 2));//java没有unsigned char，防止出现负数溢出
  }

  /**
   * eg [0xff,0x31,0x12,0xef] => "ff3112eef"
   */
  public static String ByteArrayToHexString(byte[] bytes) {
    return ByteString.of(bytes).hex();
  }

  /**
   * eg "ff3112eef" => [0xff,0x31,0x12,0xef]
   * NFC发送I/O命令时经常用这个封装，而不是使用NIO
   */
  public static byte[] HexStringToByteArray(String hex) {
    return ByteString.decodeHex(hex).toByteArray();
  }
}
