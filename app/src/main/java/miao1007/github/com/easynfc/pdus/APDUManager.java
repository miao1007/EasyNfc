package miao1007.github.com.easynfc.pdus;

import android.app.Activity;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.util.Log;
import java.io.IOException;
import miao1007.github.com.utils.LogUtils;

/**
 * Created by leon on 7/11/15.
 * NFC对象JAVA级别的代理类
 */
public class APDUManager {

  public static final String TAG = LogUtils.makeLogTag(APDUManager.class);

  private static final int MSG_SUCCESS = 0x01;
  private static final int MSG_FAIL = 0x02;

  NfcAdapter mNfcAdapter;
  Activity activity;

  private static APDUManager instance = null;

  private APDUManager(Activity activity) {
    //private constructor
    this.activity = activity;
    initial(this.activity);
  }

  public static APDUManager getInstance(Activity activity) {
    if (instance == null) {
      synchronized (APDUManager.class) {
        if (instance == null) {
          instance = new APDUManager(activity);
        }
      }
    }
    return instance;
  }

  public void onResume() {
    if ((mNfcAdapter != null) && (activity != null)) {
      //try {
      //  mNfcAdapter.disableForegroundDispatch(activity);
      //} catch (IllegalStateException e) {
      //  e.printStackTrace();
      //}
    }
  }

  private final void initial(Activity activity) {
    mNfcAdapter = NfcAdapter.getDefaultAdapter(activity);

    if (mNfcAdapter == null) {
      Log.d(TAG, "NFC is no found!");
      activity.finish();
      return;
    }

    if (!mNfcAdapter.isEnabled()) {
      Log.d(TAG, "NFC is disabled.");
    } else {
      //do nothing
    }
  }

  @WorkerThread public ResponseAPDU getResponseFromBytes(@NonNull Tag tag, byte[] bytes) {
    return ResponseAPDU.createFromPdu(getBytesFromBytes(tag, bytes));
  }

  @WorkerThread
  public ResponseAPDU getResponseFromRequest(@NonNull Tag tag, RequestAPDU requestAPDU) {
    return ResponseAPDU.createFromPdu(getBytesFromBytes(tag, requestAPDU.toBytes()));
  }

  @WorkerThread public byte[] getBytesFromRequest(@NonNull Tag tag, RequestAPDU requestAPDU) {

    return getBytesFromBytes(tag, requestAPDU.toBytes());
  }

  @WorkerThread public byte[] getBytesFromBytes(@NonNull Tag tag, byte[] bytes) {
    IsoDep iso = IsoDep.get(tag);
    byte[] result_all;
    //NfcA iso = NfcA.get(tag);
    if (iso == null) {
      Log.d(TAG, "Tech was not enumerated in NfcTechList");
      return null;
    }
    iso.setTimeout(5000);//ms
    try {
      if (!iso.isConnected()) {
        iso.connect();
      } else {
        iso.close();
        iso.connect();
      }
      result_all = iso.transceive(bytes);
      return result_all;
    } catch (IOException e) {
      Log.d(TAG, "NFC service has died");
    } finally {
      if (iso != null) {
        try {
          iso.close();
        } catch (IOException ignored) {
          Log.d(TAG, "Iso closed failed, may cause memory leak.");
        }
      }
    }
    return null;
  }
}
