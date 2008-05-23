 /*
 * xmlMVC.Startup 
 *
 *------------------------------------------------------------------------------
 *  Copyright (C) 2006-2008 University of Dundee. All rights reserved.
 *
 *
 * 	This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *------------------------------------------------------------------------------
 */
package xmlMVC;

//Java imports

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.io.IOException;

//Third-party libraries

//Application-internal dependencies

import util.WindowSaver;

/** 
 * This class has methods that need to be called when the application starts
 * and shuts-down. 
 * 
 * The method runStartUp() adds a shutDownHook to the JVM, which causes
 * runShutDown() to be called when the application quits.
 *
 * @author  William Moore &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:will@lifesci.dundee.ac.uk">will@lifesci.dundee.ac.uk</a>
 * @version 3.0
 * <small>
 * (<b>Internal version:</b> $Revision: $Date: $)
 * </small>
 * @since OME3.0
 */
public class StartupShutdown {

	
	/**
	 * This static method should called when the application starts up.
	 * It calls other methods that need to be run at start-up,
	 * then sets a ShutDownHook to the Runtime, so that when the application
	 * quits, runShutDown() is called. 
	 */
	public static void runStartUp() {
		
		initWindowSaver();
		
		
		Runtime.getRuntime().addShutdownHook(new ShutdownThread());
	}
	
	
	/**
	 * This static method is automatically called by the ShutdownThread class, 
	 * when the application shutsDown.
	 * It should be used to call any other static methods that need to be run
	 * when the application quits.  
	 */
	public static void runShutDown() {
		
		 try {
			WindowSaver.saveSettings();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * A Thread that is added as a ShutDownHook to the Runtime. 
	 * This thread is run when the application shuts down, and 
	 * it calls runShutDown();
	 * 
	 * @author will
	 *
	 */
	public static class ShutdownThread extends Thread {
        public void run() {
            runShutDown();
        }
    }
	
	
	/**
	 * This instantiates the WindowSaver singleton, which listens for 
	 * window-opening events and uses saved properties (if they exist) to set
	 * the window location and size. 
	 * WindowSaver.saveSettings(); must be called on shutdown, so that the
	 * new positions of all windows are saved to the system properties. 
	 */
	public static void initWindowSaver() {
		
		Toolkit tk = Toolkit.getDefaultToolkit();
        tk.addAWTEventListener(WindowSaver.getInstance(),AWTEvent.WINDOW_EVENT_MASK);
	}
	
}
