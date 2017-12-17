package Connection.Network;

import com.j03.mobileinput.SettingsActivity;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import Common.Enums.AppActivity;
import Layout.Enums.PositioningMode;
import Settings.AppSettings;

public class NetworkServer
    extends Thread
{
    private ObjectOutputStream output;
    private ServerSocket server;
    private Socket client;
    private final short port = 8074;
    private boolean running = false;

    @Override
    public void run()
    {
        this.running = true;
        try
        {
            server = new ServerSocket(getPort(), 0);
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
        } catch (IOException ioException)
        {
            ioException.printStackTrace();
        }

        //connected
        if (client != null)
        {
            final short sensitivity = AppSettings.getSystemSettings().getMouseSensitivity();

            if (AppSettings.getSettingsElements().getPositioningMode() == PositioningMode.ABSOLUTE)
            {
                AppSettings.getConnectionManager().getNetworkConnectionManager().send(
                    PositioningMode.ABSOLUTE, sensitivity); //, DisplayProperties.getDisplayWidth(),
                    // DisplayProperties.getDisplayHeight());
            } else//positioning mode==relative
            {
                AppSettings.getConnectionManager().getNetworkConnectionManager().send(
                    PositioningMode.RELATIVE, sensitivity);
            }

            // Load the Canvas
            AppSettings.getActivitySettings()
                    .loadActivity(SettingsActivity.getActivity(), AppActivity.CANVAS);
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