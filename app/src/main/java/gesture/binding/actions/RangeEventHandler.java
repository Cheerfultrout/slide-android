package gesture.binding.actions;

interface RangeEventHandler
{
    void onValueChange(int oldValue, int newValue);

    void onUnbind();
}