package miao1007.github.com.easynfc.pdus;

/**
 * Created by leon on 7/12/15.
 */
public interface Callback {

  void onError(Throwable t);

  void onSuccess(ResponseAPDU responseAPDU);

}
