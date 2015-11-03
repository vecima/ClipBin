package com.codeweasel.clipbin;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

public class ClipWindow implements ClipboardOwner
{
	private Label mStatus;
	private Map<String, Clip> mClipMap;
	private List mList;
	private Display mDisplay;
	private Shell mShell;

	public ClipWindow(Display display, java.util.List<Clip> pClips)
	{
		this.mDisplay = display;
		this.mShell = new Shell(this.mDisplay);

		this.mStatus = new Label(this.mShell, SWT.NONE);
		this.mStatus.setText("");

		FormLayout layout = new FormLayout();
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		layout.spacing = 5;
		this.mShell.setLayout(layout);

		FormData labelData = new FormData();
		labelData.left = new FormAttachment(0);
		labelData.right = new FormAttachment(100);
		labelData.bottom = new FormAttachment(100);
		this.mStatus.setLayoutData(labelData);

		this.mList = new List(this.mShell, SWT.SINGLE);

		this.mList.addListener(SWT.Selection, event -> onListItemSelect(this.mList));

		FormData listData = new FormData();
		listData.width = 400;
		listData.height = 10;
		listData.left = new FormAttachment(this.mShell, 0);
		listData.top = new FormAttachment(this.mShell, 0);
		listData.right = new FormAttachment(100, 0);
		listData.bottom = new FormAttachment(this.mStatus, 0);
		this.mList.setLayoutData(listData);

		this.mClipMap = new HashMap<String, Clip>();
		for (Clip clip : pClips)
		{
			this.addClip(clip);
		}
	}
	
	public void open()
	{
		this.mShell.setText("Clip Bin");
		this.mShell.pack();
		this.mShell.open();

		while (!this.mShell.isDisposed())
		{
			if (!this.mDisplay.readAndDispatch())
			{
				this.mDisplay.sleep();
			}
		}
	}

	private void onListItemSelect(List list)
	{
		String[] items = list.getSelection();
		if (items.length > 0)
		{
			String display = items[0];
			this.mStatus.setText(display);
			Clip clip = this.mClipMap.get(display);
			if (clip != null)
			{
				try
				{
					ClipboardManager.setClipboardContents(clip, this);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public void addClip(Clip pClip)
	{
		String display = pClip.getDisplay(50);
		this.mClipMap.put(display, pClip);
		this.mList.add(display);

		this.mList.setSelection(this.mList.getItemCount() - 1);
		onListItemSelect(this.mList);
		FormData formData = (FormData) this.mList.getLayoutData();
		if (formData != null)
		{
			formData.height = formData.height + 15;
			this.mShell.pack();
			this.mShell.open();
		}
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents)
	{
		// nothing yet
	}
}