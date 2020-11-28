package clipbin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class KeyComboListener implements KeyTriggerable
{
	private Map<Integer, KeyListener> fKeyListenerMap;
	private KeyTriggerable fKeyTriggerable;

	public KeyComboListener(Set<Integer> pKeys, KeyTriggerable pKeyTriggerable)
	{
		if (pKeys != null && pKeys.size() > 0)
		{
			this.fKeyListenerMap = new HashMap<Integer, KeyListener>(pKeys.size());
			for (Integer key : pKeys)
				this.fKeyListenerMap.put(key, new KeyListener(key, this));
		}
		this.fKeyTriggerable = pKeyTriggerable;
	}

	@Override
	public void trigger()
	{
		if (this.fKeyListenerMap != null && this.fKeyListenerMap.size() > 0)
		{
			boolean allKeysDown = true;
			for (KeyListener keyListener : this.fKeyListenerMap.values())
			{
				if (!keyListener.isKeyDown())
					allKeysDown = false;
			}

			if (allKeysDown)
				this.fKeyTriggerable.trigger();
		}
	}
}