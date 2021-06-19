package com.oneitthing.swingcontrollerizer.action;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.oneitthing.swingcontrollerizer.controller.ParameterMapping;
import com.oneitthing.swingcontrollerizer.model.Model;

/**
 * <p>[概 要] </p>
 * モデルの処理結果（成功、失敗）を任意の実装メソッドに振り分けるアクションクラスです。
 * 
 * <p>[詳 細] </p>
 * BaseAction実装アクションでは、登録されたモデルの処理結果をsuccessForward、
 * failureForwardメソッドの二つの固定メソッドコールバックで取得しますが、<br>
 * DispatchAction実装アクションでは、任意のメソッドをコールバックメソッドに
 * 指定します。<p>
 * 
 * BaseAction実装とはモデルの予約方法が異なります。DispatchActionを実装したアクションでは、
 * reserveModelsの代わりに<br>
 * {@link #reserveModelsAndCallbackMethod(com.oneitthing.swingcontrollerizer.action.DispatchAction.CallbackMapping)}
 * を実装します。
 * <pre class="samplecode">
 * 	&#064;Override
 *	protected void reserveModelsAndCallbackMethod(CallbackMapping callbackMapping) {
 *		// HTTPRequestCore機能モデルが成功した場合successFetchDataメソッドが、失敗した場合filureFetchDataメソッドが呼ばれる
 *		callbackMapping.add(HTTPRequestCore.class, "successFetchData", "failureFetchData");
 *	}
 * </pre>
 *
 * reserveModelsAndCallbackMethodで予約したコールバックメソッドを、以下のシグネチャで実装します。
 * <pre class="samplecode">
 *	// モデル処理正常終了時にコールバックされる
 *	public void successFetchData(Model model, Object result) {
 *		List<String> wardList = (List<String>)result;
 *		ObjectDataSource wardListDataSource = getObjectDataSourceById("wardListDataSource");
 *		wardListDataSource.setSource(wardList);
 *	}
 *
 *	// モデル処理異常終了時にコールバックされる
 *	public Exception failureFetchData(Model model, Exception e) {
 *		return e;
 *	}
 * </pre>
 *
 * 
 * <p>[備 考] </p>
 *


 * 
 
 */
public class DispatchAction extends BaseAction {

	// モデルクラスを示す定数です。
	public static final String CLASS = "class";
	
	// モデル処理成功時コールバックメソッド名を示す定数です。
	public static final String SUCCESS = "success";
	
	// モデル処理失敗時コールバックメソッド名を示す定数です。
	public static final String FAILURE = "failure";
	
	// 実行モデルクラス、成功、失敗コールバックメソッド名をマッピングするオブジェクトです。
	private CallbackMapping callbackMapping;

	/**
	 * <p>[概 要] </p>
	 * コントローラから呼び出されるアクションの基幹メソッドです。
	 *  
	 * <p>[詳 細] </p>
	 * {@link BaseAction#run(ParameterMapping)}処理を行った後、
	 * {@link #reserveModelsAndCallbackMethod(com.oneitthing.swingcontrollerizer.action.DispatchAction.CallbackMapping)}
	 * をテンプレートコールします。<br>
	 * 上記メソッドの実装によって予約されたモデル群は、BaseAction同様BaseControllerに譲渡、実行されます。<br>
	 *  
	 * <p>[備 考] </p>
	 * 
	 * @param parameterMapping MVC各レイヤを伝播するパラメータオブジェクト
	 */
	@Override
	@SuppressWarnings("unchecked")
	public ParameterMapping run(ParameterMapping parameterMapping) throws Exception{
		if(super.run(parameterMapping) == null) return null;
		
		
		// モデル、コールバックメソッド予約をテンプレートメソッドから取得
		this.callbackMapping = new CallbackMapping();
		reserveModelsAndCallbackMethod(callbackMapping);

		// 取得した予約モデルクラス群をparameterMappingに設定
		List<Class<? extends Model>> modelClasses = 
			new ArrayList<Class<? extends Model>>();
		for(int ii=0; ii<callbackMapping.size(); ii++) {
			Class<? extends Model> model = 
				(Class<? extends Model>)callbackMapping.get(ii).get(CLASS);
			modelClasses.add(model);
		}
		parameterMapping.setModelClasses(modelClasses);

		return parameterMapping;
	}

	/**
	 * <p>[概 要] </p>
	 * コントローラに実行させるモデルクラス群と、結果取得するメソッドを予約するメソッドです。
	 * 
	 * <p>[詳 細] </p>
	 * 
	 * <p>[備 考] </p>
	 * 
	 * @param callbackMapping 予約モデルとコールバックメソッド名を格納するオブジェクト
	 */
	protected void reserveModelsAndCallbackMethod(CallbackMapping callbackMapping) {
	}
	
	/**
	 * <p>[概 要] </p>
	 * オーバーライド不可です。
	 * 
	 * <p>[詳 細] </p>
	 * 
	 * <p>[備 考] </p>
	 * 
	 * @param model 予約モデル群
	 */
	@Override
	protected final void reserveModels(List<Class<? extends Model>> models) {
	}

	/**
	 * <p>[概 要] </p>
	 * オーバーライド不可です。
	 * 
	 * <p>[詳 細] </p>
	 * 
	 * <p>[備 考] </p>
	 * 
	 * @param index 結果を返却したモデルの実行インデックス
	 * @param model 結果を返却したモデルインスタンス
	 * @param result モデル処理結果オブジェクト
	 */
	@Override
	public final void successForward(int index, Model model, Object result) throws Exception {
		String successMethodName = (String)callbackMapping.get(index).get(SUCCESS);
		if(successMethodName == null) return;
		
		Class<?>[] type = new Class[2];
		type[0] = Model.class;
		type[1] = Object.class;
		Method method = this.getClass().getMethod(successMethodName, type);
		method.invoke(this, model, result);
	}

	/**
	 * <p>[概 要] </p>
	 * オーバーライド不可です。
	 * 
	 * <p>[詳 細] </p>
	 * 
	 * <p>[備 考] </p>
	 * 
	 * @param index 結果を返却したモデルの実行インデックス
	 * @param model 結果を返却したモデルインスタンス
	 * @param e モデル異常終了時の例外オブジェクト
	 * @return 引数e、若しくはオーバーライドメソッドで変換された例外
	 */
	@Override
	public final Exception failureForward(int index, Model model, Exception e) {
		String failureMethodName = (String)callbackMapping.get(index).get(FAILURE);
		if(failureMethodName == null) return e;
		
		try{
			Class<?>[] type = new Class[2];
			type[0] = Model.class;
			type[1] = Exception.class;
			Method method = this.getClass().getMethod(failureMethodName, type);
			method.invoke(this, model, e);
		}catch(NoSuchMethodException ex){
			ex.printStackTrace();
		}catch(IllegalAccessException ex){
			ex.printStackTrace();
		}catch(InvocationTargetException ex) {
			ex.printStackTrace();
		}
		
		return e;
	}

	/**
	 * <p>[概 要] </p>
	 * 予約モデルとコールバックメソッド名を格納するクラスです。
	 * 
	 * <p>[詳 細] </p>
	 * 
	 * <p>[備 考] </p>
	 *
	 */
	public class CallbackMapping {
		private List<Map<Object, Object>> list;
		
		/**
		 * <p>[概 要] </p>
		 * 
		 * <p>[詳 細] </p>
		 * 
		 * <p>[備 考] </p>
		 * 
		 */
		public CallbackMapping() {
			this.list = new ArrayList<Map<Object, Object>>();
		}
		
		/**
		 * <p>[概 要] </p>
		 * 
		 * <p>[詳 細] </p>
		 * 
		 * <p>[備 考] </p>
		 * 
		 * @param model
		 * @param successMethodName
		 * @param failureMethodName
		 */
		public void add(Class<? extends Model> model, 
							  String successMethodName, 
							  String failureMethodName) 
		{
			Map<Object, Object> map = new HashMap<Object, Object>();
			map.put(CLASS, model);
			map.put(SUCCESS, successMethodName);
			map.put(FAILURE, failureMethodName);
			
			this.list.add(map);
		}
		
		/**
		 * <p>[概 要] </p>
		 * 
		 * <p>[詳 細] </p>
		 * 
		 * <p>[備 考] </p>
		 * 
		 * @return
		 */
		public int size() {
			return this.list.size();
		}
		
		/**
		 * <p>[概 要] </p>
		 * 
		 * <p>[詳 細] </p>
		 * 
		 * <p>[備 考] </p>
		 * 
		 * @param index
		 * @return
		 */
		public Map<Object, Object> get(int index) {
			return this.list.get(index);
		}
	}
}
