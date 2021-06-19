package com.oneitthing.swingcontrollerizer.controller;

import java.awt.Color;
import java.util.Hashtable;

public class ClientConfig {

	/**  */
	private Hashtable<String, String> defaultJmsEnvironment;

	/**  */
	private Hashtable<String, String> defaultEjbEnvironment;

	/**  */
	private Hashtable<String, String> defaultDatabaseEnvironment;

	/**  */
	private Hashtable<String, String> defaultHttpEnvironment;

	/**  */
	private boolean autoWindowDispose = true;

	/**  */
	private boolean showErrorDialogOnExceptionTrap = true;

	private boolean printStackTraceOnExceptionTrap = true;

	/**  */
	private String unexpectedErrorDialogTitle = "Error";

	/**  */
	private String unexpectedErrorDialogMessage = "unexpected error occured";

	/**  */
	private boolean enableValidationFaultProcessing = true;

	/**  */
	private Color componentColorOnValidationFault = Color.YELLOW;

	/**  */
	private boolean componentColorChangeOnValidationFault = true;

	/**  */
	private boolean componentTipChangeOnValidationFault = true;

	private boolean duplicateActionInvoke = false;



	/**
	 *
	 * @return
	 */
	public Hashtable<String, String> getDefaultJmsEnvironment() {
		return defaultJmsEnvironment;
	}

	/**
	 *
	 * @param defaultJmsEnvironment
	 */
	public void setDefaultJmsEnvironment(Hashtable<String, String> defaultJmsEnvironment) {
		this.defaultJmsEnvironment = defaultJmsEnvironment;
	}

	/**
	 *
	 * @return
	 */
	public Hashtable<String, String> getDefaultEjbEnvironment() {
		return defaultEjbEnvironment;
	}

	/**
	 *
	 * @param defaultEjbEnvironment
	 */
	public void setDefaultEjbEnvironment(Hashtable<String, String> defaultEjbEnvironment) {
		this.defaultEjbEnvironment = defaultEjbEnvironment;
	}

	/**
	 *
	 * @return
	 */
	public Hashtable<String, String> getDefaultDatabaseEnvironment() {
		return defaultDatabaseEnvironment;
	}

	/**
	 *
	 * @param defaultDatabaseEnvironment
	 */
	public void setDefaultDatabaseEnvironment(
			Hashtable<String, String> defaultDatabaseEnvironment) {
		this.defaultDatabaseEnvironment = defaultDatabaseEnvironment;
	}

	/**
	 *
	 * @return
	 */
	public Hashtable<String, String> getDefaultHttpEnvironment() {
		return defaultHttpEnvironment;
	}

	/**
	 *
	 * @param defaultHttpEnvironment
	 */
	public void setDefaultHttpEnvironment(
			Hashtable<String, String> defaultHttpEnvironment) {
		this.defaultHttpEnvironment = defaultHttpEnvironment;
	}

	/**
	 *
	 * @return
	 */
	public boolean isAutoWindowDispose() {
		return autoWindowDispose;
	}

	/**
	 *
	 * @param autoWindowDispose
	 */
	public void setAutoWindowDispose(boolean autoWindowDispose) {
		this.autoWindowDispose = autoWindowDispose;
	}

	/**
	 *
	 * @return
	 */
	public boolean isShowErrorDialogOnExceptionTrap() {
		return showErrorDialogOnExceptionTrap;
	}

	/**
	 *
	 * @param showErrorDialogOnExceptionTrap
	 */
	public void setShowErrorDialogOnExceptionTrap(
			boolean showErrorDialogOnExceptionTrap) {
		this.showErrorDialogOnExceptionTrap = showErrorDialogOnExceptionTrap;
	}

	/**
	 *
	 * @return
	 */
	public boolean isPrintStackTraceOnExceptionTrap() {
		return printStackTraceOnExceptionTrap;
	}

	/**
	 *
	 * @param printStackTraceOnExceptionTrap
	 */
	public void setPrintStackTraceOnExceptionTrap(
			boolean printStackTraceOnExceptionTrap) {
		this.printStackTraceOnExceptionTrap = printStackTraceOnExceptionTrap;
	}

	/**
	 *
	 * @return
	 */
	public String getUnexpectedErrorDialogTitle() {
		return unexpectedErrorDialogTitle;
	}

	/**
	 *
	 * @param unexpectedErrorDialogTitle
	 */
	public void setUnexpectedErrorDialogTitle(String unexpectedErrorDialogTitle) {
		this.unexpectedErrorDialogTitle = unexpectedErrorDialogTitle;
	}

	/**
	 *
	 * @return
	 */
	public String getUnexpectedErrorDialogMessage() {
		return unexpectedErrorDialogMessage;
	}

	/**
	 *
	 * @param unexpectedErrorDialogMessage
	 */
	public void setUnexpectedErrorDialogMessage(String unexpectedErrorDialogMessage) {
		this.unexpectedErrorDialogMessage = unexpectedErrorDialogMessage;
	}

	/**
	 *
	 * @return
	 */
	public Color getComponentColorOnValidationFault() {
		return componentColorOnValidationFault;
	}

	/**
	 *
	 * @param componentColorOnValidationFault
	 */
	public void setComponentColorOnValidationFault(
			Color componentColorOnValidationFault) {
		this.componentColorOnValidationFault = componentColorOnValidationFault;
	}

	/**
	 *
	 * @return
	 */
	public boolean isEnableValidationFaultProcessing() {
		return enableValidationFaultProcessing;
	}

	/**
	 *
	 * @param enableValidationFaultProcessing
	 */
	public void setEnableValidationFaultProcessing(
			boolean enableValidationFaultProcessing) {
		this.enableValidationFaultProcessing = enableValidationFaultProcessing;
	}

	/**
	 *
	 * @return
	 */
	public boolean isComponentColorChangeOnValidationFault() {
		return componentColorChangeOnValidationFault;
	}

	/**
	 *
	 * @param componentColorChangeOnValidationFault
	 */
	public void setComponentColorChangeOnValidationFault(
			boolean componentColorChangeOnValidationFault) {
		this.componentColorChangeOnValidationFault = componentColorChangeOnValidationFault;
	}

	/**
	 *
	 * @return
	 */
	public boolean isComponentTipChangeOnValidationFault() {
		return componentTipChangeOnValidationFault;
	}

	/**
	 *
	 * @param componentTipChangeOnValidationFault
	 */
	public void setComponentTipChangeOnValidationFault(
			boolean componentTipChangeOnValidationFault) {
		this.componentTipChangeOnValidationFault = componentTipChangeOnValidationFault;
	}

	/**
	 *
	 * @return
	 */
	public boolean isDuplicateActionInvoke() {
		return duplicateActionInvoke;
	}

	/**
	 *
	 * @param duplicateActionInvoke
	 */
	public void setDuplicateActionInvoke(boolean duplicateActionInvoke) {
		this.duplicateActionInvoke = duplicateActionInvoke;
	}
}
