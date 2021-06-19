package com.oneitthing.swingcontrollerizer.model;

import com.oneitthing.swingcontrollerizer.event.ModelProcessEvent;

/**
 * <p>[概 要]</p>
 * デフォルトモデルクラスです.
 *
 * <p>[詳 細]</p>
 * コントローラに１つもモデルを実行委譲しなかった場合
 * （アクションでモデルをリザーブしなかった場合）、コントローラによって代替採用される
 * モデルクラスです。<br>
 * イベントハンドル時の処理シーケンスを均一化する為に使用されます。
 *
 * <p>[備 考]</p>
 *


 */
public class DefaultModel extends BaseModel {

	/**
	 * <p>[概 要]</p>
	 * モデル処理成功イベントとモデル処理完了イベントを発行します.
	 *
	 * <p>[詳 細]</p>
	 * DefaultModelに具体的な処理は有りません。<br>
	 * モデル処理フロー終了時に、成功イベントと完了イベントを発行して終了します。
	 *
	 * <p>[備 考]</p>
	 */
	@Override
	protected void postproc() {
		fireModelSuccess(new ModelProcessEvent(this));
		fireModelFinished(new ModelProcessEvent(this));
	}

	/**
	 * <p>[概 要]</p>
	 * モデル処理失敗イベントを発行します.
	 *
	 * <p>[詳 細]</p>
	 * DefaultModelには具体的な処理が無い為、このメソッドがコールされることは
	 * 有りません。
	 *
	 * <p>[備 考]</p>
	 *
	 * @param e 発生した例外
	 * @return e 発生した例外
	 */
	@Override
	protected Exception trap(Exception e) {
		fireModelFailure(new ModelProcessEvent(this));
		fireModelFinished(new ModelProcessEvent(this));
		return e;
	}
}
