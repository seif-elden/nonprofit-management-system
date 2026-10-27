package com.nonprofit;

import com.nonprofit.controller.MainController;
import com.nonprofit.view.MainView;

/**
 * Entry point: creates the main view and hands control to its controller.
 */
public class Main {

    public static void main(String[] args) {
        MainController app = new MainController(new MainView());
        app.run();
    }
}
