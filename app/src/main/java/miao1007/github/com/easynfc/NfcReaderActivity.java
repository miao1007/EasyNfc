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
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.github.miao1007.lib.easynfc.card.CQEcashCard;
import com.github.miao1007.lib.easynfc.pdus.APDUManager;
import okio.ByteString;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NfcReaderActivity extends AppCompatActivity {

  public static final String TAG = NfcReaderActivity.class.getSimpleName();

  //@Bind(R.id.apdu_cla) EditText mEditText_cla;
  //@Bind(R.id.apdu_ans) EditText mEditText_ans;
  //@Bind(R.id.apdu_p1) EditText mEditText_p1;
  //@Bind(R.id.apdu_p2) EditText mEditText_p2;
  //@Bind(R.id.apdu_lc) EditText mEditText_lc;
  @Bind(R.id.apdu_data) EditText mEditText_data;
  //@Bind(R.id.apdu_le) EditText mEditText_le;
  @Bind(R.id.apdu_response) TextView mTextView_response;

  APDUManager manager;

  @OnClick(R.id.get_balance) void get_balance() {
    Log.d(TAG, "get_balance");
    manager.trans(ByteString.of(CQEcashCard.DFN_PSE), ByteString.of(CQEcashCard.SELECT_BY_ID),
        ByteString.of(CQEcashCard.GET_BALANCE)).subscribeOn(Schedulers.io())
        //.single(string -> !string.rangeEquals(0, ByteString.of((byte) 0x90, (byte) 0x00), 0, 2))
        .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ByteString>() {
      @Override public void onCompleted() {
        Log.d(TAG, "onCompleted");
      }

      @Override public void onError(Throwable e) {
        Log.d(TAG, "onError", e);
        mTextView_response.setText(e.getMessage());
      }

      /**
       * eg: 0x6982 => 17.10
       * @param string
       */
      @Override public void onNext(ByteString string) {
        Log.d(TAG, string.hex());
        mTextView_response.setText(string.hex());
      }
    });
  }

  @OnClick(R.id.get_record) void get_record() {
    manager.trans(ByteString.of(CQEcashCard.GET_RECORD))
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<ByteString>() {
          @Override public void onCompleted() {
            Log.d(TAG, "onCompleted");
          }

          @Override public void onError(Throwable e) {
            Log.d(TAG, "onError", e);
          }

          /**
           * 610f4f09a0000000038698070150023f01
           * 610f4f09a0000000038698070250023f02
           * 610f4f09a0000000038698070350023f03
           * 610f4f09a0000000038698070450023f04
           * 610f4f09a0000000038698070550023f05
           * 9000
           * @param string
           */
          @Override public void onNext(ByteString string) {
            //data[0]-data[1]:index
            //data[2]-data[4]:over,金额溢出?
            //    data[5]-data[8]:交易金额
            //data[9]:如果等于0x06或者0x09，表示刷卡；否则是充值
            //data[10]-data[15]:刷卡机或充值机编号
            //data[16]-data[22]:日期String.format("%02X%02X.%02X.%02X %02X:%02X:%02X",data[16], data[17], data[18], data[19], data[20], data[21], data[22]);
            //string.
            Log.d(TAG, string.hex());
            mTextView_response.setText(string.hex());
          }
        });
  }

  @OnClick(R.id.btn_send) void btn_send_cus() {
    manager.trans(ByteString.decodeHex(mEditText_data.getText().toString()));
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_nfcscanner);
    ButterKnife.bind(this);
    manager = new APDUManager(this);
    onNewIntent(getIntent());
  }

  @Override protected void onResume() {
    super.onResume();
    manager.onResume();
  }

  @Override protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    manager.onNewIntent(intent);
    get_balance();
  }

  @Override protected void onPause() {
    super.onPause();
    manager.onPause();
  }
}
