package miao1007.github.com.easynfc.pdus;

import android.app.Activity;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.support.annotation.WorkerThread;
import android.util.Log;
import java.io.IOException;
import miao1007.github.com.utils.LogUtils;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by leon on 7/11/15.
 * NFC对象的代理类，在activity中注入，提供I/O功能与生命周期。
 */
public class APDUManager {

  public static final String TAG = LogUtils.makeLogTag(APDUManager.class);

  NfcAdapter mNfcAdapter;
  Activity activity;

  public APDUManager(Activity activity) {
    //private constructor
    this.activity = activity;
    initial(this.activity);
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

  public final Observable<ResponseAPDU> getResponseAPDUObservable(final Tag tag,
      final byte... bytes) {

    return Observable.create(new Observable.OnSubscribe<ResponseAPDU>() {
      @WorkerThread @Override public void call(Subscriber<? super ResponseAPDU> subscriber) {
        if (tag == null) {
          subscriber.onError(new NullPointerException(
              "Tag is null,try again to turn on NFC and keep your card close to your phone!"));
          return;
        }
        if (bytes == null || bytes.length == 0) {
          subscriber.onError(
              new NullPointerException("apdu is null or empty, cheak your command!"));
          return;
        }
        IsoDep iso = IsoDep.get(tag);
        byte[] result_all;
        //NfcA iso = NfcA.get(tag);
        if (iso == null) {
          subscriber.onError(new NullPointerException("Tech was not enumerated in NfcTechList"));
          return;
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
          subscriber.onNext(ResponseAPDU.createFromPdu(result_all));
          subscriber.onCompleted();
        } catch (IOException e) {
          subscriber.onError(e);
        } finally {
          if (iso != null) {
            try {
              iso.close();
            } catch (IOException ignored) {
              subscriber.onError(ignored);
            }
          }
        }
      }
    });
  }
}
