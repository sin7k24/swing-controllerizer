package com.oneitthing.swingcontrollerizer.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.util.List;

import javax.swing.JPopupMenu;

import com.oneitthing.swingcontrollerizer.manager.WindowManager;

/**
 * <p>[概 要] </p>
 * Swingコンポーネントを検出するユーティリティクラスです。
 *
 * <p>[詳 細] </p>
 *
 * <p>[備 考] </p>
 *


 *

 */
public class ComponentSearchUtil {

	/**
	 * <p>[概 要] </p>
	 * 引数Componentから引数nameを持つコンポーネントを返却します。
	 *
	 * <p>[詳 細] </p>
	 * componentがコンテナコンポーネントの場合、再帰的に子孫コンポーネントを走査します。
	 * nameを持つコンポーネントが見つかった時点で再帰を中断、コンポーネントを返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param component コンポーネントを探すルート
	 * @param name 探すコンポーネントの名前
	 * @return nameを名前として持つコンポーネント。見つからなかった場合はnull。
	 */
	public static Component searchComponentByName(Component component, String name) {
		Component ret = null;

		if(name.equals(component.getName())) {
			return component;
		}

		if(component instanceof Container) {
			Component[] cs = ((Container)component).getComponents();
			for(Component c : cs) {
				ret = searchComponentByName(c, name);
				if(ret != null) {
					return ret;
				}
			}
		}

		return ret;
	}

	/**
	 * <p>[概 要] </p>
	 * メモリ上に存在する全ウィンドウコンポーネントから引数nameを持つコンポーネントを返却します。
	 *
	 * <p>[詳 細] </p>
	 * WindowManagerが管理している画面コンポーネントリストの中から
	 * nameを名前として持つコンポーネントを探して返却します。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param name 探すコンポーネントの名前
	 * @return nameを名前として持つコンポーネント。見つからなかった場合はnull。
	 */
	public static Component searchComponentByNameFromAllWindow(String name) {
		Component ret = null;

		for(Window window : WindowManager.getInstance().getWindowList()) {
			ret = searchComponentByName(window, name);
			if(ret != null) {
				break;
			}
		}

		return ret;
	}

	/**
	 * <p>[概 要] </p>
	 * 引数Componentから引数nameを持つ全てのコンポーネントを返却します。
	 *
	 * <p>[詳 細] </p>
	 * componentがコンテナコンポーネントの場合、再帰的に子孫コンポーネントを走査します。
	 * nameを持つコンポーネントが見つかった場合でも全ての子孫を走査し終わるまで再帰を続けます。
	 *
	 * <p>[備 考] </p>
	 *
	 * @param list 検出されたコンポーネントが格納されるリスト
	 * @param component コンポーネントを探すルート
	 * @param name 探すコンポーネントの名前
	 */
	public static void searchComponentsByName(List<Component> list, Component component, String name) {

		if(name.equals("*") || name.equals(component.getName())) {
			list.add(component);
		}

		if(component instanceof Container) {
			Component[] cs = ((Container)component).getComponents();
			for(Component c : cs) {
				searchComponentsByName(list, c, name);
			}
		}
	}

	/**
	 * <p>[概 要] </p>
	 * 引数Componentが所属するウィンドウコンポーネントを返却します。
	 *
	 * <p>[詳 細] </p>
	 * componentのparentを辿り、Windowクラス継承オブジェクトを探します。<br>
	 * nullを返すparentまで辿り着いた場合、nullを返却したコンポーネントを返却します。
	 *
	 * <p>[備 考] </p>
	 * componentがJMenuItemのようなJPopupMenuコンポーネントの要素の場合、
	 * 戻り値はJPopupMenuを呼び出したコンポーネントが所属するウィンドウコンポーネント
	 * になります。
	 *
	 * @param component ウィンドウを探す子コンポーネント
	 * @return componentが属するウィンドウコンポーネント
	 */
	public static Window searchWindowLevelObject(Component component) {
		while(!(component instanceof Window) &&
				component.getParent() != null)
		{
			component = component.getParent();
		}

		// parent==nullを返却したのがJPopupMenuの場合、ポップアップ発生元ウィンドウを返却
		if(component instanceof JPopupMenu) {
			component = ((JPopupMenu)component).getInvoker();
			component = searchWindowLevelObject(component);
		}
		return (Window)component;
	}

	/**
	 * <p>[概 要] </p>
	 * 引数Componentが所属するウィンドウコンポーネントを返却します。
	 *
	 * <p>[詳 細] </p>
	 * {@link #searchWindowLevelObject(Component)}に処理委譲します。
	 * <p/>
	 *
	 * Componentの中には、getParent()で返却される親コンポーネントの参照が
	 * イベントディスパッチスレッド間で維持されないものが有ります。<br/>
	 * (TableCellEditorで生成されたセル内ボタン等)<br/>
	 * これらのコンポーネントの親を確実に取る為に第二引数parentが用意されています。<br/>
	 *
	 * <p>[備 考] </p>
	 *
	 * @param component ウィンドウを探す子コンポーネント
	 * @param parent ウィンドウを探す子コンポーネントの親コンポーネント
	 * @return componentが属するウィンドウコンポーネント
	 */
	public static Window searchWindowLevelObject(Component component, Component parent) {
		if(component.getParent() == null && parent != null) {
			if(component instanceof Window) {
				return (Window)component;
			}
			component = parent;
		}

		return searchWindowLevelObject(component);
	}
}
