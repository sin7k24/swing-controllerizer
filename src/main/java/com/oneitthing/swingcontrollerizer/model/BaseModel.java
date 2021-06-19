package com.oneitthing.swingcontrollerizer.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.swing.SwingUtilities;

import com.oneitthing.swingcontrollerizer.controller.BaseController;
import com.oneitthing.swingcontrollerizer.controller.ParameterMapping;
import com.oneitthing.swingcontrollerizer.event.ModelProcessEvent;
import com.oneitthing.swingcontrollerizer.listener.ModelProcessListener;

/**
 * <p>[概 要]</p>
 * Model I/Fを実装した、全てのModelの基底クラスです。
 *
 * <p>[詳 細]</p>
 * BaseControllerに通常スレッドとして実行されます。<br>
 * <ol>
 *   <li>{@link #init()}</li>
 *   <li>{@link #run()}</li>
 *   <li>　{@link #preproc()}</li>
 *   <li>　{@link #mainproc()}</li>
 *   <li>　{@link #postproc()}</li>
 *   <li>　{@link #trap(Exception)}（例外発生時のみ）</li>
 *   <li>　{@link #finalproc()}</li>
 *   <li>{@link #done()}</li>
 * </ol>
 * の順にテンプレートコールされます。<br>
 * setSkip(true)が設定されているモデルの場合、init後のrunはコールされませんが、doneはコールされます。
 * <p>
 *
 * この基底Modelクラスを継承したModelクラスは、以下の3つのイベントを
 * 発火する処理を実装する必要が有ります。
 *
 * <p/>
 * Model処理成功イベント：
 * <pre class="samplecode">
 *		ModelProcessEvent evt = new ModelProcessEvent(this);
 *		evt.setResult(result); // 任意のモデル処理結果
 *		fireModelSuccess(evt);
 * </pre>
 *
 * Model処理失敗イベント：
 * <pre class="samplecode">
 *		ModelProcessEvent evt = new ModelProcessEvent(this);
 *		evt.setExeption(e); // 発生した例外
 *		fireModelFailure(evt);
 * </pre>
 *
 * Model処理完了イベント：
 * <pre class="samplecode">
 *		ModelProcessEvent evt = new ModelProcessEvent(this);
 *		fireModelFinished(evt);
 * </pre>
 *
 * 成功イベント、失敗イベントをBaseController内のモデル処理監視リスナがハンドリングすることによって、
 * {@link com.oneitthing.swingcontrollerizer.action.BaseAction#successForward(int, Model, Object)}
 * と
 * {@link com.oneitthing.swingcontrollerizer.action.BaseAction#failureForward(int, Model, Exception)}
 * がコールバックされます。<br>
 *
 * BaseAction継承クラス内ではモデル処理の正常、異常を上記のメソッドをオーバーライド
 * することで検知することが出来ます。<br>
 * モデル処理監視リスナが予約されているモデル数分の完了イベントを受け取ると、
 * {@link com.oneitthing.swingcontrollerizer.action.BaseAction#complete(ParameterMapping)}
 * がコールバックされます。<br>
 *
 * <p>[備 考]</p>
 * SwingControllerizerパッケージ内に存在する、～.model.*.～Coreクラスにはイベント発火処理が既に実装されています。
 * これらのクラスを継承する場合は、イベントディスパッチ処理を実装する必要は有りません。
 *


 *

 */
public class BaseModel implements Model, Callable<Object>{

	/** 汎用的なモデル処理結果格納オブジェクトです。 */
	private Object result;

	/** MVC各レイヤを巡回するパラメータオブジェクトです。 */
	private ParameterMapping parameterMapping;

	/** このモデルを起動したコントローラインスタンスです。 */
	private BaseController controller;

	/** このモデルがコントローラによって実行された実行順位です。 */
	private int executeIndex;

	/** このモデルの正常終了、異常終了を監視するリスナリストです。 */
	private List<ModelProcessListener> listenerList = new ArrayList<ModelProcessListener>();;

	/** モデル処理が成功した回数です。継続的に結果を返却するタイプのモデルがincrementします。 */
	private int successCount;

	/** このモデルを実行するかコントローラが判断する為のフラグです。 */
	private boolean skip;

	/** このモデルを呼び出し元スレッドと非同期で実行するかどうかのフラグです。（デフォルト:false） */
	private boolean async;


	/**
	 * <p>[概 要] </p>
	 * 汎用的なモデル処理結果格納オブジェクトを返却します。
	 *
	 * <p>[詳 細] </p>
	 * resultフィールド値を変革します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return 汎用的なモデル処理結果格納オブジェクト
	 */
	public Object getResult() {
		return this.result;
	}

	/**
	 * <p>[概 要] </p>
	 * 汎用的なモデル処理結果格納オブジェクトを設定します。
	 *
	 * <p>[詳 細] </p>
	 * resultフィールド値を引数resultで設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param result 汎用的なモデル処理結果格納オブジェクト
	 */
	public void setResult(Object result) {
		this.result = result;
	}

	/**
	 * <p>[概 要] </p>
	 * MVC各レイヤを巡回するパラメータオブジェクトを返却します。
	 *
	 * <p>[詳 細] </p>
	 * parameterMappingフィールド値を返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return MVC各レイヤを巡回するパラメータオブジェクト
	 */
	public ParameterMapping getParameterMapping() {
		return this.parameterMapping;
	}

	/**
	 * <p>[概 要] </p>
	 * MVC各レイヤを巡回するパラメータオブジェクトを設定します。
	 *
	 * <p>[詳 細] </p>
	 * parameterMappingフィールド値を引数parameterMappingで設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param parameterMapping MVC各レイヤを巡回するパラメータオブジェクト
	 */
	public void setParameterMapping(ParameterMapping parameterMapping) {
		this.parameterMapping = parameterMapping;
	}

	/**
	 * <p>[概 要] </p>
	 * このモデルを起動したコントローラインスタンスを返却します。
	 *
	 * <p>[詳 細] </p>
	 * controllerフィールド値を返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return このモデルを起動したコントローラインスタンス
	 */
	public BaseController getController() {
		return this.controller;
	}

	/**
	 * <p>[概 要] </p>
	 * このモデルを起動したコントローラインスタンスを設定します。
	 *
	 * <p>[詳 細] </p>
	 * controllerフィールド値を引数controllerで設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param controller
	 */
	public void setController(BaseController controller) {
		this.controller = controller;
	}

	/**
	 * <p>[概 要] </p>
	 * このモデルがコントローラによって実行された実行順位を返却します。
	 *
	 * <p>[詳 細] </p>
	 * executeIndexフィールド値を返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return このモデルがコントローラによって実行された実行順位
	 */
	public int getExecuteIndex() {
		return this.executeIndex;
	}

	/**
	 * <p>[概 要] </p>
	 * このモデルがコントローラによって実行された実行順位を設定します。
	 *
	 * <p>[詳 細] </p>
	 * executeIndexフィールド値を引数executeIndexで設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param executeIndex このモデルがコントローラによって実行された実行順位
	 */
	public void setExecuteIndex(int executeIndex) {
		this.executeIndex = executeIndex;
	}

	/**
	 * <p>[概 要] </p>
	 * このモデルの正常終了、異常終了を監視するリスナリストを返却します。
	 *
	 * <p>[詳 細] </p>
	 * listenerListフィールド値を返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return このモデルの正常終了、異常終了を監視するリスナリスト
	 */
	public List<ModelProcessListener> getListenerList() {
		return this.listenerList;
	}

	/**
	 * <p>[概 要] </p>
	 * このモデルの正常終了、異常終了を監視するリスナリストを設定します。
	 *
	 * <p>[詳 細] </p>
	 * listenerListフィールド値を引数listenerListで設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param listenerList このモデルの正常終了、異常終了を監視するリスナリスト
	 */
	public void setListenerList(List<ModelProcessListener> listenerList) {
		this.listenerList = listenerList;
	}

	/**
	 * <p>[概 要] </p>
	 * モデル監視リスナリストにモデル監視リスナを追加します。
	 *
	 * <p>[詳 細] </p>
	 * listenerListフィールドオブジェクトに引数listenerを追加します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param listener モデル監視リスナ
	 */
	public void addModelProcessListener(ModelProcessListener listener) {
		getListenerList().add(listener);
	}

	/**
	 * <p>[概 要] </p>
	 * モデル監視リスナリストからモデル監視リスナを削除します。
	 *
	 * <p>[詳 細] </p>
	 * listenerListフィールドオブジェクトから引数listenerを削除します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param listener モデル監視リスナ
	 */
	public void removeModelProcessListener(ModelProcessListener listener) {
		getListenerList().remove(listener);
	}

	/**
	 * <p>[概 要] </p>
	 * モデルインスタンス生存中に、何回モデル処理が成功したかを返却します。
	 *
	 * <p>[詳 細] </p>
	 * successCountフィールド値を返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return モデル処理成功回数
	 */
	public int getSuccessCount() {
		return this.successCount;
	}

	/**
	 * <p>[概 要] </p>
	 * モデル処理成功回数を1増加させて返却します。
	 *
	 * <p>[詳 細] </p>
	 * successCountフィールド値をインクリメントします。<br>
	 * モデル処理が成功したタイミングでコールされる、fireModelSuccessメソッド内でコールされます。
	 *
	 * <p>[備 考] </p>
	 * SubscribeCore等、継続的に成功イベントを発行するモデルはsuccessCount値が増大します。
	 *
	 * @return インクリメント後のモデル処理成功回数
	 */
	protected int incrementSuccessCount(){
		return this.successCount++;
	}

	/**
	 * <p>[概 要] </p>
	 * このモデルを実行するか、コントローラが判断する為のフラグを取得します。
	 *
	 * <p>[詳 細] </p>
	 * skipフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 * このフラグがtrueの場合、コントローラはこのモデルを実行しません。
	 *
	 * @return このモデルを実行するかコントローラが判断する為のフラグ
	 */
	public boolean isSkip() {
		return skip;
	}

	/**
	 * <p>[概 要] </p>
	 * このモデルを実行するか、コントローラが判断する為のフラグを設定します。
	 *
	 * <p>[詳 細] </p>
	 * skipフィールドを引数skipで設定します。
	 *
	 * <p>[備 考] </p>
	 * 下記例の場合、予約モデルは3つですが、実行されるのは2つになります。<p>
	 * <b>使用例)</b><br>
	 * ・一回目の通信結果を判断して二回目の実行モデルを切り替える
	 * <pre class="samplecode">
     *    &#064;Override
     *    protected void reserveModels(List<Class<? extends Model>> models) {
     *        // 実行する可能性の有るモデルを予め登録
     *        models.add(HTTPRequestCore.class);
     *        models.add(SubscribeCore.class);
     *        models.add(PublishCore.class);
     *    }
     *
     *    &#064;Override
     *    public boolean nextModel(int index, ModelProcessEvent prev, Model next) throws Exception {
     *        switch(index){
     *            case 0:
     *                ((HTTPRequestCore)next).setRequestUrl("webcontroller");
     *                ((HTTPRequestCore)next).addUrlParameters("model.fqcn", "demo.server.model.DataFetchModel");
     *                break;
     *            case 1:
     *                // 一回目の通信結果を取得
     *                this.dao = (DataDao)prev.getResult();
     *                if(this.dao.getDivision() == 0) {
     *                    ((SubscribeCore)next).setTopic(channelName);
     *                    ((SubscribeCore)next).setSubscribe(true);
     *                }else {
     *                    // 想定外なのでスキップ。SubscribeCoreをコントローラに実行させない
     *                    ((SubscribeCore)next).setSkip(true);
     *                }
     *                break;
     *            case 2:
     *                if(this.dao.getDivision() == 1) {
     *                    ((PublishCore)next).setTopic(channelName);
     *                    ((PublishCore)next).setPublishObject(handleName + "が入室しました。");
     *                }else{
     *                    // 想定外なのでスキップ。PublishCoreをコントローラに実行させない
     *                    ((PublishCore)next).setSkip(true);
     *                }
     *                break;
     *        }
     *        return true;
     *    }
	 * </pre>
	 *
	 * @param skip このモデルを実行するかコントローラが判断する為のフラグ
	 */
	public void setSkip(boolean skip) {
		this.skip = skip;
	}

	/**
	 * <p>[概 要] </p>
	 * このモデルを呼び出し元スレッドと非同期で実行するかどうか調べます。
	 *
	 * <p>[詳 細] </p>
	 * asyncフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return true : 非同期、false : 同期
	 */
	public boolean isAsync() {
		return async;
	}

	/**
	 * <p>[概 要] </p>
	 * このモデルを呼び出し元スレッドと非同期で実行するかどうか設定します。
	 *
	 * <p>[詳 細] </p>
	 * asyncフィールドを引数asyncで設定します。<br>
	 * trueが設定された場合、モデルスレッド実行元はこのスレッドをjoinしません。
	 *
	 * <p>[備 考] </p>
	 * デフォルトはfalse（同期実行）です。
	 *
	 * @param async true : 非同期、false : 同期
	 */
	public void setAsync(boolean async) {
		this.async = async;
	}


	/**
	 * <p>[概 要] </p>
	 * モデルスレッド処理エントリポイントです。
	 *
	 * <p>[詳 細] </p>
	 * {@link #run()}を呼び出して{@link #getResult()}を返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return モデルスレッドの処理結果
	 */
	@Override
	public Object call() throws Exception {
		run();

		return getResult();
	}

	/**
	 * <p>[概 要] </p>
	 * モデル初期化メソッドです。
	 *
	 * <p>[詳 細] </p>
	 * モデルスレッド起動前にコールされます。<br>
	 * モデルインスタンスの生成直後にコールされる為、
	 * {@link com.oneitthing.swingcontrollerizer.action.BaseAction#nextModel(int, ModelProcessEvent, Model)}
	 * 実行までに行っておきたい初期化処理等を記述出することが出来ます。
	 *
	 * <p>[備 考] </p>
	 *
	 * @throws Exception
	 */
	public void init() throws Exception {
	}

	/**
	 * <p>[概 要] </p>
	 * Modelの処理を開始するメソッドです。
	 *
	 * <p>[詳 細] </p>
	 * モデルの処理フローを生成します。
	 * 下記のテンプレートメソッドが順にテンプレートコールされます。<p>
	 *
	 * 	<ol>
	 * 		<li>{@link BaseModel#preproc()}</li>
	 * 		<li>{@link BaseModel#mainproc()}</li>
	 * 		<li>{@link BaseModel#postproc()}</li>
	 * 		<li>{@link BaseModel#finalproc()}</li>
	 * 	</ol>
	 * 上記メソッド内で例外が発生した場合、trapメソッドがテンプレートコールされます。
	 *
	 * <p>[備 考] </p>
	 * 例外が発生した場合、デフォルト動作としてModel処理失敗イベントが発火されますが、
	 * trapオーバーライドメソッドでnullを返却すると、このイベントは発火されません。
	 *
	 */
	public void run() throws Exception{
		try{
			// 前処理。falseを返却した場合は処理中止
			if(preproc()) {
				// 主処理
				mainproc();
				// 後処理
				postproc();
			}else{
				fireModelFinished(new ModelProcessEvent(this));
			}
		}catch(Exception e){
			// 例外ハンドリングメソッド
			e = trap(e);
			// 例外ハンドリングメソッドがnullを返却した場合は例外イベントを発火させない
			if(e != null){
				ModelProcessEvent evt = new ModelProcessEvent(this);
				evt.setException(e);
				fireModelFailure(evt);
			}
		}finally{
			// 最終処理
			finalproc();
		}
	}

	/**
	 * <p>[概 要] </p>
	 * 前処理テンプレートメソッドです。
	 *
	 * <p>[詳 細] </p>
	 * デフォルト処理はtrueを返却します。<p>
	 *
	 * mainprocよりも先に呼ばれるメソッドです。<br>
	 * オーバーライドして、主処理の前に行う初期化を記述します。<br>
	 * nullを返却すると、それ以降のモデル処理は中止されます。<br>
	 *
	 * <p>[備 考] </p>
	 *
	 * @return 以降の処理を継続するかどうかのフラグ（デフォルト：true）
	 * @throws Exception オーバーライド先で発生する可能性が有る例外
	 */
	protected boolean preproc() throws Exception{
		return true;
	}

	/**
	 * <p>[概 要] </p>
	 * 主処理テンプレートメソッドです。
	 *
	 * <p>[詳 細] </p>
	 * デフォルト処理は有りません。<br>
	 * オーバーライドしてこのモデルのメイン処理を実装します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @throws Exception オーバーライド先で発生する可能性が有る例外
	 */
	protected void mainproc() throws Exception{
	}

	/**
	 * <p>[概 要] </p>
	 * 後処理テンプレートメソッドです。
	 *
	 * <p>[詳 細] </p>
	 * デフォルト処理は有りません。<br>
	 * オーバーライドしてこのモデルの主処理後処理を実装します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @throws Exception オーバーライド先で発生する可能性が有る例外
	 */
	protected void postproc() throws Exception{

	}

	/**
	 * <p>[概 要] </p>
	 * {@link BaseModel#run()}内で発生した全例外をハンドリングするメソッドです。
	 *
	 * <p>[詳 細] </p>
	 * デフォルト処理は有りません。<br>
	 * オーバーライドしてこのモデルの例外ハンドリング処理を実装します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param e オーバーライド先で発生する可能性が有る例外
	 * @return 引数eか、trapオーバーライドメソッドで変換された例外
	 */
	protected Exception trap(Exception e){
		return e;
	}

	/**
	 * <p>[概 要] </p>
	 * {@link BaseModel#run()}が終了したタイミングでテンプレートコールされるメソッドです。
	 *
	 * <p>[詳 細] </p>
	 * デフォルト処理は有りません。<br>
	 * オーバーライドしてこのモデルの終了処理を実装します。
	 *
	 * <p>[備 考] </p>
	 *
	 */
	protected void finalproc(){
	}

	/**
	 * <p>[概 要] </p>
	 * 最終処理テンプレートメソッドです。
	 *
	 * <p>[詳 細] </p>
	 * デフォルト処理は有りません。
	 * オーバーライドして接続のcloseやメモリの解放処理を実装します。
	 *
	 * <p>[備 考] </p>
	 * このメソッドは例外を発生させて異常終了したモデル、setSkip(true)されたモデル
	 * に対してもコールされます。
	 *
	 * @throws Exception
	 */
	public void done() throws Exception {
	}

	/**
	 * <p>[概 要] </p>
	 * モデル処理成功イベントを発行します。
	 *
	 * <p>[詳 細] </p>
	 * モデル処理成功回数を1増加させ、このモデルに登録されているモデル処理監視リスナ群
	 * に対して処理が成功したことを通知します。<br>
	 *
	 * <p>[備 考] </p>
	 * 以下のようにModelProcessEventを生成してから使用します。
	 *
	 * <pre class="samplecode">
	 *	// モデル処理イベントインスタンス生成
	 *	ModelProcessEvent evt = new ModelProcessEvent(this);
	 *	evt.setResult(result);
	 *	// 発火
	 *	fireModelSuccess(evt);
	 * </pre>
	 *
	 * @param evt モデル成功処理結果が入ったModelProcessEventインスタンス　
	 */
	public void fireModelSuccess(final ModelProcessEvent evt) {
		incrementSuccessCount();

		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				for(ModelProcessListener listener : listenerList) {
					listener.modelSuccess(evt);
				}
			}
		});
	}

	/**
	 * <p>[概 要] </p>
	 * モデル処理失敗イベントを発行します。
	 *
	 * <p>[詳 細] </p>
	 * このモデルに登録されているモデル処理監視リスナ群に対して処理が失敗したことを通知します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @see BaseModel#fireModelSuccess(ModelProcessEvent)
	 * @param evt モデル失敗処理結果が入ったModelProcessEventインスタンス
	 */
	public void fireModelFailure(final ModelProcessEvent evt) {

		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				for(ModelProcessListener listener : listenerList) {
					listener.modelFailure(evt);
				}
			}
		});
	}

	/**
	 * <p>[概 要] </p>
	 * モデル処理終了イベントを発行します。
	 *
	 * <p>[詳 細] </p>
	 * このモデルに登録されているモデル処理監視リスナ群に対して処理が終了したことを通知します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @see BaseModel#fireModelSuccess(ModelProcessEvent)
	 * @param evt モデル終了処理結果が入ったModelProcessEventインスタンス
	 */
	public void fireModelFinished(final ModelProcessEvent evt) {
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				for(ModelProcessListener listener : listenerList) {
					listener.modelFinished(evt);
				}
			}
		});
	}
}
