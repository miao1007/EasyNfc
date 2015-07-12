package miao1007.github.com.easynfc.pdus;

import java.nio.ByteBuffer;

/**
 * Created by leon on 7/11/15.
 */
public class RequestAPDU {

  public static final int REQUIRED_LENGTH = 4;

  //required
  byte cla;
  byte ins;
  byte p1;
  byte p2;
  //optional
  byte lc;
  byte[] data;
  byte le;

  public byte getCla() {
    return cla;
  }

  public void setCla(byte cla) {
    this.cla = cla;
  }

  public byte getIns() {
    return ins;
  }

  public void setIns(byte ins) {
    this.ins = ins;
  }

  public byte getP1() {
    return p1;
  }

  public void setP1(byte p1) {
    this.p1 = p1;
  }

  public byte getP2() {
    return p2;
  }

  public void setP2(byte p2) {
    this.p2 = p2;
  }

  public byte getLc() {
    return lc;
  }

  public void setLc(byte lc) {
    this.lc = lc;
  }

  public byte[] getData() {
    return data;
  }

  public void setData(byte[] data) {
    this.data = data;
  }

  public byte getLe() {
    return le;
  }

  public void setLe(byte le) {
    this.le = le;
  }

  RequestAPDU(byte cla, byte ins, byte p1, byte p2, byte lc, byte[] data, byte le) {
    this.cla = cla;
    this.ins = ins;
    this.p1 = p1;
    this.p2 = p2;
    this.lc = lc;
    this.data = data;
    this.le = le;
  }

  public byte[] toBytes() {
    if (data == null || this.data.length == 0) {
      ByteBuffer buff = ByteBuffer.allocate(4);
      buff.put(this.cla) // CLA Class
          .put(this.ins) // INS Instruction
          .put(this.p1) // P1 Parameter 1
          .put(this.p2); // P2 Parameter 2
      return buff.array();
    } else {
      ByteBuffer buff = ByteBuffer.allocate(this.data.length + 6);
      buff.put(this.cla) // CLA Class
          .put(this.ins) // INS Instruction
          .put(this.p1) // P1 Parameter 1
          .put(this.p2) // P2 Parameter 2
          .put(this.lc) // Lc
          .put(this.data).put(this.le); // Le
      return buff.array();
    }
  }

  public static class Builder {
    private byte cla;
    private byte ins;
    private byte p1;
    private byte p2;
    private byte lc;
    private byte[] data;
    private byte le;

    public Builder setCla(byte cla) {
      this.cla = cla;
      return this;
    }

    public Builder setIns(byte ins) {
      this.ins = ins;
      return this;
    }

    public Builder setP1(byte p1) {
      this.p1 = p1;
      return this;
    }

    public Builder setP2(byte p2) {
      this.p2 = p2;
      return this;
    }

    public Builder setData(byte[] data) {
      if (data.length == 0) {
        return this;
      }
      this.data = data;
      this.le = (byte) 0x00;
      this.lc = (byte) data.length;
      return this;
    }

    public RequestAPDU builder() {
      ensureSaneDefaults();
      return new RequestAPDU(cla, ins, p1, p2, lc, data, le);
    }

    private void ensureSaneDefaults() {
      //if ()
    }
  }
}
