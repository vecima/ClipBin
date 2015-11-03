package com.codeweasel.clipbin;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class ClipWindow2 implements ClipboardOwner
{
	private static final int MAX_FORM_DATA_HEIGHT = 600;

	private Display mDisplay;
	private Shell mShell;
	private Label mStatus;
	private Map<String, Clip> mClipMap;
	private Table mTable;
	private Image mDeleteImage;

	public ClipWindow2(Display display, java.util.List<Clip> pClips)
	{
		this.mDisplay = display;
		this.mDeleteImage = new Image(this.mDisplay, "C:\\Users\\Eric\\workspace\\ClipBin\\resources\\delete_16.png");
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

//		this.mList = new List(this.mShell, SWT.SINGLE);
//
//		this.mList.addListener(SWT.Selection, event -> onListItemSelect(this.mList));
//
		FormData tableData = new FormData();
		tableData.width = 400;
		tableData.height = 100;
		tableData.left = new FormAttachment(this.mShell, 0);
		tableData.top = new FormAttachment(this.mShell, 0);
		tableData.right = new FormAttachment(100, 0);
		tableData.bottom = new FormAttachment(this.mStatus, 0);
//		this.mList.setLayoutData(listData);
		
		this.mTable = new Table(this.mShell, SWT.BORDER | SWT.MULTI);
		Rectangle clientArea = this.mShell.getClientArea();
		this.mTable.setBounds(clientArea.x, clientArea.y, 200, 200);

		this.mTable.addListener(SWT.Selection, event -> onTableItemSelect());

		Menu menu = new Menu(this.mShell, SWT.POP_UP);
		this.mTable.setMenu(menu);
		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText("Delete Selected Clips");
		item.addListener(SWT.Selection, new Listener()
		{
			@Override
			public void handleEvent(Event event)
			{
				int[] selectionIndices = ClipWindow2.this.mTable.getSelectionIndices();
				for (int i = 0; i < selectionIndices.length; i++)
				{
					TableItem tableItem = ClipWindow2.this.mTable.getItem(selectionIndices[i]);
					String clipDisplay = tableItem.getText();
					Clip clip = ClipWindow2.this.mClipMap.get(clipDisplay);
					clip.getClipFile().delete();
				}
				ClipWindow2.this.mTable.remove(selectionIndices);
			}
		});
		this.mTable.setLayoutData(tableData);

		this.mClipMap = new HashMap<String, Clip>();
		for (Clip clip : pClips)
		{
			this.addClip(clip);
		}

		/*
		 * NOTE: MeasureItem, PaintItem and EraseItem are called repeatedly.
		 * Therefore, it is critical for performance that these methods be as
		 * efficient as possible.
		 */
		Listener paintListener = new Listener()
		{
			@Override
			public void handleEvent(Event event)
			{
				switch (event.type)
				{
				case SWT.MeasureItem:
				{
					Rectangle rect = ClipWindow2.this.mDeleteImage.getBounds();
					event.width += rect.width;
					event.height = Math.max(event.height, rect.height + 2);
					break;
				}
				case SWT.PaintItem:
				{
					int x = event.x + event.width;
					Rectangle rect = ClipWindow2.this.mDeleteImage.getBounds();
					int offset = Math.max(0, (event.height - rect.height) / 2);
					event.gc.drawImage(ClipWindow2.this.mDeleteImage, x, event.y + offset);
					break;
				}
				}
			}
		};
		this.mTable.addListener(SWT.MeasureItem, paintListener);
		this.mTable.addListener(SWT.PaintItem, paintListener);
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

	private void onTableItemSelect()
	{
		int[] selectionIndices = ClipWindow2.this.mTable.getSelectionIndices();
		for (int i = 0; i < selectionIndices.length; i++)
		{
			TableItem tableItem = ClipWindow2.this.mTable.getItem(selectionIndices[i]);
			String clipDisplay = tableItem.getText();
			this.mStatus.setText(clipDisplay);
			Clip clip = ClipWindow2.this.mClipMap.get(clipDisplay);
			if (clip != null)
			{
				try
				{
					ClipboardManager.setClipboardContents(clip, this);
					i = selectionIndices.length;
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
		// this.mTable.add(display);

		TableItem item = new TableItem(this.mTable, SWT.NONE);
		item.setText(display);

		// Button button = new Button(((Composite) item), SWT.NONE);
		// button.setText("Remove");

		TableEditor editor = new TableEditor(this.mTable);
		Button button = new Button(this.mTable, SWT.CHECK);
		button.pack();
		editor.minimumWidth = button.getSize().x;
		editor.horizontalAlignment = SWT.RIGHT;
		editor.setEditor(button, item, 1);

		this.mTable.setSelection(this.mTable.getItemCount() - 1);
		onTableItemSelect();
		FormData formData = (FormData) this.mTable.getLayoutData();
		if (formData != null)
		{
			if ((formData.height + 15) <= MAX_FORM_DATA_HEIGHT)
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