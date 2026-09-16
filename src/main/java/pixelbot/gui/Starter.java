package pixelbot.gui;

import java.awt.AWTException;
import java.io.File;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;

import pixelbot.elements.ElemTools;
import pixelbot.log.BotLogger;
import pixelbot.misc.Tools;

public class Starter {

	public static void start(File workdir) {
		Tools.workdir = workdir;
		// Display.getDefault();
		try {
			BotLogger.init();
		} catch (Exception e) {}
		try {
			String skin = UIManager.getSystemLookAndFeelClassName();
			for (LookAndFeelInfo item : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(item.getName())) {
					skin = item.getClassName();
					break;
				}
			}
			UIManager.setLookAndFeel(skin);
		} catch (Exception e) {}

		try {
			ElemTools.factor = 1;
			PixelBotView v = new pixelbot.gui.PixelBotView();
			v.setVisible(true);
		} catch (AWTException e) {}

	}

}
