package com.oneitthing.swingcontrollerizer.helper;

import java.awt.Component;
import java.awt.Window;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import com.oneitthing.swingcontrollerizer.action.AbstractAction;
import com.oneitthing.swingcontrollerizer.action.BaseAction;
import com.oneitthing.swingcontrollerizer.controller.BaseController;
import com.oneitthing.swingcontrollerizer.controller.EventBinder;
import com.oneitthing.swingcontrollerizer.manager.WindowManager;
import com.oneitthing.swingcontrollerizer.model.Model;
import com.oneitthing.swingcontrollerizer.util.ComponentSearchUtil;

/**
 * <p>[概 要] </p>
 *
 * <p>[詳 細] </p>
 *
 * <p>[備 考] </p>
 *
 *


 *

 */
public class CorrelationBuilder {

	/**
	 * <p>[概 要] </p>
	 *
	 * <p>[詳 細] </p>
	 *
	 * <p>[備 考] </p>
	 *
	 * @param controller
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public static ClassNode build(BaseController controller) throws InstantiationException, IllegalAccessException, SecurityException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
		ClassNode controllerNode =
			new ClassNode(ClassNode.CONTROLLER_NODE,
					controller.getClass().getPackage().toString(),
					controller.getClass().getSimpleName());

		EventBinder eventBinder = controller.getEventBinder();
		List<Window> windowList = WindowManager.getInstance().getWindowList();
		for(Window window : windowList) {
			ClassNode viewNode =
				new ClassNode(ClassNode.VIEW_NODE, window.getClass().getPackage().toString(), window.getClass().getSimpleName());
			controllerNode.addChild(viewNode);

			List<Component> componentList = new ArrayList<Component>();
			ComponentSearchUtil.searchComponentsByName(componentList, window, "*");
			for(Component c : componentList) {
				String componentName = c.getName();
				if(componentName != null && componentName.length() != 0) {
					if(eventBinder.isEventBinding(componentName)) {
						List<Class<? extends EventListener>> listenerTypes =
							eventBinder.getListenerTypes(componentName);
						for(Class<? extends EventListener> listenerType : listenerTypes) {
							List<String> eventTypes =
								eventBinder.getEventTypes(componentName, listenerType);
							for(String eventType : eventTypes) {
								Class<? extends AbstractAction> actionClass =
									eventBinder.getActionClass(componentName, listenerType, eventType);
								ClassNode actionNode =
									new ClassNode(ClassNode.ACTION_NODE, actionClass.getPackage().toString(), actionClass.getSimpleName());
								viewNode.addChild(actionNode);

								AbstractAction action = actionClass.newInstance();

								Method method = BaseAction.class.getDeclaredMethod("reserveModels", new Class[]{List.class});
								method.setAccessible(true);

								List<Class<? extends Model>> models = new ArrayList<Class<? extends Model>>();
								method.invoke(action, models);
								for(Class<? extends Model> modelClass : models) {
									ClassNode modelNode =
										new ClassNode(ClassNode.MODEL_NODE, modelClass.getPackage().toString(), modelClass.getSimpleName());
									actionNode.addChild(modelNode);
								}
							}
						}
					}
				}
			}
		}

		return controllerNode;
	}
}
