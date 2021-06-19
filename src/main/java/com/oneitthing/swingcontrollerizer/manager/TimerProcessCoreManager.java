package com.oneitthing.swingcontrollerizer.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.oneitthing.swingcontrollerizer.action.AbstractAction;
import com.oneitthing.swingcontrollerizer.controller.ParameterMapping;
import com.oneitthing.swingcontrollerizer.model.TimerProcessCore;

/**
 * <p>[概 要] </p>
 * TimerProcessCore機能モデルを管理するマネージャクラスです。
 *
 * <p>[詳 細] </p>
 * タイマーを開始したTimerProcessCoreをtimerIdをキーにして管理します。<br>
 * タイマーを停止する際、指定された任意のtimerIdを持つTimerProcessCoreに
 * タイマー停止を命じます。<br>
 *
 *
 * <p>[備 考] </p>
 *
 *


 *

 */
public class TimerProcessCoreManager {

	/** このマネージャのシングルトンインスタンスです。 */
	private static TimerProcessCoreManager instance;

	/** TimerProcessCoreを管理するマップオブジェクトです。 */
	private Map<String, TimerProcessCore> timers = new HashMap<String, TimerProcessCore>();

	/**
	 * <p>[概 要] </p>
	 * TimerProcessCoreを管理するマップオブジェクトを返却します。
	 *
	 * <p>[詳 細] </p>
	 * timersフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return TimerProcessCoreを管理するマップオブジェクト
	 */
	public Map<String, TimerProcessCore> getTimers() {
		return timers;
	}

	/**
	 * <p>[概 要] </p>
	 * TimerProcessCoreを管理するマップオブジェクトを設定します。
	 *
	 * <p>[詳 細] </p>
	 * timersフィールドを引数timersで設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param timers TimerProcessCoreを管理するマップオブジェクト
	 */
	public void setTimers(Map<String, TimerProcessCore> timers) {
		this.timers = timers;
	}

	/**
	 * <p>[概 要] </p>
	 * プライベートコンストラクタです。
	 *
	 * <p>[詳 細] </p>
	 * インスタンス生成には{@link #getInstance()}を使用します。
	 *
	 * <p>[備 考] </p>
	 *
	 */
	private TimerProcessCoreManager() {
	}

	/**
	 * <p>[概 要] </p>
	 * シングルトンインスタンス取得メソッドです。
	 *
	 * <p>[詳 細] </p>
	 * TimerProcessCoreManagerのインスタンスはJVM内でユニークです。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return マネージャのシングルトンインスタンス
	 */
	public static TimerProcessCoreManager getInstance() {
		if(instance == null) {
			instance = new TimerProcessCoreManager();
		}

		return instance;
	}

	/**
	 * <p>[概 要] </p>
	 * 引数timerIdを持つTimerProcessCoreが既に管理されているか調べます。
	 *
	 * <p>[詳 細] </p>
	 * timers管理マップに引数timerIdがキーとして登録されているかどうか
	 * 調べて返却します。<br>
	 * trueの場合、タイマーをstartしているTimerProcessCoreが既に存在します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param timerId タイマーの識別子
	 * @return true : 既登録、false : 未登録
	 */
	public boolean isRegist(String timerId) {
		return getTimers().containsKey(timerId);
	}

	/**
	 * <p>[概 要] </p>
	 * タイマー処理を開始します。
	 *
	 * <p>[詳 細] </p>
	 * 既に同名のtimerIdを持つtimerProcessCoreが存在する場合はfalseを返却します。<br>
	 *
	 * ScheduledThreadPoolExecuterに定期処理を委譲します。<br>
	 * Executerの実行にはTimerProcessCoreに設定されている情報が使用されます。<br>
	 *
	 * <ol>
	 *   <li>初期遅延：{@link TimerProcessCore#getInitialDelay()}</li>
	 *   <li>実行間隔：{@link TimerProcessCore#getPeriod()}</li>
	 *   <li>終了時間：{@link TimerProcessCore#getStopLater()}（0の場合は自動停止無し）</li>
	 *   <li>実行処理：{@link TimerProcessCore#getIntervalAction()} （nullの場合は成功イベント発行のみ）</li>
	 * </ol>
	 *
	 * タイマー処理を開始したTimerProcessCoreは、timerIdをキーにtimersフィールドで管理開始されます。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param timerProcessCore 開始するタイマー情報を持った機能モデルインスタンス
	 * @return true : タイマー開始成功、 false : 失敗
	 */
	public boolean start(final TimerProcessCore timerProcessCore) {
		if(isRegist(timerProcessCore.getTimerId())) {
			return false;
		}

		ScheduledThreadPoolExecutor executorService =
			(ScheduledThreadPoolExecutor)Executors.newScheduledThreadPool(1);
		executorService.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
		executorService.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);

		timerProcessCore.setExecutorService(executorService);

		ScheduledFuture<?> future = executorService.scheduleAtFixedRate(new Runnable(){
			public void run() {
				if(timerProcessCore.getIntervalAction() != null) {
					// タイマー実行用アクションのParameterMappingをクローン生成
					// インタラプトをタイマー中止と見做す
					ParameterMapping mapping = timerProcessCore.getParameterMapping().clone();
					mapping.setAllowInteruptedExceptionOnSyncModel(true);

					// タイマー実行用アクション取得、コントローラに実行を委譲
					Class<?extends AbstractAction> action = timerProcessCore.getIntervalAction();
					timerProcessCore.getController().invoke(action, mapping);
				}else{
					timerProcessCore.onTick();
				}
			}
		},timerProcessCore.getInitialDelay(), timerProcessCore.getPeriod(), TimeUnit.MILLISECONDS);
		timerProcessCore.setFuture(future);
		getTimers().put(timerProcessCore.getTimerId(), timerProcessCore);

		// TimerProcessCore#stopLaterが設定されている場合はタイマ停止用タスクをスケジュール
		if(timerProcessCore.getStopLater() > 0) {
			stop(timerProcessCore.getTimerId(), timerProcessCore.getStopLater());
		}

		return true;
	}

	/**
	 * <p>[概 要] </p>
	 * タイマー処理を停止します。
	 *
	 * <p>[詳 細] </p>
	 * {@link #stop(String, long)}オーバーロードメソッドに処理委譲します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param timerId
	 * @return true : 停止成功、false : 停止失敗
	 */
	public boolean stop(String timerId) {
		return stop(timerId, 0);
	}

	/**
	 * <p>[概 要] </p>
	 * タイマー処理を緩やかに停止します。
	 *
	 * <p>[詳 細] </p>
	 * 引数timerIdを持つTimerProcessCoreに対してタイマー処理の停止を命じます。<br>
	 * timerIdを持つTimerProcessCoreが管理されていない場合はfalseを返却します。<br>
	 * タイマー停止が行われた後、timersフィールドからtimerIdがキーのTimerProcessCoreを削除します。<br>
	 * <p>
	 *
	 * これらの処理が正常に行われた後、タイマー処理を停止したTimerProcessCoreに対して
	 * ModelProcessEvent.FINISHEDイベントを発行され、BaseAction#completeがコールバックされます。<br>
	 *
	 * <p>[備 考] </p>
	 * タイマーがスケジュールしているタスクがまだ存在する場合、それらのタスクが
	 * 終了してから、タイマーが停止します。<br/>
	 * 遅延タスクが存在する場合でも、ただちにタイマーを停止する場合は
	 * {@link #stopImmediately(String)}を使用してください。
	 *
	 * @param timerId タイマーの識別子
	 * @param stopLater タイマーを止めるまでのミリ秒
	 * @return true : 停止成功、false : 停止失敗
	 */
	private boolean stop(final String timerId, long stopLater) {
		if(!isRegist(timerId)) {
			return false;
		}

		final TimerProcessCore timerProcessCore = getTimers().get(timerId);
		timerProcessCore.getExecutorService().schedule(new Runnable() {
			public void run() {
				stopImmediately(timerId);
			}
		}, stopLater, TimeUnit.MILLISECONDS);

		return true;
	}

	/**
	 * <p>[概 要] </p>
	 * タイマー処理をただちに停止します。
	 *
	 * <p>[詳 細] </p>
	 * 引数timerIdを持つTimerProcessCoreに対してタイマー処理の停止を命じます。<br>
	 * timerIdを持つTimerProcessCoreが管理されていない場合はfalseを返却します。<br>
	 * タイマー停止が行われた後、timersフィールドからtimerIdがキーのTimerProcessCoreを削除します。<br>
	 * <p>
	 *
	 * これらの処理が正常に行われた後、タイマー処理を停止したTimerProcessCoreに対して
	 * ModelProcessEvent.FINISHEDイベントを発行され、BaseAction#completeがコールバックされます。<br>
	 *
	 * <p>[備 考] </p>
	 * タイマーがスケジュールしているタスクがまだ存在する場合、それらのタスク実行をキャンセル
	 * して、タイマーが停止します。<br/>
	 * 遅延タスクを全て実行しきってタイマーを停止する場合は
	 * {@link #stop(String)}を使用してください。
	 *
	 * @param timerId タイマーの識別子
	 * @return true : 停止成功、false : 停止失敗
	 */
	public boolean stopImmediately(String timerId) {
		if(!isRegist(timerId)) {
			return false;
		}

		TimerProcessCore timerProcessCore = getTimers().get(timerId);
		timerProcessCore.onStop();

		timerProcessCore.getFuture().cancel(true);
		timerProcessCore.getExecutorService().shutdownNow();
		getTimers().remove(timerId);

		return true;
	}
}
