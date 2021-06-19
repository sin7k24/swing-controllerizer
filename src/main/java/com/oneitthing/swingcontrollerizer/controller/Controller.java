package com.oneitthing.swingcontrollerizer.controller;

import com.oneitthing.swingcontrollerizer.action.Action;


/**
 * <p>[概 要] </p>
 * コントローラの基底I/Fです。
 *
 * <p>[詳 細] </p>
 * 実装クラスがコントローラであることを示します。
 *
 * <p>[備 考] </p>
 *
 *


 *

 */
public interface Controller {

	/**
	 * <p>[概 要]</p>
	 * コントローラの主幹メソッドです。
	 *
	 * <p>[詳 細]</p>
	 * 継承コントローラはこのメソッドをオーバーライドしてロジックフローを実装します。
	 *
	 * <p>[備 考]</p>
	 *
	 * @param actionClass 発生したイベントに対応する実行アクションクラス型
	 * @param parameterMapping MVC各レイヤを伝播するパラメータオブジェクト
	 */
	public Object invoke(Class<? extends Action> actionClass, ParameterMapping parameterMapping);
}
