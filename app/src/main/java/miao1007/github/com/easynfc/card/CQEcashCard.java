package miao1007.github.com.easynfc.card;

import java.nio.ByteBuffer;
import miao1007.github.com.easynfc.pdus.RequestAPDU;

/**
 * Created by leon on 7/11/15.
 * 重庆轨道交通一卡通
 */
public class CQEcashCard implements Card {

  public static final byte[] cmd_getbanlance = {
      (byte) 0x80, // CLA Class
      (byte) 0x5C, // INS Instruction
      (byte) 0x00, // P1 Parameter 1
      (byte) 1, // P2 Parameter 2
      (byte) 0x04, // Le
  };

  public static final byte[] get_record = {
      (byte) 0x00, // CLA Class
      (byte) 0xB2, // INS Instruction
      (byte) 0x01, // P1 Parameter 1
      (byte) ((1 << 3) | 0x05), // P2 Parameter 2
      (byte) 0x00, // Le
  };

  public static byte[] selectByID(byte... id) {
    ByteBuffer buff = ByteBuffer.allocate(id.length + 6);
    buff.put((byte) 0x00) // CLA Class
        .put((byte) 0xA4) // INS Instruction
        .put((byte) 0x00) // P1 Parameter 1
        .put((byte) 0x00) // P2 Parameter 2
        .put((byte) id.length) // Lc
        .put(id).put((byte) 0x00); // Le

    return buff.array();
  }

  public static byte[] selectByName(byte... name) {
    ByteBuffer buff = ByteBuffer.allocate(name.length + 6);
    buff.put((byte) 0x00) // CLA Class
        .put((byte) 0xA4) // INS Instruction
        .put((byte) 0x04) // P1 Parameter 1
        .put((byte) 0x00) // P2 Parameter 2
        .put((byte) name.length) // Lc
        .put(name).put((byte) 0x00); // Le

    return buff.array();
  }

  @Override public long getNumber(RequestAPDU apdu) {
    return 0;
  }

  @Override public long getVersion(RequestAPDU apdu) {
    return 0;
  }

  @Override public long getValidTimeStart(RequestAPDU apdu) {
    return 0;
  }

  @Override public long getValidTimeEnd(RequestAPDU apdu) {
    return 0;
  }

  @Override public long getBalance(RequestAPDU apdu) {
    return 0;
  }

  @Override public Object[] getLog(RequestAPDU apdu) {
    return new Object[0];
  }
}
