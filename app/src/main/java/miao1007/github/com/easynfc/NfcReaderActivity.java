/*
 * Copyright (c) 2015. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package miao1007.github.com.easynfc;

import android.content.Intent;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import miao1007.github.com.easynfc.card.CQEcashCard;
import miao1007.github.com.easynfc.pdus.APDUManager;
import miao1007.github.com.utils.LogUtils;
import okio.ByteString;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class NfcReaderActivity extends AppCompatActivity {

  public static final String TAG = LogUtils.makeLogTag(NfcReaderActivity.class);

  //@Bind(R.id.apdu_cla) EditText mEditText_cla;
  //@Bind(R.id.apdu_ans) EditText mEditText_ans;
  //@Bind(R.id.apdu_p1) EditText mEditText_p1;
  //@Bind(R.id.apdu_p2) EditText mEditText_p2;
  //@Bind(R.id.apdu_lc) EditText mEditText_lc;
  @Bind(R.id.apdu_data) EditText mEditText_data;
  //@Bind(R.id.apdu_le) EditText mEditText_le;
  @Bind(R.id.apdu_response) TextView mTextView_response;

  APDUManager manager;
  Tag tag;

  @OnClick(R.id.btn_send) void onClick() {

  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_nfcscanner);
    ButterKnife.bind(this);
    manager = new APDUManager(this);
    onNewIntent(getIntent());
  }

  @Override protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    manager.getTag(intent)
        .flatMap(new Func1<Tag, Observable<IsoDep>>() {
          @Override public Observable<IsoDep> call(Tag tag) {
            return manager.getIsoDep(tag, 4000);
          }
        })
        .flatMap(new Func1<IsoDep, Observable<byte[]>>() {
          @Override public Observable<byte[]> call(IsoDep isoDep) {
            return manager.getResponseAPDUObservable(isoDep, CQEcashCard.getBalance());
          }
        })
        .map(new Func1<byte[], String>() {
          @Override public String call(byte[] bytes) {
            return ByteString.of(bytes).utf8();
          }
        })
        .single(new Func1<String, Boolean>() {
          @Override public Boolean call(String s) {
            return s.endsWith("9000");
          }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<String>() {
          @Override public void onCompleted() {
            Log.d(TAG, "onCompleted");
          }

          @Override public void onError(Throwable e) {
            e.printStackTrace();
          }

          @Override public void onNext(String isoDep) {
            Log.d(TAG, "onNext" + isoDep);
          }
        });
  }

  @Override protected void onPause() {
    super.onPause();
    manager.onPause();
  }

  @Override protected void onResume() {
    super.onResume();
    manager.onResume();
  }
}
