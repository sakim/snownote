package kr.pragmatic.snownote;

import kr.pragmatic.snownote.actions.CreatePageAction;
import kr.pragmatic.snownote.actions.SyncAction;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

/**
 * An action bar advisor is responsible for creating, adding, and disposing of
 * the actions added to a workbench window. Each window will be populated with
 * new actions.
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	// Actions - important to allocate these only in makeActions, and then use
	// them
	// in the fill methods. This ensures that the actions aren't recreated
	// when fillActionBars is called with FILL_PROXY.
	private IWorkbenchAction aboutAction;
	private IWorkbenchAction preferenceAction;

	private IWorkbenchAction saveAction;
	private IWorkbenchAction saveAllAction;
	private IWorkbenchAction exitAction;

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	protected void makeActions(final IWorkbenchWindow window) {
		// Creates the actions and registers them.
		// Registering is needed to ensure that key bindings work.
		// The corresponding commands keybindings are defined in the plugin.xml
		// file.
		// Registering also provides automatic disposal of the actions when
		// the window is closed.
		aboutAction = ActionFactory.ABOUT.create(window);
		preferenceAction = ActionFactory.PREFERENCES.create(window);

		saveAction = ActionFactory.SAVE.create(window);
		saveAllAction = ActionFactory.SAVE_ALL.create(window);
		exitAction = ActionFactory.QUIT.create(window);

		register(aboutAction);
		register(preferenceAction);

		register(saveAction);
		register(saveAllAction);
		register(exitAction);
	}

	protected void fillMenuBar(IMenuManager menuBar) {

		MenuManager fileMenu = new MenuManager("&File",
				IWorkbenchActionConstants.M_FILE);
		// fileMenu.add(newPageAction);
		fileMenu.add(saveAction);
		fileMenu.add(saveAllAction);
		fileMenu.add(exitAction);
		menuBar.add(fileMenu);

		// temp: 액션 추가하지 않으면 환경설정이 보이지 않기에 추가하고 보이지 않도록 함.
		MenuManager noteMenu = new MenuManager("SnowNote", "snownote");
		noteMenu.add(aboutAction);
		noteMenu.add(preferenceAction);
		noteMenu.setVisible(false);
		menuBar.add(noteMenu);

		MenuManager windowsMenu = new MenuManager("&Windows",
				IWorkbenchActionConstants.M_WINDOW);
		windowsMenu.add(preferenceAction);
		menuBar.add(windowsMenu);

		MenuManager helpMenu = new MenuManager("&Help",
				IWorkbenchActionConstants.M_HELP);
		// helpMenu.add(preferenceAction);
		helpMenu.add(aboutAction);
		menuBar.add(helpMenu);
	}

	@Override
	protected void fillCoolBar(ICoolBarManager coolBar) {
		IToolBarManager toolBar = new ToolBarManager(coolBar.getStyle());
		coolBar.add(toolBar);
		toolBar.add(saveAction);
		toolBar.add(saveAllAction);
	}
}
