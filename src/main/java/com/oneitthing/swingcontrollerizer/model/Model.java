package com.oneitthing.swingcontrollerizer.model;

import com.oneitthing.swingcontrollerizer.listener.ModelProcessListener;

/**
 * <p>[概 要] </p>
 * 全Modelクラスの基底となるI/Fです。
 *
 * <p>[詳 細] </p>
 * runメソッドI/Fを持ちます。
 * このI/Fを実装した具象モデルクラスは必ずrunメソッドを実装する必要が有ります。
 *
 * <p>[備 考] </p>
 *
 *


 *

 */
public interface Model {

	/**
	 * <p>[概 要] </p>
	 * モデル処理のFacadeとなるメソッドです。
	 *
	 * <p>[詳 細] </p>
	 * 実装クラスはこのメソッドを実装して具体的なModel処理を実装します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @throws Exception 発生する可能性の有る全例外
	 */
	public void run() throws Exception;

	/**
	 * <p>[概 要] </p>
	 * 実行結果取得
	 *
	 * <p>[詳 細] </p>
	 * モデルの実行結果を取得します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return モデル実行結果
	 */
	public Object getResult();

	/**
	 * <p>[概 要] </p>
	 * モデルリスナー追加
	 *
	 * <p>[詳 細] </p>
	 * モデル実行時のリスナーを追加します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param listener モデルリスナー
	 */
	public void addModelProcessListener(ModelProcessListener listener);
}
