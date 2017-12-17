package gesture;

public interface TouchEvents
{
    void touchStart(final float x, final float y);

    void touchMove(final float x, final float y);

    void touchUp();
}