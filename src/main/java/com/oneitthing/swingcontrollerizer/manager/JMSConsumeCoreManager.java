package com.oneitthing.swingcontrollerizer.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.oneitthing.swingcontrollerizer.model.JMSConsumeCore;

/**
 * <p>[概 要] </p>
 * JMSサーバキューを受信するJMSConsumeCoreを管理するクラスです。
 *
 * <p>[詳 細] </p>
 * キューを受信中のJMSConsumeCoreを管理して、開始、停止処理を行います。<br/>
 * このクラスを使用することで、任意のタイミングで受信を停止させることが出来ます。<br/>
 *
 * <pre class="samplecode">
 *  &#064;Override
 *	public void successForward(int index, Model model, Object result) throws Exception {
 *		JMSConsumeCoreManager.getInstance().unconsume("キュー名");
 *	}
 * </pre>
 *
 * <p>[備 考] </p>
 *
 *


 *

 */
public class JMSConsumeCoreManager {

	/**	このマネージャクラスのインスタンスです。 */
	private static JMSConsumeCoreManager instance;

	/** JMSConsumeCoreを管理するマップです。 */
	private Map<String, List<JMSConsumeCore>> consumerMap = new HashMap<String, List<JMSConsumeCore>>();


	/**
	 * <p>[概 要] </p>
	 * JMSConsumeCoreを管理するマップを返却します。
	 *
	 * <p>[詳 細] </p>
	 * consumerMapフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return JMSConsumeCoreを管理するマップ
	 */
	public Map<String, List<JMSConsumeCore>> getSubscriberMap() {
		return consumerMap;
	}

	/**
	 * <p>[概 要] </p>
	 * JMSConsumeCoreを管理するマップを設定します。
	 *
	 * <p>[詳 細] </p>
	 * consumerMapフィールドを引数consumerMapで設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param consumerMap JMSConsumeCoreを管理するマップ
	 */
	public void setSubscriberMap(Map<String, List<JMSConsumeCore>> consumerMap) {
		this.consumerMap = consumerMap;
	}

	/**
	 * <p>[概 要] </p>
	 * このマネージャクラスのインスタンスを返却します。
	 *
	 * <p>[詳 細] </p>
	 * instanceフィールを返却します。
	 * instanceフィールドがnullの場合、
	 * 新規にJMSConsumerManagerオブジェクトを生成します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return このマネージャクラスのシングルトンインスタンス
	 */
	public static JMSConsumeCoreManager getInstance() {
		if(instance == null) {
			instance = new JMSConsumeCoreManager();
		}
		return instance;
	}

	/**
	 * <p>[概 要] </p>
	 * プライベートコンストラクタです。
	 *
	 * <p>[詳 細] </p>
	 * このクラスオブジェクトはシングルトンです。
	 * {@link #getInstance()}を使用してインスタンス生成します。
	 *
	 * <p>[備 考] </p>
	 *
	 */
	private JMSConsumeCoreManager() {
	}

	/**
	 * <p>[概 要] </p>
	 * queueNameで表されるJMS Queueを購読開始します。
	 *
	 * <p>[詳 細] </p>
	 * queueNameキューを購読開始する処理を行います。
	 * JMSの各種情報、
	 * <ol>
	 *   <li>コネクション</li>
	 *   <li>セッション</li>
	 *   <li>キュー</li>
	 *   <li>メッセージハンドラ</li>
	 * </ol>
	 * はconsumeCoreに設定されます。
	 * これらの情報はJMSConsumeCoreがメッセージ受信する為、unconsume命令を受けた時に
	 * 購読を中止する為に使用されます。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param queueName 購読を開始するキュー名
	 * @param consumeCore 購読を行い、JMS接続情報を保持するモデルインスタンス
	 * @throws NamingException
	 * @throws JMSException
	 */
	public void consume(String queueName, JMSConsumeCore consumeCore) throws NamingException, JMSException {
		List<JMSConsumeCore> consumeCoreList = null;

		InitialContext context = new InitialContext(consumeCore.getEnvironment());
		QueueConnectionFactory tcf =
			(QueueConnectionFactory)context.lookup(consumeCore.getConnectionFactoryName());

		// QueueConnectionを作成してJMSConsumeCoreに保存
		QueueConnection queueConnection = tcf.createQueueConnection();
		if(consumeCore.getClientId() != null) {
			queueConnection.setClientID(consumeCore.getClientId());
		}
		consumeCore.setQueueConnection(queueConnection);

		// QueueConnectionからQueueSessionを作成してJMSConsumeCoreに保存
		QueueSession queueSession = queueConnection.createQueueSession(false, QueueSession.AUTO_ACKNOWLEDGE);
		consumeCore.setQueueSession(queueSession);

		// QueueをJNDI経由で取得してJMSConsumeCoreに保存
		Queue queue = (Queue)context.lookup(queueName);
		consumeCore.setQueue(queue);

		// QueueSessionからqueueを購読するQueueSubscriberを作成してJMSConsumeCoreに保存
		MessageConsumer consumer = queueSession.createConsumer(queue);
		consumeCore.setConsumer(consumer);
		consumer.setMessageListener(consumeCore);


		// consumeを開始したJMSConsumeCoreをMap管理開始
		if(!getSubscriberMap().containsKey(queueName)) {
			consumeCoreList = new ArrayList<JMSConsumeCore>();
			getSubscriberMap().put(queueName, consumeCoreList);
		}else{
			consumeCoreList = getSubscriberMap().get(queueName);
		}
		consumeCoreList.add(consumeCore);

		// サーバQueueと通信開始
		queueConnection.start();
	}

	/**
	 * <p>[概 要] </p>
	 * queueNameで表されるJMS Queueを購読中の機能モデル全てに購読停止を命令します。
	 *
	 * <p>[詳 細] </p>
	 * {@link #unconsume(String, String)}
	 * メソッドに処理委譲します。
	 *
	 * <p>[備 考] </p>
	 *
	 *
	 * 「queue/chatQueue」キューを購読中の全JMSConsumeCoreに購読中止を命令します。
	 * <pre>
	 * JMSConsumerManager.getInstance().unconsume("queue/chatQueue");
	 * </pre>
	 *
	 * @param queueName 購読を停止するキュー名
	 * @throws JMSException
	 */
	public void unconsume(String queueName) throws JMSException {
		unconsume(queueName, null);
	}

	/**
	 * <p>[概 要] </p>
	 * queueNameで表されるJMS Queueを購読中の任意の機能モデルに購読停止を命令します。
	 *
	 * <p>[詳 細] </p>
	 * queueNameキューを購読中の全機能モデルの内、引数identifierを識別子として持つ
	 * JMSConsumeCoreに購読停止を命令します。
	 * <p>
	 *
	 * queueNameキューを購読中のJMSConsumeCoreが存在しない場合は処理は行いません。<br>
	 * identifierがnullの場合、queueNameキューを購読中の全JMSConsumeCoreに購読を停止させます。<br>
	 * 購読停止処理の結果、queueNameを購読中のJMSConsumeCoreが無くなった場合、queueNameキーを
	 * consumeMapフィールドから削除して管理を終了します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param queueName 購読を停止するキュー名
	 * @param identifier 購読を停止させる機能モデルの識別子
	 * @throws JMSException
	 */
	public void unconsume(String queueName, String identifier) throws JMSException {
		Map<String, List<JMSConsumeCore>> consumerMap = getSubscriberMap();

		// queueNameを購読しているJMSConsumeCoreリストが無い場合は無処理
		if(!consumerMap.containsKey(queueName)) {
			return;
		}

		// queueNameを購読しているJMSConsumeCoreリスト
		List<JMSConsumeCore> consumeCoreList = consumerMap.get(queueName);

		// identifierが指定されていない場合は全購読を中止
		if(identifier == null) {
			for(Iterator<JMSConsumeCore> it = consumeCoreList.iterator(); it.hasNext();) {
				JMSConsumeCore consumeCore = it.next();
				stop(consumeCore);
				it.remove();
			}
		}else{
			for(Iterator<JMSConsumeCore> it = consumeCoreList.iterator(); it.hasNext();) {
				JMSConsumeCore consumeCore = it.next();
				if(identifier.equals(consumeCore.getIdentifier())) {
					stop(consumeCore);
					it.remove();
				}
			}
		}

		// unconsume処理の結果、queueNameの購読が無くなった場合、queueName登録を削除
		if(consumeCoreList.size() == 0) {
			consumerMap.remove(queueName);
		}
	}

	/**
	 * <p>[概 要] </p>
	 * 引数consumeCoreに対して購読を停止させます。
	 *
	 * <p>[詳 細] </p>
	 * consumeCoreに保持されている
	 *
	 * <ol>
	 *   <li>QueueConnectionをstop</li>
	 *   <li>QueueSessionをclose</li>
	 *   <li>QueueConnectionをclose</li>
	 * </ol>
	 * させます。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param consumeCore キューを購読中のJMSConsumeCoreインスタンス
	 * @throws JMSException
	 */
	protected void stop(JMSConsumeCore consumeCore) throws JMSException {
		consumeCore.onUnconsume();

		consumeCore.getQueueConnection().stop();
		consumeCore.getQueueSession().close();
		consumeCore.getQueueConnection().close();
	}
}
