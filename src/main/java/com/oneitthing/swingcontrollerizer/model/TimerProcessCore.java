package com.oneitthing.swingcontrollerizer.model;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import com.oneitthing.swingcontrollerizer.action.AbstractAction;
import com.oneitthing.swingcontrollerizer.event.ModelProcessEvent;
import com.oneitthing.swingcontrollerizer.manager.TimerProcessCoreManager;

/**
 * <p>[概 要] </p>
 * 設定されたタイマー契機で任意のコード、Actionを実行する機能モデルです。
 *
 * <p>[詳 細] </p>
 * タイマーが実行されるとModelProcessEvent.SUCCESSイベントが発行されます。<br>
 * イベントを受け取ったコントローラのモデル処理監視リスナはBaseController#successForwardをコールします。<br>
 * 定期的に実行するコードを、TimerProcessCoreをリザーブしたBaseAction継承アクションのsuccessForward
 * に実装しておくことで、タイマー契機でコードを繰り返し実行出来るようになります。<br>
 * <p>
 *
 * intervalActionフィールドが設定されていると、ModelProcessEvent.SUCCESSイベントの発行の代わりに
 * intervalActionの実行をコントローラに委譲します。<br>
 * 繰り返し実行されるのはアクションのsuccessForwardでは無く、intervalActionになります。
 * <p>
 *
 * intervalActionの指定有無に関わらず、TimerProcessCoreをリザーブしたアクションのcompleteが
 * コールされるのは、TimerProcessCoreが停止したタイミングです。
 *
 * <p>[備 考] </p>
 *
 * <b>使用例）</b><br>
 * ・ローカルJVMのメモリ遷移を表すJFreeChartグラフを定期的に更新する。
 * <pre class="samplecode">
 * 	package demo.jfreechart.action;
 *
 *	import java.lang.management.ManagementFactory;
 *	import java.lang.management.MemoryPoolMXBean;
 *	import java.util.List;
 *
 *	import com.oneitthing.swingcontrollerizer.action.BaseAction;
 *	import com.oneitthing.swingcontrollerizer.controller.ParameterMapping;
 *	import com.oneitthing.swingcontrollerizer.event.ModelProcessEvent;
 *	import com.oneitthing.swingcontrollerizer.model.Model;
 *	import com.oneitthing.swingcontrollerizer.model.TimerProcessCore;
 *
 *	import org.jfree.data.time.Millisecond;
 *
 *	import demo.jfreechart.TimeSeriesChartIFrame;
 *
 *	public class TimeSeriesChartInitAction extends BaseAction {
 *
 *		MemoryPoolMXBean eden;
 *
 *		MemoryPoolMXBean survivor;
 *
 *		MemoryPoolMXBean tenured;
 *
 *		MemoryPoolMXBean perm;
 *
 *		&#064;Override
 *		protected boolean prepare(ParameterMapping parameterMapping) throws Exception {
 *
 *			// 世代別メモリのMXBeanを取得してフィールドに保存します。
 *			List<MemoryPoolMXBean> memoryPoolMXBeans = ManagementFactory
 *					.getMemoryPoolMXBeans();
 *			for (MemoryPoolMXBean mpbean : memoryPoolMXBeans) {
 *				if("Eden Space".equals(mpbean.getName())) {
 *					this.eden = mpbean;
 *				}else if("Survivor Space".equals(mpbean.getName())) {
 *					this.survivor = mpbean;
 *				}else if("Tenured Gen".equals(mpbean.getName())) {
 *					this.tenured = mpbean;
 *				}else if("Perm Gen".equals(mpbean.getName())) {
 *					this.perm = mpbean;
 *				}
 *			}
 *
 *			return true;
 *		}
 *
 *		&#064;Override
 *		protected void reserveModels(List<Class<? extends Model>> models) {
 *			// タイマー実行機能モデルをリザーブ
 *			models.add(TimerProcessCore.class);
 *		}
 *
 *		&#064;Override
 *		public boolean nextModel(int index, ModelProcessEvent prev, Model next) throws Exception {
 *			if(index == 0) {
 *				// 1秒毎にタイマー実行され、successForwardが定期実行されるよう設定します。
 *				// タイマーをストップさせる為に必要なtimerIdは、画面クラスのハッシュコードにしています。
 *				Object eventSource = getParameterMapping().getEventSource();
 *				((TimerProcessCore)next).setTimerId(String.valueOf(eventSource.hashCode()));
 *				((TimerProcessCore)next).setPeriod(1000);
 *			}
 *
 *			return true;
 *		}
 *
 *		&#064;Override
 *		public void successForward(int index, Model model, Object result) throws Exception {
 *			// 1秒毎にコントローラからコールバックされます。
 *			// JFreeChartの描画データを追加しています。
 *			TimeSeriesChartIFrame iframe = (TimeSeriesChartIFrame) getParameterMapping().getEventSource();
 *
 *			iframe.edenSeries.add(new Millisecond(), eden.getUsage().getUsed());
 *			iframe.survivorSeries.add(new Millisecond(), survivor.getUsage().getUsed());
 *			iframe.tenuredSeries.add(new Millisecond(), tenured.getUsage().getUsed());
 *			iframe.permSeries.add(new Millisecond(), perm.getUsage().getUsed());
 *		}
 *	}
 * </pre>
 *
 * ・サーバ時刻を取得するアクションを定期的に実行する。
 *
 * <pre class="samplecode">
 * 	package demo.timeraction.action;
 *
 *	import java.util.List;
 *
 *	import javax.swing.JLabel;
 *
 *	import com.oneitthing.swingcontrollerizer.action.BaseAction;
 *	import com.oneitthing.swingcontrollerizer.controller.ParameterMapping;
 *	import com.oneitthing.swingcontrollerizer.event.ModelProcessEvent;
 *	import com.oneitthing.swingcontrollerizer.model.HTTPRequestCore;
 *	import com.oneitthing.swingcontrollerizer.model.Model;
 *
 *	// タイマーで定期実行されるアクションです。
 *	public class PeriodicAction extends BaseAction {
 *
 *		&#064;Override
 *		protected boolean prepare(ParameterMapping parameterMapping) throws Exception {
 *			System.out.println("PeriodicAction#prepare");
 *			return true;
 *		}
 *
 *		&#064;Override
 *		protected void reserveModels(List<Class<? extends Model>> models) {
 *			// サーバに時刻を問い合わせる為のHTTPリクエスト機能モデルをリザーブ
 *			models.add(HTTPRequestCore.class);
 *		}
 *
 *
 *		&#064;Override
 *		public boolean nextModel(int index, ModelProcessEvent prev, Model next) throws Exception {
 *			if(index == 0) {
 *				// サーバロジックは任意です（Struts等でも可）。
 *				// PeriodicAccessModelはサーバ側でnew Date()をシリアライズして返却します。
 *				String serverSleepMs = getComponentValueAsString("timerActionFrame.jtfServerSleepMs");
 *
 *				((HTTPRequestCore)next).setRequestUrl("/webcontroller");
 *				((HTTPRequestCore)next).addUrlParamteters("model.fqcn", "demo.server.model.PeriodicAccessModel");
 *				((HTTPRequestCore)next).addUrlParamteters("sleepMs", serverSleepMs);
 *			}
 *			return true;
 *		}
 *
 *		&#064;Override
 *		public void successForward(int index, Model model, Object result) throws Exception {
 *			// 画面にサーバ時刻を表示します。
 *			JLabel jlResult = (JLabel)getComponent("timerActionFrame.jlResult");
 *			jlResult.setText(result.toString());
 *			System.out.println("PeriodicAction#successForward");
 *		}
 *
 *		&#064;Override
 *		public Exception failureForward(int index, Model model, Exception e) {
 *			System.out.println("PeriodicAction#failureForward");
 *			return e;
 *		}
 *
 *		&#064;Override
 *		public void complete() {
 *			System.out.println("PeriodicAction#complete");
 *		}
 *	}
 * </pre>
 *
 * <pre class="samplecode">
 * 	package demo.timeraction.action;
 *
 *	import java.util.List;
 *
 *	import javax.swing.JButton;
 *
 *	import com.oneitthing.swingcontrollerizer.action.BaseAction;
 *	import com.oneitthing.swingcontrollerizer.controller.ParameterMapping;
 *	import com.oneitthing.swingcontrollerizer.event.ModelProcessEvent;
 *	import com.oneitthing.swingcontrollerizer.model.Model;
 *	import com.oneitthing.swingcontrollerizer.model.TimerProcessCore;
 *
 *	// タイマー開始ボタン押下アクションです。
 *	public class TimerActionStartAction extends BaseAction {
 *
 *		&#064;Override
 *		protected boolean prepare(ParameterMapping parameterMapping) throws Exception {
 *
 *			// 開始、停止ボタンの有無効を切り替えます。
 *			JButton jbStart = (JButton)getComponent("timerActionFrame.jbStart");
 *			JButton jbStop = (JButton)getComponent("timerActionFrame.jbStop");
 *
 *			jbStart.setEnabled(false);
 *			jbStop.setEnabled(true);
 *
 *			return true;
 *		}
 *
 *		&#064;Override
 *		protected void reserveModels(List<Class<? extends Model>> models) {
 *			// タイマー開始用機能モデルをリザーブ
 *			models.add(TimerProcessCore.class);
 *		}
 *
 *		&#064;Override
 *		public boolean nextModel(int index, ModelProcessEvent prev, Model next) throws Exception {
 *			if(index == 0) {
 *				// 画面から初期遅延、間隔、自動終了ミリ秒数を取ります。（ここでは自動終了ミリ秒数は0です）
 *				int timerInitDelayMs =
 *					Integer.parseInt(getComponentValueAsString("timerActionFrame.jtfTimerInitDelayMs"));
 *				int timerPeriodMs =
 *					Integer.parseInt(getComponentValueAsString("timerActionFrame.jtfTimerPeriodMs"));
 *				int timerStopLaterMs =
 *					Integer.parseInt(getComponentValueAsString("timerActionFrame.jtfTimerStopLaterMs"));
 *
 *				((TimerProcessCore)next).setTimerId(String.valueOf(getOwnWindow().hashCode()));
 *				((TimerProcessCore)next).setInitialDelay(timerInitDelayMs);
 *				((TimerProcessCore)next).setPeriod(timerPeriodMs);
 *				((TimerProcessCore)next).setStopLater(timerStopLaterMs);
 *				((TimerProcessCore)next).setIntervalAction(PeriodicAction.class);
 *			}
 *
 *			return true;
 *		}
 *
 *		&#064;Override
 *		public void successForward(int index, Model model, Object result) throws Exception {
 *			System.out.println("TimerActionExecuteAction#successForward");
 *		}
 *
 *		&#064;Override
 *		public Exception failureForward(int index, Model model, Exception e) {
 *			System.out.println("TimerActionExecuteAction#failureForward");
 *			return e;
 *		}
 *
 *		&#064;Override
 *		public void complete() {
 *			System.out.println("TimerActionExecuteAction#complete");
 *			JButton jbStart = (JButton)getComponent("timerActionFrame.jbStart");
 *			JButton jbStop = (JButton)getComponent("timerActionFrame.jbStop");
 *
 *			jbStart.setEnabled(true);
 *			jbStop.setEnabled(false);
 *		}
 *	}
 * </pre>
 *
 * <pre class="samplecode">
 * 	package demo.timeraction.action;
 *
 *	import java.util.List;
 *
 *	import com.oneitthing.swingcontrollerizer.action.BaseAction;
 *	import com.oneitthing.swingcontrollerizer.event.ModelProcessEvent;
 *	import com.oneitthing.swingcontrollerizer.model.Model;
 *	import com.oneitthing.swingcontrollerizer.model.TimerProcessCore;
 *
 *	// タイマー停止ボタン押下アクションです。
 *	public class TimerActionStopAction extends BaseAction {
 *
 *		&#064;Override
 *		protected void reserveModels(List<Class<? extends Model>> models) {
 *			// タイマー停止用にリザーブ
 *			models.add(TimerProcessCore.class);
 *		}
 *
 *		&#064;Override
 *		public boolean nextModel(int index, ModelProcessEvent prev, Model next) throws Exception {
 *			if(index == 0) {
 *				// タイマーを開始したtimerIdに対してタイマーストップを依頼します。
 *				((TimerProcessCore)next).setTimerId(String.valueOf(getOwnWindow().hashCode()));
 *				((TimerProcessCore)next).setStop(true);
 *			}
 *
 *			return true;
 *		}
 *
 *		&#064;Override
 *		public void successForward(int index, Model model, Object result) throws Exception {
 *			System.out.println("TimerActionStopAction#successForward");
 *		}
 *
 *		&#064;Override
 *		public Exception failureForward(int index, Model model, Exception e) {
 *			System.out.println("TimerActionStopAction#failureForward");
 *			return e;
 *		}
 *
 *		&#064;Override
 *		public void complete() {
 *			System.out.println("TimerActionStopAction#complete");
 *		}
 *	}
 * </pre>
 *
 * ・開始ボタンを押下して数秒後に停止ボタンを押下した実行結果
 * <pre>
 * 	PeriodicAction#prepare
 *	PeriodicAction#successForward
 *	PeriodicAction#complete
 *		：
 *		：
 *	PeriodicAction#prepare
 *	PeriodicAction#successForward
 *	PeriodicAction#complete
 *	TimerActionExecuteAction#complete
 *	TimerActionStopAction#successForward
 *	TimerActionStopAction#complete
 * </pre>
 *


 *

 */
public class TimerProcessCore extends BaseModel {

	/** タイマーの識別子です。 */
	private String timerId;

	/** 初期遅延ミリ秒数です。 */
	private long initialDelay = 1000;

	/** タイマー実行する間隔ミリ秒数です。 */
	private long period = 3000;

	/** タイマーを終了させるミリ秒数です。 */
	private long stopLater = 0;

	/** タイマー実行するスケジューラサービスです。 */
	private ScheduledExecutorService executorService;

	/** タイマー実行中のタスクです。 */
	private ScheduledFuture<?> future;

	/** タイマーを停止するかどうかのフラグです。 */
	private boolean stop;

	/** タイマーをただちに停止するかどうかのフラグです。 */
	private boolean stopImmediately;

	/** 定期実行するアクションクラスです。 */
	private Class<? extends AbstractAction> intervalAction;


	/**
	 * <p>[概 要] </p>
	 * タイマーの識別子を返却します。
	 *
	 * <p>[詳 細] </p>
	 * timerIdフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return タイマーの識別子
	 */
	public String getTimerId() {
		return timerId;
	}

	/**
	 * <p>[概 要] </p>
	 * タイマーの識別子を設定します。
	 *
	 * <p>[詳 細] </p>
	 * timerIdフィールドを引数timerIdで設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param timerId タイマーの識別子
	 */
	public void setTimerId(String timerId) {
		this.timerId = timerId;
	}

	/**
	 * <p>[概 要] </p>
	 * 初期遅延ミリ秒数を返却します。
	 *
	 * <p>[詳 細] </p>
	 * initialDelayフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return 初期遅延ミリ秒数
	 */
	public long getInitialDelay() {
		return initialDelay;
	}

	/**
	 * <p>[概 要] </p>
	 * 初期遅延ミリ秒数を設定します。
	 *
	 * <p>[詳 細] </p>
	 * initialDelayフィールドを引数initialDelayで設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param initialDelay 初期遅延ミリ秒数
	 */
	public void setInitialDelay(long initialDelay) {
		this.initialDelay = initialDelay;
	}

	/**
	 * <p>[概 要] </p>
	 * タイマー実行する間隔ミリ秒数を返却します。
	 *
	 * <p>[詳 細] </p>
	 * periodフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return タイマー実行する間隔ミリ秒数
	 */
	public long getPeriod() {
		return period;
	}

	/**
	 * <p>[概 要] </p>
	 * タイマー実行する間隔ミリ秒数を設定します。
	 *
	 * <p>[詳 細] </p>
	 * periodフィールドを引数periodで設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param period タイマー実行する間隔ミリ秒数
	 */
	public void setPeriod(long period) {
		this.period = period;
	}

	/**
	 * <p>[概 要] </p>
	 * タイマーを終了させるミリ秒数を返却します。
	 *
	 * <p>[詳 細] </p>
	 * stopLaterフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return タイマーを終了させるミリ秒数
	 */
	public long getStopLater() {
		return stopLater;
	}

	/**
	 * <p>[概 要] </p>
	 * タイマーを終了させるミリ秒数を設定します。
	 *
	 * <p>[詳 細] </p>
	 * stopLaterフィールドを引数stopLaterで設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param stopLater タイマーを終了させるミリ秒数
	 */
	public void setStopLater(long stopLater) {
		this.stopLater = stopLater;
	}

	/**
	 * <p>[概 要] </p>
	 * タイマー実行するスケジューラサービスを返却します。
	 *
	 * <p>[詳 細] </p>
	 * executorServiceフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return タイマー実行するスケジューラサービス
	 */
	public ScheduledExecutorService getExecutorService() {
		return executorService;
	}

	/**
	 * <p>[概 要] </p>
	 * タイマー実行するスケジューラサービスを設定します。
	 *
	 * <p>[詳 細] </p>
	 * executorServiceフィールドを引数executorServiceで設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param executorService タイマー実行するスケジューラサービス
	 */
	public void setExecutorService(ScheduledExecutorService executorService) {
		this.executorService = executorService;
	}

	/**
	 * <p>[概 要] </p>
	 * タイマー実行中のタスクを返却します。
	 *
	 * <p>[詳 細] </p>
	 * futureフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return タイマー実行中のタスク
	 */
	public ScheduledFuture<?> getFuture() {
		return future;
	}

	/**
	 * <p>[概 要] </p>
	 * タイマー実行中のタスクを設定します。
	 *
	 * <p>[詳 細] </p>
	 * futureフィールドを引数futureで設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param future タイマー実行中のタスク
	 */
	public void setFuture(ScheduledFuture<?> future) {
		this.future = future;
	}

	/**
	 * <p>[概 要] </p>
	 * タイマーを停止するかどうかのフラグを返却します。
	 *
	 * <p>[詳 細] </p>
	 * stopフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return タイマーを停止するかどうかのフラグ
	 */
	public boolean isStop() {
		return stop;
	}

	/**
	 * <p>[概 要] </p>
	 * タイマーを停止するかどうかのフラグを設定します。
	 *
	 * <p>[詳 細] </p>
	 * stopフィールドを引数stopで設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param stop タイマーを停止するかどうかのフラグ
	 */
	public void setStop(boolean stop) {
		this.stop = stop;
	}



	public boolean isStopImmediately() {
		return stopImmediately;
	}

	public void setStopImmediately(boolean stopImmediately) {
		this.stopImmediately = stopImmediately;
	}

	/**
	 * <p>[概 要] </p>
	 * 定期実行するアクションクラスを返却します。
	 *
	 * <p>[詳 細] </p>
	 * intervalActionフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return 定期実行するアクションクラス
	 */
	public Class<? extends AbstractAction> getIntervalAction() {
		return intervalAction;
	}

	/**
	 * <p>[概 要] </p>
	 * 定期実行するアクションクラスを設定します。
	 *
	 * <p>[詳 細] </p>
	 * intervalActionフィールドを引数intervalActionで設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param intervalAction 定期実行するアクションクラス
	 */
	public void setIntervalAction(Class<? extends AbstractAction> intervalAction) {
		this.intervalAction = intervalAction;
	}


	/**
	 * <p>[概 要] </p>
	 * TimerProcessCoreManagerにタイマーの開始、停止を委譲します。
	 *
	 * <p>[詳 細] </p>
	 * {@link #isStop()}を判定して開始、停止をManagerに依頼します。<br>
	 * TimerProcessCoreManagerにはgetTimerId()とこの機能モデルのインスタンスが
	 * 引数として渡されます。<br>
	 * Manager側では、渡されたtimerIdを元に開始されたTimerProcessCoreインスタンス
	 * を管理、停止するTimerProcessCoreインスタンスの削除を行います。<br>
	 * <p>
	 *
	 * isStopがtrueの場合は、TimerProcessCoreManager#stopが正常に行われた後、
	 * ModelProcessEvent.SUCCESS、ModelProcessEvent.FINISHEDイベントが発行されます。
	 *
	 * <p>[備 考] </p>
	 *
	 */
	@Override
	protected void mainproc() throws Exception {
		TimerProcessCoreManager manager = TimerProcessCoreManager.getInstance();

		if(!isStop() && !isStopImmediately()) {
			if(!manager.start(this)) {
				// 同名のtimerIdが既登録の場合
				ModelProcessEvent failureEvent = new ModelProcessEvent(this);
				fireModelFailure(failureEvent);

				ModelProcessEvent finishedEvent = new ModelProcessEvent(this);
				fireModelFinished(finishedEvent);
			}
		}else{
			if(isStop()) {
				manager.stop(getTimerId());
			}else if(isStopImmediately()) {
				manager.stopImmediately(getTimerId());
			}

			ModelProcessEvent successEvent = new ModelProcessEvent(this);
			fireModelSuccess(successEvent);

			ModelProcessEvent finishedEvent = new ModelProcessEvent(this);
			fireModelFinished(finishedEvent);
		}
	}

	/**
	 * <p>[概 要] </p>
	 * 指定したタイマー間隔でコールされるメソッドです。
	 *
	 * <p>[詳 細] </p>
	 * ModelProcessEvent.SUCCESSイベントを発行します。<br>
	 * このイベントを受け取ったコントローラのモデル処理監視リスナは、<br>
	 * BaseAction継承アクションのsuccessForwardをコールバックします。
	 *
	 * <p>[備 考] </p>
	 *
	 */
	public final void onTick() {
		postTick();

		ModelProcessEvent successEvent = new ModelProcessEvent(this);
		fireModelSuccess(successEvent);
	}

	/**
	 * <p>[概 要] </p>
	 * タイマーハンドラ拡張用メソッドです。
	 *
	 * <p>[詳 細] </p>
	 * {@link #timerCompleted()}によってテンプレートコールされます。<br>
	 * タイマー処理として共通の処理を実装する場合、このメソッドを
	 * オーバーライドした機能モデルを作成することで、汎用的なコードを
	 * 記述出来ます。
	 *
	 * <p>[備 考] </p>
	 *
	 */
	protected void postTick() {
	}

	/**
	 * <p>[概 要] </p>
	 * タイマー終了時にテンプレートコールされます。
	 *
	 * <p>[詳 細] </p>
	 * {@link #postStop()}をコールしてModelProcessEvent.FINISHED
	 * イベントを発行します。
	 *
	 * <p>[備 考] </p>
	 *
	 */
	public final void onStop() {
		postStop();

		ModelProcessEvent finishedEvent = new ModelProcessEvent(this);
		fireModelFinished(finishedEvent);
	}

	/**
	 * <p>[概 要] </p>
	 * タイマー終了時にテンプレートコールされるオーバーライドメソッドです。
	 *
	 * <p>[詳 細] </p>
	 * デフォルト処理は有りません。
	 *
	 * <p>[備 考] </p>
	 * このメソッドをオーバーライドすることでタイマー終了時の共通処理を記述出来ます。
	 *
	 */
	protected void postStop() {
	}
}