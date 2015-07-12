package miao1007.github.com.easynfc.pdus;

import android.support.annotation.NonNull;
import java.util.Arrays;
import miao1007.github.com.utils.Util;

/**
 * Created by leon on 7/11/15.
 * Return following table
 *
 * <table>
 * <tr>
 * <td colspan="4" align="center">Response APDU</td>
 * </tr>
 * <tr>
 * <td colspan="2" align="center">Body(optional)</td>
 * <td colspan="2" align="center">Trailer(required)</td>
 * </tr>
 * <tr>
 * <td colspan="2" align="center">Data</td>
 * <td>SW1</td>
 * <td>SW2</td>
 * </tr>
 * </table>
 */

public class ResponseAPDU {

  public static final int SW_SIZE = 2;

  byte[] data;
  short sw;

  private ResponseAPDU(byte[] data, short sw) {
    this.data = data;
    this.sw = sw;
  }

  private ResponseAPDU() {
  }

  public static ResponseAPDU createFromPdu(@NonNull byte[] bytes) {
    return createFromPdu(bytes, bytes.length);
  }

  public static ResponseAPDU createFromPdu(byte[] raw_pdus, int newLength) {
    ResponseAPDU apdu = new ResponseAPDU();
    if (newLength > 2) {
      apdu.data = Arrays.copyOf(raw_pdus, newLength - SW_SIZE);
      apdu.sw = Util.bytesToShort(
          new byte[] { raw_pdus[newLength - SW_SIZE], raw_pdus[newLength - SW_SIZE - 1] });
    } else {
      apdu.sw = Util.bytesToShort(raw_pdus);
    }
    return apdu;
  }

  public byte[] getData() {
    return data;
  }

  public short getSw() {
    return sw;
  }

  public byte get_SW1() {
    return Util.shortToBytes(sw)[0];
  }

  public byte get_SW2() {
    return Util.shortToBytes(sw)[1];
  }

  @Override public String toString() {
    return "ResponseAPDU{" +
        "data=0x" + Util.byteArraytoHexString(data) +
        ", sw=0x" + Util.shortToHexString(sw) +
        '}';
  }
}
