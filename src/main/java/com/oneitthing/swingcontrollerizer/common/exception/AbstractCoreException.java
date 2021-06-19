package com.oneitthing.swingcontrollerizer.common.exception;

import java.util.Properties;

import com.oneitthing.swingcontrollerizer.common.util.ResourceUtil;

/**
 * <p>[概 要]</p>
 *  共通例外の抽象基底クラスです.
 * <p>[詳 細]</p>
 *  このクラスを継承した例外クラスがスローされ、フレームワークに
 *  キャッチされると、自動的にユーザ通知が行われます。
 *  ユーザ通知を意図的にしない場合、setNotifyToUser(false)をコールして下さい。
 * <p>[備 考]</p>
 *

 */
public abstract class AbstractCoreException extends Exception implements CoreExceptionIF {
	/** シリアルバージョン番号 */
	private static final long serialVersionUID = 3907562037355776424L;
	/** propertiesのキーに対応します。 */
	private String id;
	/** idに対するメッセージを保持します。 */
	private String message;
	/** 例外をユーザ通知するかどうかのフラグです。デフォルトはtrueです。 */
	private boolean nofifyToUser = true;
	/** メッセージ取得元であるpropertiesインスタンスです。 */
	private Properties messages;

	/**
	 * メッセージリソースの所在を返却するメソッドです。
	 * AbstractCoreExceptionを継承する例外は必ずこのメソッドを実装して、
	 * メッセージ取得元を返却する必要が有ります。
	 * クラスパスの通ったディレクトリにあるpropertiesを以下のようにして
	 * 返却して下さい。
	 *
	 * <PRE>
	 * 	protected String getMessageSource(){
	 * 		return "com.oneitthing.swingcontrollerizer.common.exception.corelogic_message";
	 * 	}
	 * </PRE>
	 *
	 * @return propertiesが存在する名前空間
	 */
	protected abstract String getMessageSource();

	@Override
	public String getId() {
		return this.id;
	}

	/**
	 * <p>[概 要]</p>
	 * エラー番号設定
	 * <p>[詳 細]</p>
	 * <p>[備 考]</p>
	 *
	 * @param id エラー番号
	 */
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getMessage() {
		return this.message;
	}

	/**
	 * この例外が保持するエラーメッセージを返却します。
	 *
	 * @param エラーメッセージ
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public boolean isNotifyToUser() {
		return this.nofifyToUser;
	}

	@Override
	public void setNotifyToUser(boolean notifyToUser) {
		this.nofifyToUser = notifyToUser;
	}

	public AbstractCoreException() {

	}

	/**
	 * メッセージIDを引数に取るコンストラクタです。
	 * getMessageSource()で指定したProperties内のキーを指定して下さい。
	 *
	 * @param id メッセージID
	 */
	public AbstractCoreException(String id) {
		init();

		this.id = id;
		this.message = this.messages.getProperty(id);
	}

	/**
	 * メッセージIDと原因例外を引数に取るコンストラクタです。
	 * 既にスローされた例外をキャッチしてCoreExceptionIF実装例外
	 * に包みます。
	 * <PRE>
	 * 		try{
	 * 			:
	 * 		}catch(IOException e){
	 * 			throw new CoreLogicException("EFC9999", e);
	 * 		}
	 * </PRE>
	 * 上記のように発生した例外に対してメッセージを付けて、
	 * フレームワークにスローすることが出来ます。
	 *
	 * @param id メッセージID
	 * @param e 原因例外
	 */
	public AbstractCoreException(String id, Throwable e) {
		init();

		this.id = id;
		this.message = this.messages.getProperty(id);

		initCause(e);
	}

	/**
	 * 例外初期化メソッドです。
	 * getMessageSource()オーバーライドメソッドで返却された
	 * Propertiesファイルを読み込みます。
	 *
	 */
	protected void init() {
		this.messages = ResourceUtil.instance.asProperties(getMessageSource());
	}
}
