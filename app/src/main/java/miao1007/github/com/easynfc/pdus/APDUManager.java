package miao1007.github.com.easynfc.pdus;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.util.Log;
import java.util.Arrays;
import java.util.NoSuchElementException;
import miao1007.github.com.utils.LogUtils;
import okio.ByteString;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by leon on 7/11/15.
 * NFC对象的代理类，在activity中注入，提供I/O功能与生命周期。
 */
public class APDUManager {

  public static final String TAG = LogUtils.makeLogTag(APDUManager.class);

  public static final int DEFALUT_TIMEOUT = 5000;

  static IntentFilter[] NFC_FILTERS;
  static String[][] NFC_TECHLISTS;

  static {

    try {
      String[][] strings = new String[1][];
      //最常见的卡片类型就是IsoDep
      strings[0] = new String[] { IsoDep.class.getName() };
      NFC_TECHLISTS = strings;
      NFC_FILTERS =
          new IntentFilter[] { new IntentFilter("android.nfc.action.TECH_DISCOVERED", "*/*") };
    } catch (Exception e) {
      e.printStackTrace();//ignored
    }
  }

  NfcAdapter mAdapter;
  Activity mActivity;
  PendingIntent pendingIntent;

  @RequiresPermission("android.permission.NFC") public APDUManager(Activity mActivity) {

    this.mActivity = mActivity;
    mAdapter = NfcAdapter.getDefaultAdapter(mActivity);
    if (mAdapter == null || !mAdapter.isEnabled()) {
      return;
    }
    /**
     * 处理弹出框分发
     */
    pendingIntent = PendingIntent.getActivity(mActivity, 0,
        new Intent(mActivity, mActivity.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
  }

  Observable<NfcAdapter> getDefaultAdapter(Activity activity) {
    return Observable.create(new Observable.OnSubscribe<NfcAdapter>() {
      @Override public void call(Subscriber<? super NfcAdapter> subscriber) {
        NfcAdapter mNfcAdapter = NfcAdapter.getDefaultAdapter(activity);
        if (mNfcAdapter == null || !mNfcAdapter.isEnabled()) {
          subscriber.onError(new NoSuchElementException("NFC is off"));
        }
        subscriber.onNext(mNfcAdapter);
        subscriber.onCompleted();
      }
    });
  }

  /**
   * wtf... update the intent
   * @param intent new intent from NFC
   */
  public void onNewIntent(Intent intent) {
    Log.d(TAG, "onNewIntent:" + intent.toString());
    mActivity.setIntent(intent);
  }

  public void onPause() {
    if (mActivity != null) {
      mAdapter.disableForegroundDispatch(mActivity);
    }
  }

  public void onResume() {
    if (mActivity != null && mAdapter != null) {
      mAdapter.enableForegroundDispatch(mActivity, pendingIntent, NFC_FILTERS, NFC_TECHLISTS);
    }
  }

  @NonNull public Observable<IsoDep> getIsoDep() {
    Log.d(TAG, "getIsoDep");
    return getTag().map(IsoDep::get);
  }

  @NonNull public final Observable<Tag> getTag() {
    Log.d(TAG, "getTag");
    return Observable.create(new Observable.OnSubscribe<Tag>() {
      @Override public void call(Subscriber<? super Tag> subscriber) {
        Intent intent = mActivity.getIntent();
        Log.d(TAG, intent == null ? "null" : intent.toString());
        if (intent == null || intent.getParcelableExtra(NfcAdapter.EXTRA_TAG) == null) {
          subscriber.onError(new Throwable("Intent has no valid action for NFC flag"));
          subscriber.onCompleted();
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

  /**
   * 多Hex的I/O流处理
   */
  public final Observable<ByteString> trans(final ByteString... byteString) {
    return Observable.from(byteString).flatMap(new Func1<ByteString, Observable<ByteString>>() {
      @Override public Observable<ByteString> call(ByteString byteString) {
        return trans(byteString);
      }
    });
  }

  public boolean isSuccess(ByteString byteString) {
    return byteString.rangeEquals(0, ByteString.of((byte) 0x90, (byte) 0x00), 0, 2);
  }

  public final Observable<ByteString> trans(final ByteString byteString) {
    Log.d(TAG, "trans");
    return getIsoDep().map(isoDep2 -> {
      try {
        if (!isoDep2.isConnected()) {
          isoDep2.connect();
        } else {
          isoDep2.close();
          isoDep2.connect();
        }
        byte[] ret = isoDep2.transceive(byteString.toByteArray());
        Log.d(TAG, "RX(Hex)=" + byteString.toString());
        Log.d(TAG, "TX(Hex)=" + ByteString.of(ret).toString());
        return ret;
      } catch (Exception e) {
        e.printStackTrace();
        return null;
      } finally {
        try {
          isoDep2.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }).map(ByteString::of);
  }
}
