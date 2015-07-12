package miao1007.github.com.easynfc;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import java.util.Arrays;
import miao1007.github.com.easynfc.card.CQEcashCard;
import miao1007.github.com.easynfc.pdus.APDUManager;
import miao1007.github.com.easynfc.pdus.ResponseAPDU;
import miao1007.github.com.utils.LogUtils;
import miao1007.github.com.utils.Util;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NfcscannerActivity extends AppCompatActivity {

  public static final String TAG = LogUtils.makeLogTag(MainActivity.class);

  APDUManager manager;
  private Handler backgroundHandler;

  @Override protected void onPause() {
    super.onPause();
  }

  @Override protected void onResume() {
    super.onResume();
    manager.onResume();
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_nfcscanner);
    manager = APDUManager.getInstance(this);
    handleIntent(getIntent());
  }

  @Override protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    Log.d(TAG, "onNewIntent");
    handleIntent(intent);
  }

  private void handleIntent(Intent intent) {
    Log.d(TAG, "handleIntent: " + intent.getAction());
    if (!intent.getAction().equals(NfcAdapter.ACTION_TECH_DISCOVERED)) {
      Log.d(TAG, "handleIntent: no valid action");
      return;
    }
    final Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
    Log.d(TAG, "Id:" + Util.toHexString(tag.getId()));
    Log.d(TAG, "TechList:" + Arrays.toString(tag.getTechList()));

    //new Thread(new Runnable() {
    //  @Override public void run() {
    //    byte[]  cmd =CQEcashCard.selectByName((byte) 0x11, (byte) 0x22);
    //    Log.d(TAG,
    //        manager.getResponseFromBytes(tag, CQEcashCard.selectByID((byte) 0x02)).toString());
    //    Log.d(TAG, manager.getResponseFromBytes(tag,cmd).toString());
    //    Log.d(TAG, manager.getResponseFromBytes(tag, CQEcashCard.cmd_getbanlance).toString());
    //    Log.d(TAG, manager.getResponseFromBytes(tag, CQEcashCard.get_record).toString());
    //  }
    //}).start();
    Subscriber subscriber = new Subscriber<ResponseAPDU>() {
      @Override public void onCompleted() {
        Log.d(TAG, "onCompleted:");
      }

      @Override public void onError(Throwable e) {
        Log.d(TAG, "onError:" + e.getMessage());
      }

      @Override public void onNext(ResponseAPDU responseAPDU) {
        Log.d(TAG, "onNext:" + responseAPDU.toString());
      }
    };
    getResponseAPDUObservable(tag, CQEcashCard.get_record).subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(subscriber);


  }

  Observable<ResponseAPDU> getResponseAPDUObservable(final Tag tag, final byte... bytes) {

    return Observable.create(new Observable.OnSubscribe<ResponseAPDU>() {
      @Override public void call(Subscriber<? super ResponseAPDU> subscriber) {
        Log.d(TAG, "onCall" + subscriber.toString());
        subscriber.onNext(manager.getResponseFromBytes(tag, bytes));
        subscriber.onCompleted();
      }
    });
  }
}
