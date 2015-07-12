package miao1007.github.com.easynfc;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.util.Arrays;
import miao1007.github.com.easynfc.pdus.APDUManager;
import miao1007.github.com.easynfc.pdus.ResponseAPDU;
import miao1007.github.com.utils.LogUtils;
import miao1007.github.com.utils.Util;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NfcReaderActivity extends AppCompatActivity {

  public static final String TAG = LogUtils.makeLogTag(MainActivity.class);

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
    Subscriber subscriber = new Subscriber<ResponseAPDU>() {
      @Override public void onCompleted() {
        Log.d(TAG, "onCompleted:");
      }

      @Override public void onError(Throwable e) {
        mTextView_response.setText(e.getMessage());
      }

      @Override public void onNext(ResponseAPDU responseAPDU) {
        Log.d(TAG, "onNext:" + responseAPDU.toString());
        mTextView_response.setText(responseAPDU.toString());
      }
    };
    byte[] data = Util.hexStringToByteArray(mEditText_data.getText().toString());

    manager.getResponseAPDUObservable(tag, data)
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(subscriber);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_nfcscanner);
    ButterKnife.bind(this);
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
    tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
    Log.d(TAG, "Id:" + Util.byteArraytoHexString(tag.getId()));
    Log.d(TAG, "TechList:" + Arrays.toString(tag.getTechList()));
  }
}
