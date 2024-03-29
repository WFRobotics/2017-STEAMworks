package org.wfrobotics.prototype.config;

import org.wfrobotics.prototype.commands.ExampleForwardCommand;
import org.wfrobotics.reuse.controller.ButtonFactory;
import org.wfrobotics.reuse.controller.ButtonFactory.TRIGGER;
import org.wfrobotics.reuse.controller.Xbox;

import edu.wpi.first.wpilibj.buttons.Button;

/** Maps Buttons to Commands **/
public class IO
{
    public static Xbox controller = new Xbox(0);

    public static Button X = ButtonFactory.makeButton(controller, Xbox.BUTTON.X, TRIGGER.WHILE_HELD, new ExampleForwardCommand());
    public static Button Y = ButtonFactory.makeButton(controller, Xbox.BUTTON.Y, TRIGGER.WHILE_HELD, new ExampleForwardCommand());
    // TODO Switch the 'Y' Button to run another Command, such as ExampleBackwardCommand
    // TODO create more buttons if you need them to do your testing
}