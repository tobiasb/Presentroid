using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.Net.Sockets;

namespace PresendroidAddIn
{
    class Recipient
    {
        private UdpClient client = new UdpClient();

        private IPAddress ipAddress;
        private int portNumber;
        
        private IPEndPoint endpoint = null;

        public Recipient(IPAddress ipAddress, int portNumber)
        {
            this.ipAddress = ipAddress;
            this.portNumber = portNumber;
            endpoint = new IPEndPoint(ipAddress, portNumber);
        }

        public void Send(byte[] data)
        {
            if (data != null)
            {
                client.Send(data, data.Length, endpoint);
            }
        }

        public override bool Equals(object obj)
        {
            if (obj is Recipient)
            {
                return (obj as Recipient).ipAddress == ipAddress && (obj as Recipient).portNumber == portNumber;
            }

            return false;
        }
    }
}
