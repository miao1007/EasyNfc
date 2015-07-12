package miao1007.github.com.easynfc.card;

import miao1007.github.com.easynfc.pdus.RequestAPDU;

/**
 * Created by leon on 7/11/15.
 */
public interface Card {

  /**
   * 获取卡号
   * @param apdu 查询命令
   * @return 卡号
   */
  long getNumber(RequestAPDU apdu);

  /**
   * 获取版本号
   * @param apdu 查询命令
   * @return 版本号
   */
  long getVersion(RequestAPDU apdu);


  /**
   * 获取有效时间开始的时间戳
   * @param apdu 查询命令
   * @return 开始时间戳
   */
  long getValidTimeStart(RequestAPDU apdu);

  /**
   * 获取有效时间结束的时间戳
   * @param apdu 查询命令
   * @return 结束时间戳
   */
  long getValidTimeEnd(RequestAPDU apdu);

  /**
   * 获取余额
   * @param apdu 查询命令
   * @return 余额
   */
  long getBalance(RequestAPDU apdu);

  /**
   * 获取Log
   * @param apdu 查询命令
   * @return Log
   */
  Object[] getLog(RequestAPDU apdu);
}
