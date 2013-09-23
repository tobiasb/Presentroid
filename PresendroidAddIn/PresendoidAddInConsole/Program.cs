using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Sockets;
using System.Net;
using System.Diagnostics;
using System.Threading;

namespace PresendoidAddInConsole
{
    class Program
    {
        static int listenPort = 4713;
        static IPEndPoint sendToEndpoint;

        static void Main(string[] args)
        {
            try
            {
                Thread receiveThread = new Thread(new ThreadStart(ReceiveData));
                receiveThread.IsBackground = true;
                receiveThread.Start();

                sendToEndpoint = new IPEndPoint(new IPAddress(0x0100007f), 4711);
                Console.WriteLine("Will send to " + sendToEndpoint.ToString());

                SendText("subscribe 127.0.0.1 " + listenPort.ToString());

                string text;
                do
                {
                    text = Console.ReadLine();

                    // Den Text zum Remote-Client senden.
                    if (text != "")
                    {
                        SendText(text);
                    }
                } while (text != "");
            }
            catch (Exception err)
            {
                Console.WriteLine(err.ToString());
            }
        }

        static private void SendText(string text)
        {
            UdpClient client = new UdpClient();
            byte[] data = Encoding.UTF8.GetBytes(text);

            client.Send(data, data.Length, sendToEndpoint);
        }

        static private void ReceiveData()
        {
            UdpClient client = new UdpClient(listenPort);
            IPEndPoint anyIP = new IPEndPoint(IPAddress.Any, listenPort);

            while (true)
            {
                byte[] data = client.Receive(ref anyIP);

                string text = Encoding.UTF8.GetString(data);

                Console.WriteLine(DateTime.Now + ": " + text);
            }
        }
    }
}
