package de.jcup.yamleditor.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.service.prefs.BackingStoreException;

public class YamlEditorPreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		  IEclipsePreferences defaults = DefaultScope.INSTANCE.getNode("de.jcup.yamleditor");
		  defaults.putInt(YamlEditorPreferenceConstants.P_SPACES_TO_REPLACE_TAB.getId(), 2);
		  try {
			defaults.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

}
