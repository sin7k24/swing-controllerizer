package com.oneitthing.swingcontrollerizer.listener;

import java.util.EventListener;

import com.oneitthing.swingcontrollerizer.event.ModelProcessEvent;

/**
 * <p>[概 要] </p>
 * モデルの処理結果を監視するリスナI/Fです。
 *
 * <p>[詳 細] </p>
 *
 *
 * <p>[備 考] </p>
 *
 *


 *

 */
public interface ModelProcessListener extends EventListener {

	/**
	 * <p>[概 要]</p>
	 * モデル処理が成功した場合にコールされるリスナメソッドです。
	 *
	 * <p>[詳 細]</p>
	 * モデルから処理正常終了イベントを受信した時にコールされます。<BR>
	 * Object result = evt.getResult();<BR>
	 * で、モデル処理結果オブジェクトを取得出来ます。
	 *
	 * <p>[備 考]</p>
	 * PublishCoreの成功時やSubscribeCoreの購読中止成功時等、
	 * 正常終了した場合でもモデル処理結果オブジェクトがnullの場合が有ります。
	 *
	 * @param evt モデル処理が正常終了した情報が入ったイベントオブジェクト
	 */
	public void modelSuccess(ModelProcessEvent evt);

	/**
	 * <p>[概 要]</p>
	 * モデル処理が失敗した場合にコールされるリスナメソッドです。
	 *
	 * <p>[詳 細]</p>
	 * モデルから処理異常終了イベントを受信した時にコールされます。<BR>
	 * Exception e = evt.getException();<BR>
	 * で、モデル処理中に発生した例外を取得出来ます。
	 *
	 * <p>[備 考]</p>
	 *
	 * @param evt モデル処理が異常終了した情報が入ったイベントオブジェクト
	 */
	public void modelFailure(ModelProcessEvent evt);

	/**
	 * <p>[概 要]</p>
	 * モデル処理が終了した場合にコールされるリスナメソッドです。
	 *
	 * <p>[詳 細]</p>
	 * モデルから処理終了イベントを受信した時にコールされます。<BR>
	 *
	 * <p>[備 考]</p>
	 *
	 * @param evt モデル処理が終了した情報が入ったイベントオブジェクト
	 */
	public void modelFinished(ModelProcessEvent evt);
}
