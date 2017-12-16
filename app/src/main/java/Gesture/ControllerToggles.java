package Gesture;

abstract class ControllerToggles
{
    private static int previousCommand;

    static void setPreviousCommand(final int e)
    {
        previousCommand = e;
    }

    static int getPreviousCommand()
    {
        return previousCommand;
    }
}