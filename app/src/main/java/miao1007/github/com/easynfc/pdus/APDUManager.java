package miao1007.github.com.easynfc.pdus;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcF;
import android.support.annotation.RequiresPermission;
import android.support.annotation.WorkerThread;
import android.util.Log;
import java.io.IOException;
import java.util.Arrays;
import miao1007.github.com.utils.LogUtils;
import okio.ByteString;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by leon on 7/11/15.
 * NFC对象的代理类，在activity中注入，提供I/O功能与生命周期。
 */
public class APDUManager {

  public static final String TAG = LogUtils.makeLogTag(APDUManager.class);

  public static final int DEFALUT_TIMEOUT = 5000;

  NfcAdapter mNfcAdapter;
  Activity activity;
  PendingIntent pendingIntent ;
  IntentFilter tech;
  IntentFilter[] intentFiltersArray;
  String [][] techListsArray;

  @RequiresPermission("android.permission.NFC")
  public APDUManager(Activity activity) {
    //private constructor
    this.activity = activity;
    mNfcAdapter = NfcAdapter.getDefaultAdapter(activity);
    if (mNfcAdapter == null || !mNfcAdapter.isEnabled()){
      return;
    }

    pendingIntent = PendingIntent.getActivity(activity, 0,
        new Intent(activity, activity.getClass()), 0);
     tech = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
    try {

      tech.addDataType("*/*");    /* Handles all MIME based dispatches.
                                       You should specify only the ones that you need. */
    } catch (IntentFilter.MalformedMimeTypeException e) {
      throw new RuntimeException("fail", e);
    }
    intentFiltersArray = new IntentFilter[] { tech, };
    techListsArray = new String[][] { new String[] { NfcF.class.getName() } };
  }


  public void onPause() {

    if (mNfcAdapter != null)
      mNfcAdapter.disableForegroundDispatch(activity);
  }

  public void onResume() {

    if (mNfcAdapter != null)
      mNfcAdapter.enableForegroundDispatch(activity, pendingIntent, intentFiltersArray,
          techListsArray);
  }


public Observable<IsoDep> getIsoDep(final Tag tag,final int timeoutms){

  return Observable.create(new Observable.OnSubscribe<IsoDep>() {
    @WorkerThread @Override public void call(Subscriber<? super IsoDep> subscriber) {
      if (tag == null) {
        subscriber.onError(new NullPointerException(
            "Tag is null,try again to turn on NFC and keep your card close to your phone!"));
        return;
      }
      IsoDep iso = IsoDep.get(tag);
      if (iso == null) {
        subscriber.onError(new NullPointerException("Tech was not enumerated in NfcTechList"));
        return;
      }
      iso.setTimeout(timeoutms == 0?DEFALUT_TIMEOUT:timeoutms);//ms}
      subscriber.onNext(iso);
      subscriber.onCompleted();
      }
  });
}



  public final Observable<Tag> getTag(final Intent intent){
    return Observable.create(new Observable.OnSubscribe<Tag>() {
      @Override public void call(Subscriber<? super Tag> subscriber) {
        if (intent ==null || intent.getParcelableExtra(NfcAdapter.EXTRA_TAG) == null){
          subscriber.onError(new Throwable("intent has no valid action"));
          return;
        }
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Log.d(TAG, "Id(hex):" + ByteString.of(tag.getId()).hex());
        Log.d(TAG, "TechList:" + Arrays.toString(tag.getTechList()));
        subscriber.onNext(tag);
        subscriber.onCompleted();
      }
    });

  }

  public final Observable<byte[]> getResponseAPDUObservable(final IsoDep iso,
      final byte... bytes) {

    return Observable.create(new Observable.OnSubscribe<byte[]>() {
      @WorkerThread @Override public void call(Subscriber<? super byte[]> subscriber) {

        byte[] result_all;
        try {
          if (!iso.isConnected()) {
            iso.connect();
          } else {
            iso.close();
            iso.connect();
          }
          result_all = iso.transceive(bytes);
          subscriber.onNext(result_all);
          subscriber.onCompleted();
        } catch (IOException e) {
          subscriber.onError(e);
        } finally {
          try {
            iso.close();
          } catch (IOException ignored) {
            subscriber.onError(ignored);
          }
        }
      }
    });
  }
}
