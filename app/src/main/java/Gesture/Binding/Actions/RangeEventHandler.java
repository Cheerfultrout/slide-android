package Gesture.Binding.Actions;

interface RangeEventHandler
{
    void onValueChange(int oldValue, int newValue);

    void onUnbind();
}