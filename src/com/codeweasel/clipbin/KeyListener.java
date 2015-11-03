package com.codeweasel.clipbin;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class KeyListener implements NativeKeyListener
{
	private Integer fKey;
	private boolean fKeyDown;
	private KeyTriggerable fKeyTriggerable;

	public KeyListener(Integer pKey, KeyTriggerable pKeyTriggerable)
	{
		try
		{
			GlobalScreen.registerNativeHook();
//			if (GlobalScreen.isNativeHookRegistered())
//				GlobalScreen.registerNativeHook();

			Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
			logger.setLevel(Level.WARNING);
		}
		catch (NativeHookException ex)
		{
			System.err.println("There was a problem registering the native hook.");
			System.err.println(ex.getMessage());

			System.exit(1);
		}

		GlobalScreen.addNativeKeyListener(this);

		this.fKey = pKey;
		this.fKeyTriggerable = pKeyTriggerable;
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent e)
	{
		System.out.println("Key Pressed: " + NativeKeyEvent.getKeyText(e.getKeyCode()));

		if (e.getKeyCode() == this.fKey)
		{
			this.fKeyDown = true;
			if (this.fKeyTriggerable != null)
				this.fKeyTriggerable.trigger();
		}
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent e)
	{
		System.out.println("Key Released: " + NativeKeyEvent.getKeyText(e.getKeyCode()));

		if (e.getKeyCode() == this.fKey)
		{
			this.fKeyDown = false;
		}
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent e)
	{
		// not used
	}

	public boolean isKeyDown()
	{
		return this.fKeyDown;
	}
}