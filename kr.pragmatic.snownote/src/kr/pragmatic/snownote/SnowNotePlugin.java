package kr.pragmatic.snownote;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import kr.pragmatic.snownote.core.SnowNote;
import kr.pragmatic.snownote.core.SnowNoteManager;
import kr.pragmatic.snownote.preferences.PreferenceKeys;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class SnowNotePlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "kr.pragmatic.snownote";

	// The shared instance
	private static SnowNotePlugin plugin;

	// Resource bundle
	private ResourceBundle resourceBundle;

	private static SnowNote note;

	/**
	 * The constructor
	 */
	public SnowNotePlugin() {
		try {
			resourceBundle = ResourceBundle
					.getBundle("kr.pragmatic.snownote.editors.SnowNoteEditorPluginResources");
		} catch (MissingResourceException e) {
			e.printStackTrace();
			resourceBundle = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static SnowNotePlugin getDefault() {
		return plugin;
	}

	public static IWorkbenchPage getActivePage() {
		return getDefault().internalGetActivePage();
	}

	private IWorkbenchPage internalGetActivePage() {
		IWorkbenchWindow window = getWorkbench().getActiveWorkbenchWindow();
		if (window == null)
			return null;
		return window.getActivePage();
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public static SnowNote getSnowNote() {
		if (note == null) {
			try {
				note = SnowNoteManager.createSnowNote();
			} catch (Exception e) {
				MessageDialog
						.openError(Display.getDefault().getActiveShell(), "에러",
								"이미 Snow Note가 사용중입니다. 동일한 계정에 대해서 2개의 Snow Note를 띄울 수 없습니다.");
				Display.getDefault().close();
				System.exit(0);
			}
		}
		return note;
	}

	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	public static String getWorkspacePath() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		return workspace.getRoot().getLocation().toString();
	}

	public static String getUserDomain() {
		IPreferenceStore store = SnowNotePlugin.getDefault()
				.getPreferenceStore();
		return store.getString(PreferenceKeys.USER_DOMAIN);
	}
}
