package com.oneitthing.swingcontrollerizer.action;

import com.oneitthing.swingcontrollerizer.controller.ParameterMapping;

/**
 * <p>[概 要] </p>
 * コントローラから起動される全アクションのI/Fです。
 *
 * <p>[詳 細] </p>
 * コントローラがアクション起動する為のexecuteメソッドI/Fを提供します。
 *
 * <p>[備 考] </p>
 *
 *


 *

 */
public interface Action {

	/**
	 * <p>[概 要] </p>
	 * コントローラにコールされるアクションの主幹メソッドです。
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @param parameterMapping パラメータマッピング
	 * @return パラメータマッピング
	 * @throws Exception アクション実行例外
	 */
	public ParameterMapping run(ParameterMapping parameterMapping) throws Exception;
}
