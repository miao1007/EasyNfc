package miao1007.github.com.easynfc.card;

import java.nio.ByteBuffer;
import miao1007.github.com.easynfc.pdus.RequestAPDU;

/**
 * Created by leon on 7/11/15.
 * 重庆轨道交通一卡通
 */
public class CQEcashCard {

  public static final byte[] GET_RECORD = {
      (byte) 0x00, // CLA Class
      (byte) 0xB2, // INS Instruction 178
      (byte) 0x01, // P1 Parameter 1
      (byte) ((1 << 3) | 0x05), // P2 Parameter 2
      (byte) 0x00, // Le
  };
  public static final byte[] DFN_PSE = {
      (byte) '1', (byte) 'P', (byte) 'A', (byte) 'Y', (byte) '.', (byte) 'S', (byte) 'Y',
      (byte) 'S', (byte) '.', (byte) 'D', (byte) 'D', (byte) 'F', (byte) '0', (byte) '1',
  };
  public static final byte[] DFN_SRV = {
      (byte) 'P', (byte) 'A', (byte) 'Y', (byte) '.', (byte) 'S', (byte) 'Z', (byte) 'T'
  };
  public static final byte[] GET_BALANCE = {
      (byte) 0x80, // CLA Class
      (byte) 0x5C, // INS Instruction
      (byte) 0x00, // P1 Parameter 1
      (byte) 0x02, // P2 Parameter 2
      (byte) 0x04, // Le
  };

}
