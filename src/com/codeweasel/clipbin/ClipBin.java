package com.codeweasel.clipbin;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.widgets.Display;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;

public class ClipBin implements ClipboardOwner
{
	public static final String CLIPS_PATH = "./Clips/";

	public ClipBin()
	{
		// TODO: read a properties file to set up

		// make sure clipsFolder exists
		File clipsFolder = new File(CLIPS_PATH);
		if (!clipsFolder.exists())
			clipsFolder.mkdir();

		// read files in CLIPS_PATH into Clip objects
		List<Clip> clipList = readInClips(clipsFolder);

		Display display = new Display();
		ClipWindow2 clipWindow = new ClipWindow2(display, clipList);

		// COPY Key Combo
		Set<Integer> copyKeys = new HashSet<Integer>();
		copyKeys.add(NativeKeyEvent.VC_CONTROL_L);
		copyKeys.add(NativeKeyEvent.VC_C);
		KeyComboListener copyKeyComboListener = new KeyComboListener(copyKeys, new KeyTriggerable()
		{
			@Override
			public void trigger()
			{
				try
				{
					Date date = new Date();
					Thread.sleep(100);
					Clip clip = ClipboardManager.getClipboardContents();
					
					boolean duplicate = false;
					for (Clip existingClip : clipList)
					{
						System.out.println("checking " + clip.getDisplay(0) + " against " + existingClip.getDisplay(0));
						if (clip.getDisplay(0).equals(existingClip.getDisplay(0)))
							duplicate = true;
					}

					if (!duplicate)
					{
						SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH-mm-ss");
						File file = new File(CLIPS_PATH, dateFormat.format(date) + ".clip");

						ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
						out.writeObject(clip);
						out.close();

						clip.setClipFile(file);

						display.asyncExec(new Runnable()
						{
							@Override
							public void run()
							{
								clipWindow.addClip(clip);
							}
						});

						clipList.add(clip);
					}
				}
				catch (IOException ioe)
				{
					ioe.printStackTrace();
				}
				catch (InterruptedException ie)
				{
					ie.printStackTrace();
				}
			}
		});

		// QUIT Key Combo
		Set<Integer> stopKeys = new HashSet<Integer>();
		stopKeys.add(NativeKeyEvent.VC_CONTROL_L);
		stopKeys.add(NativeKeyEvent.VC_SHIFT_L);
		stopKeys.add(NativeKeyEvent.VC_Q);
		KeyComboListener stopKeyComboListener = new KeyComboListener(stopKeys, new KeyTriggerable()
		{
			@Override
			public void trigger()
			{
				System.out.println("CLOSING APPLICATION!!!!!!");
				try
				{
					if (GlobalScreen.isNativeHookRegistered())
						GlobalScreen.unregisterNativeHook();
				}
				catch (NativeHookException nhe)
				{
					System.err.println("There was a problem unregistering the native hook.");
					System.err.println(nhe.getMessage());

					nhe.printStackTrace();
				}
				finally
				{
					System.exit(0);
				}
			}
		});

		clipWindow.open();

		display.dispose();
		System.exit(0);
	}

	private static List<Clip> readInClips(File pFile)
	{
		List<Clip> clipList = new ArrayList<Clip>();
		File[] files = pFile.listFiles();
		if (files != null && files.length > 0)
		{
			for (File file : files)
			{
				if (file.isFile() && !file.isHidden())
				{
					Clip clip = null;
					try
					{
						FileInputStream fileIn = new FileInputStream(file);
						ObjectInputStream in = new ObjectInputStream(fileIn);
						clip = (Clip) in.readObject();
						in.close();
						fileIn.close();
						clip.setClipFile(file);
						clipList.add(clip);
					}
					catch (IOException ioe)
					{
						ioe.printStackTrace();
					}
					catch (ClassNotFoundException cnfe)
					{
						System.out.println("Clip class not found");
						cnfe.printStackTrace();
					}
				}
			}
		}
		return clipList;
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents)
	{
		// nothing yet
	}

	public static void main(String... pArgs)
	{
		new ClipBin();

//		SwingUtilities.invokeLater(new Runnable()
//		{
//			@Override
//			public void run()
//			{
//				new ClipBin();
//			}
//		});
	}
}