package Connection.USB;

import com.j03.mobileinput.SettingsActivity;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import Layout.Enums.PositioningMode;
import Settings.AppSettings;

public class UsbServer
    extends Thread
{
    private ServerSocket server;
    private Socket client;
    private final short port = 8072;
    private ObjectOutputStream output;
    private boolean running = false;

    @Override
    public void run()
    {
        this.running = true;
        try
        {
            server = new ServerSocket(getPort());
            server.setReuseAddress(true);
            if (!server.isBound())
            {
                server.bind(new InetSocketAddress(getPort()));
            }

            if (!server.isClosed())
            {
                client = server.accept();
                client.setTcpNoDelay(true);

                output = new ObjectOutputStream(client.getOutputStream());
                output.flush();
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        //connected
        if (client != null)
        {
            final short sensitivity = AppSettings.getInstance().getSystemSettings().getMouseSensitivity();

            if (AppSettings.getInstance().getSettingsElements().getPositioningMode() == PositioningMode.ABSOLUTE)
            {
                AppSettings.getInstance().getConnectionManager()
                    .getUsbConnectionManager()
                    .send(PositioningMode.ABSOLUTE, sensitivity);
            } else
            {
                AppSettings.getInstance().getConnectionManager().getUsbConnectionManager().send(
                    PositioningMode.RELATIVE, sensitivity);
            }

            SettingsActivity.getActivity().startActivity(
                AppSettings.getInstance().getActivitySettings()
                    .getCanvasIntent()); // Load the Canvas
        }
    }

    // Getters
    private short getPort()
    {
        return this.port;
    }

    ObjectOutputStream getOutput()
    {
        return this.output;
    }

    ServerSocket getServer()
    {
        return this.server;
    }

    Socket getClient()
    {
        return this.client;
    }

    public boolean isRunning()
    {
        return this.running;
    }
}