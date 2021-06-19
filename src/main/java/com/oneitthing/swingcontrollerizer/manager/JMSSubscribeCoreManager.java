package com.oneitthing.swingcontrollerizer.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.oneitthing.swingcontrollerizer.model.JMSSubscribeCore;

/**
 * <p>[概 要] </p>
 * JMSサーバトピックを購読するJMSSubscribeCoreを管理するクラスです。
 *
 * <p>[詳 細] </p>
 * トピックを購読中のJMSSubscribeCoreを管理して、開始、停止処理を行います。<br/>
 * このクラスを使用することで、任意のタイミングで購読を停止させることが出来ます。<br/>
 *
 * <pre class="samplecode">
 *  &#064;Override
 *	public void successForward(int index, Model model, Object result) throws Exception {
 *		JMSSubscribeCoreManager.getInstance().unsubscribe("トピック名");
 *	}
 * </pre>
 *
 * <p>[備 考] </p>
 *


 *

 */
public class JMSSubscribeCoreManager {

	/**	このマネージャクラスのインスタンスです。 */
	private static JMSSubscribeCoreManager instance;

	/** JMSSubscribeCoreを管理するマップです。 */
	private Map<String, List<JMSSubscribeCore>> subscriberMap = new HashMap<String, List<JMSSubscribeCore>>();


	/**
	 * <p>[概 要] </p>
	 * JMSSubscribeCoreを管理するマップを返却します。
	 *
	 * <p>[詳 細] </p>
	 * subscriberMapフィールドを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return JMSSubscribeCoreを管理するマップ
	 */
	public Map<String, List<JMSSubscribeCore>> getSubscriberMap() {
		return subscriberMap;
	}

	/**
	 * <p>[概 要] </p>
	 * JMSSubscribeCoreを管理するマップを設定します。
	 *
	 * <p>[詳 細] </p>
	 * subscriberMapフィールドを引数subscriberMapで設定します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param subscriberMap JMSSubscribeCoreを管理するマップ
	 */
	public void setSubscriberMap(Map<String, List<JMSSubscribeCore>> subscriberMap) {
		this.subscriberMap = subscriberMap;
	}

	/**
	 * <p>[概 要] </p>
	 * このマネージャクラスのインスタンスを返却します。
	 *
	 * <p>[詳 細] </p>
	 * instanceフィールを返却します。
	 * instanceフィールドがnullの場合、
	 * 新規にJMSSubscriberManagerオブジェクトを生成します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @return このマネージャクラスのシングルトンインスタンス
	 */
	public static JMSSubscribeCoreManager getInstance() {
		if(instance == null) {
			instance = new JMSSubscribeCoreManager();
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
	private JMSSubscribeCoreManager() {
	}

	/**
	 * <p>[概 要] </p>
	 * topicNameで表されるJMS Topicを購読開始します。
	 *
	 * <p>[詳 細] </p>
	 * topicNameトピックを購読開始する処理を行います。
	 * JMSの各種情報、
	 * <ol>
	 *   <li>コネクション</li>
	 *   <li>セッション</li>
	 *   <li>トピック</li>
	 *   <li>メッセージハンドラ</li>
	 * </ol>
	 * はsubscribeCoreに設定されます。
	 * これらの情報はJMSSubscribeCoreがメッセージ受信する為、unsubscribe命令を受けた時に
	 * 購読を中止する為に使用されます。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param topicName 購読を開始するトピック名
	 * @param subscribeCore 購読を行い、JMS接続情報を保持するモデルインスタンス
	 * @throws NamingException
	 * @throws JMSException
	 */
	public void subscribe(String topicName, JMSSubscribeCore subscribeCore) throws NamingException, JMSException {
		List<JMSSubscribeCore> subscribeCoreList = null;

		InitialContext context = new InitialContext(subscribeCore.getEnvironment());
		TopicConnectionFactory tcf =
			(TopicConnectionFactory)context.lookup(subscribeCore.getConnectionFactoryName());

		// TopicConnectionを作成してJMSSubscribeCoreに保存
		TopicConnection topicConnection = tcf.createTopicConnection();
		if(subscribeCore.getClientId() != null) {
			topicConnection.setClientID(subscribeCore.getClientId());
		}
		subscribeCore.setTopicConnection(topicConnection);

		// TopicConnectionからTopicSessionを作成してJMSSubscribeCoreに保存
		TopicSession topicSession = topicConnection.createTopicSession(false, TopicSession.AUTO_ACKNOWLEDGE);
		subscribeCore.setTopicSession(topicSession);

		// TopicをJNDI経由で取得してJMSSubscribeCoreに保存
		Topic topic = (Topic)context.lookup(topicName);
		subscribeCore.setTopic(topic);

		// TopicSessionからtopicを購読するTopicSubscriberを作成してJMSSubscribeCoreに保存
		TopicSubscriber subscriber = null;
		if(subscribeCore.isDurable()) {
			subscriber =
				topicSession.createDurableSubscriber(topic, subscribeCore.getDurableSubscriberName());
		}else{
			subscriber = topicSession.createSubscriber(topic);

		}
		subscribeCore.setSubscriber(subscriber);
		subscriber.setMessageListener(subscribeCore);


		// subscribeを開始したJMSSubscribeCoreをMap管理開始
		if(!getSubscriberMap().containsKey(topicName)) {
			subscribeCoreList = new ArrayList<JMSSubscribeCore>();
			getSubscriberMap().put(topicName, subscribeCoreList);
		}else{
			subscribeCoreList = getSubscriberMap().get(topicName);
		}
		subscribeCoreList.add(subscribeCore);

		// サーバTopicと通信開始
		topicConnection.start();
	}

	/**
	 * <p>[概 要] </p>
	 * topicNameで表されるJMS Topicを購読中の機能モデル全てに購読停止を命令します。
	 *
	 * <p>[詳 細] </p>
	 * {@link #unsubscribe(String, String)}
	 * メソッドに処理委譲します。
	 *
	 * <p>[備 考] </p>
	 *
	 *
	 * 「topic/chatTopic」トピックを購読中の全JMSSubscribeCoreに購読中止を命令します。
	 * <pre>
	 * JMSSubscriberManager.getInstance().unsubscribe("topic/chatTopic");
	 * </pre>
	 *
	 * @param topicName 購読を停止するトピック名
	 * @throws JMSException
	 */
	public void unsubscribe(String topicName) throws JMSException {
		unsubscribe(topicName, null);
	}

	/**
	 * <p>[概 要] </p>
	 * topicNameで表されるJMS Topicを購読中の任意の機能モデルに購読停止を命令します。
	 *
	 * <p>[詳 細] </p>
	 * topicNameトピックを購読中の全機能モデルの内、引数identifierを識別子として持つ
	 * JMSSubscribeCoreに購読停止を命令します。
	 * <p>
	 *
	 * topicNameトピックを購読中のJMSSubscribeCoreが存在しない場合は処理は行いません。<br>
	 * identifierがnullの場合、topicNameトピックを購読中の全JMSSubscribeCoreに購読を停止させます。<br>
	 * 購読停止処理の結果、topicNameを購読中のJMSSubscribeCoreが無くなった場合、topicNameキーを
	 * subscribeMapフィールドから削除して管理を終了します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param topicName 購読を停止するトピック名
	 * @param identifier 購読を停止させる機能モデルの識別子
	 * @throws JMSException
	 */
	public void unsubscribe(String topicName, String identifier) throws JMSException {
		Map<String, List<JMSSubscribeCore>> subscriberMap = getSubscriberMap();

		// topicNameを購読しているJMSSubscribeCoreリストが無い場合は無処理
		if(!subscriberMap.containsKey(topicName)) {
			return;
		}

		// topicNameを購読しているJMSSubscribeCoreリスト
		List<JMSSubscribeCore> subscribeCoreList = subscriberMap.get(topicName);

		// identifierが指定されていない場合は全購読を中止
		if(identifier == null) {
			for(Iterator<JMSSubscribeCore> it = subscribeCoreList.iterator(); it.hasNext();) {
				JMSSubscribeCore subscribeCore = it.next();
				stop(subscribeCore);
				it.remove();
			}
		}else{
			for(Iterator<JMSSubscribeCore> it = subscribeCoreList.iterator(); it.hasNext();) {
				JMSSubscribeCore subscribeCore = it.next();
				if(identifier.equals(subscribeCore.getIdentifier())) {
					stop(subscribeCore);
					it.remove();
				}
			}
		}

		// unsubscribe処理の結果、topicNameの購読が無くなった場合、topicName登録を削除
		if(subscribeCoreList.size() == 0) {
			subscriberMap.remove(topicName);
		}
	}

	/**
	 * <p>[概 要] </p>
	 * 引数subscribeCoreに対して購読を停止させます。
	 *
	 * <p>[詳 細] </p>
	 * subscribeCoreに保持されている
	 *
	 * <ol>
	 *   <li>TopicConnectionをstop</li>
	 *   <li>TopicSessionをclose</li>
	 *   <li>TopicConnectionをclose</li>
	 * </ol>
	 * させます。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param subscribeCore トピックを購読中のJMSSubscribeCoreインスタンス
	 * @throws JMSException
	 */
	protected void stop(JMSSubscribeCore subscribeCore) throws JMSException {
		subscribeCore.onUnsubscribe();

		subscribeCore.getSubscriber().close();
		subscribeCore.getTopicConnection().stop();
		if(subscribeCore.isDurable()) {
			subscribeCore.getTopicSession().unsubscribe(subscribeCore.getDurableSubscriberName());
		}
		subscribeCore.getTopicSession().close();
		subscribeCore.getTopicConnection().close();
	}
}
