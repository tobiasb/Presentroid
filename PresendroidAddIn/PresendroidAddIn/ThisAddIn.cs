using System;
using System.Diagnostics;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Collections.Generic;
using System.Threading;
using System.Windows.Forms;
using Microsoft.Office.Interop.PowerPoint;
using System.IO;
using System.Drawing;
using System.Drawing.Imaging;
using System.Linq;

namespace PresendroidAddIn
{
    public partial class ThisAddIn
    {
        int listenPort = 4711;

        List<Recipient> endpoints = new List<Recipient>();
        //Stack<string> messagesToSend = new Stack<string>();

        bool isSlideShow = false;
        private Status statusView;

        private void ThisAddIn_Startup(object sender, System.EventArgs e)
        {
            endpoints.Clear();

            statusView = new Status();
            statusView.Show();
            statusView.Append("Startup");

            Thread receiveThread = new Thread(new ThreadStart(ReceiveData));
            receiveThread.IsBackground = true;
            receiveThread.Start();

            Application.SlideSelectionChanged += new Microsoft.Office.Interop.PowerPoint.EApplication_SlideSelectionChangedEventHandler(Application_SlideSelectionChanged);
            Application.SlideShowBegin += new Microsoft.Office.Interop.PowerPoint.EApplication_SlideShowBeginEventHandler(Application_SlideShowBegin);
            Application.SlideShowEnd += new EApplication_SlideShowEndEventHandler(Application_SlideShowEnd);

        }

        void Application_SlideShowEnd(Presentation Pres)
        {
            isSlideShow = false;
        }        

        void Application_SlideShowBegin(Microsoft.Office.Interop.PowerPoint.SlideShowWindow Wn)
        {
            isSlideShow = true;
        }

        void Application_SlideSelectionChanged(Microsoft.Office.Interop.PowerPoint.SlideRange SldRange)
        {
        }

        private void ThisAddIn_Shutdown(object sender, System.EventArgs e)
        {
        }

        private void SendNow(string text)
        {
            statusView.Append("Send [" + text + "]");

            byte[] data = Encoding.UTF8.GetBytes(text);

            endpoints.ForEach(r => r.Send(data));

            statusView.Append("Done");
        }

        private void SendNow(Slide s)
        {
            statusView.Append("Send Slide");

            byte[] prefix = Encoding.UTF8.GetBytes("currentslide=");
            byte[] image =  GetSlideImageData(s);

            if (image != null)
            {
                byte[] data = prefix.Concat(image).ToArray();

                if (data != null)
                {
                    endpoints.ForEach(r => r.Send(data));
                }
                else
                {
                    SendNow("status=NO DATA");
                }
            }
            else
            {
                SendNow("status=NO DATA");
            }

            statusView.Append("Done");
        }

        private void ReceiveData()
        {
            UdpClient client = new UdpClient(listenPort); 

            while (true)
            {
                IPEndPoint anyIP = new IPEndPoint(IPAddress.Any, listenPort);
                byte[] data = client.Receive(ref anyIP);

                string text = Encoding.UTF8.GetString(data);

                statusView.Append("Receive [" + text + "]");

                HandleMessage(text);

                statusView.Append("Handled");
            }
        }

        private void HandleMessage(string msg)
        {
            string[] parts = msg.Split(' ');

            try
            {
                switch (parts[0])
                {
                    //subscribe 192.168.0.23 3333
                    case "subscribe":
                        try
                        {
                            IPAddress ipAddress = IPAddress.Parse(parts[1]);
                            int port = int.Parse(parts[2]);

                            Recipient newRecipient = new Recipient(ipAddress, port);

                            if (!endpoints.Where(r => r == newRecipient).Any())
                            {
                                endpoints.Add(newRecipient);
                                SendNow("status=ADDED");

                                SendNow(Application.ActivePresentation.SlideShowWindow.View.Slide);
                            }
                            else
                            {
                                SendNow("status=NOT ADDED");
                            }
                        }
                        catch
                        {
                            SendNow("status=WRONG INPUT");
                        }
                    break;
                    case "slidesnum":
                        SendNow("slidesnum=" + Application.ActivePresentation.Slides.Count.ToString());
                        break;
                    case "viewtype":
                        SendNow("viewtype=" + Application.ActiveWindow.ViewType.ToString());
                        break;
                    case "next":
                        if (isSlideShow)
                        {
                            Application.ActivePresentation.SlideShowWindow.View.Next();

                            SendNow(Application.ActivePresentation.SlideShowWindow.View.Slide);
                        }
                        break;
                    case "prev":
                        if (isSlideShow)
                        {
                            Application.ActivePresentation.SlideShowWindow.View.Previous();

                            SendNow(Application.ActivePresentation.SlideShowWindow.View.Slide);
                        }
                        break;
                    default:
                        SendNow("status=UNKNOWN " + msg);
                    break;
                }
            }
            catch(Exception ex)
            { MessageBox.Show(ex.StackTrace, ex.Message); }
        }

        private byte[] GetSlideImageData(Slide s)
        {
            string fileName = Path.GetTempFileName();
            s.Export(fileName, "PNG", 320, 240);

            try
            {
                Bitmap bmp = new Bitmap(fileName);

                using (MemoryStream stream = new MemoryStream())
                {
                    bmp.Save(stream, ImageFormat.Png);
                    return stream.ToArray();
                }
            }
            catch { return null; }
            finally
            {
                //File.Delete(fileName);
            }
        }

        #region VSTO generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InternalStartup()
        {
            this.Startup += new System.EventHandler(ThisAddIn_Startup);
            this.Shutdown += new System.EventHandler(ThisAddIn_Shutdown);
        }
        
        #endregion
    }
}

