package com.oneitthing.swingcontrollerizer.validator;

import java.awt.Component;
import java.util.Vector;

/**
 * <p>[概 要] </p>
 * ValidateErrorオブジェクトを集約するクラスです。
 *
 * <p>[詳 細] </p>
 * ValidateErrorオブジェクトを集約する為のVectorをフィールドに持ちます。<br>
 * そのフィールドを対象に、
 * <ul>
 *     <li>エラー発生有無返却</li>
 *     <li>エラー数返却</li>
 *     <li>エラーコンポーネント判定</li>
 *     <li>エラー集約への追加</li>
 * </ul>
 * 等の操作を行います。
 *
 * <p>[備 考] </p>
 *

 *

 */
public class ValidateErrors {

	/** ValidateErrorオブジェクトリストです。 */
	private Vector<ValidateError> errors;

	/**
	 * <p>[概 要] </p>
	 * バリデーションエラーが有ったかどうかを判定します。
	 *
	 * <p>[詳 細] </p>
	 * errorsフィールドのサイズを判定し、0以上であればtrueを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return true : エラー有り、 false : エラー無し
	 */
	public boolean hasError(){
		return this.errors.size() > 0 ? true : false;
	}

	/**
	 * <p>[概 要] </p>
	 * バリデーションエラーの数を返却します。
	 *
	 * <p>[詳 細] </p>
	 * errorsフィールドのサイズを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return errorsフィールドのサイズ
	 */
	public int size(){
		return this.errors.size();
	}

	/**
	 * <p>[概 要] </p>
	 * 引数componentがバリデーションエラーに含まれるか調べます。
	 *
	 * <p>[詳 細] </p>
	 * errorsフィールド内の各componentと引数componentが等しいかどうか調べます。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param component バリデーションエラーを起こしたかどうか調べるコンポーネント
	 * @return true : エラーコンポーネント
	 */
	public boolean contains(Component component){
		boolean ret = false;

		for(ValidateError error : this.errors){
			if(component.equals(error.getComponent())){
				ret = true;
				break;
			}
		}
		return ret;
	}

	/**
	 * <p>[概 要] </p>
	 * 引数errorをValidateErrorsリストに追加します。
	 *
	 * <p>[詳 細] </p>
	 * errorsフィールドに引数errorを追加します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param error 一バリデーションエラーオブジェクト
	 */
	public void addError(ValidateError error){
		this.errors.add(error);
	}

	/**
	 * <p>[概 要] </p>
	 * 引数指定されたインデックスのValidateErrorオブジェクトを返却します。
	 *
	 * <p>[詳 細] </p>
	 * errorsフィールドのindex番目を取得、返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param index errorsフィールドのインデックス
	 * @return indexに対応するValidateErrorオブジェクト
	 */
	public ValidateError getError(int index){
		return errors.get(index);
	}

	/**
	 * <p>[概 要] </p>
	 * コンストラクタです。
	 *
	 * <p>[詳 細] </p>
	 * errorsフィールドインスタンスを生成します。
	 *
	 * <p>[備 考] </p>
	 *
	 */
	public ValidateErrors(){
		this.errors = new Vector<ValidateError>();
	}
}
