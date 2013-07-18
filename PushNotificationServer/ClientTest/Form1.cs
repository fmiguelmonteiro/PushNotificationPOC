using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.IO;
using PushSharp;
using PushSharp.Android;
using PushSharp.Apple;
using PushSharp.Core;
using System.ServiceModel;
using System.ServiceModel.Description;
using ClientTest.PushNotificationService;
using System.Net;
using System.Runtime.Serialization.Json;

namespace ClientTest
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
        }

        private void button1_Click(object sender, EventArgs e)
        {

            //string ServiceUri = "http://localhost:58145/PushNotificationService.svc/AddMessage?title="
            //                     + title.Text;
            //WebClient proxy = new WebClient();
            //proxy.co
            //proxy.DownloadStringCompleted +=
            //           new DownloadStringCompletedEventHandler(proxy_DownloadStringCompleted);

            //proxy.DownloadStringAsync(new Uri(ServiceUri));

        }

        void proxy_DownloadStringCompleted(object sender, DownloadStringCompletedEventArgs e)
        {
            Stream stream = new MemoryStream(Encoding.Unicode.GetBytes(e.Result));
            DataContractJsonSerializer obj = new DataContractJsonSerializer(typeof(string));
            string result = obj.ReadObject(stream).ToString();
            MessageBox.Show(result);

            //PushNotificationService.PushNotificationServiceClient client = new PushNotificationService.PushNotificationServiceClient(;
            ////var push = new PushSharp.PushBroker();

            //client.AddMessage(title.Text, text.Text, "http://www.sapo.pt");

            //client.Close();

            string baseAddress = "http://localhost:58145/PushNotificationService.svc";

            //ServiceHost host = new ServiceHost(typeof(IPushNotificationService), new Uri(baseAddress));

            //ServiceEndpoint endpoint = host.AddServiceEndpoint(typeof(IPushNotificationService), new WebHttpBinding(), "");

            //endpoint.Behaviors.Add(new WebHttpBehavior());

            //host.Open();

            //Console.WriteLine("Host opened");



            //HttpWebRequest req = (HttpWebRequest)HttpWebRequest.Create(baseAddress + "/ProcessXml");

            //req.Method = "POST";

            //req.ContentType = "application/xml; charset=utf-8";

            //string reqBody = @"<TextBlock x:Name=""text"" IsHitTestVisible=""false"" Text=""Hello"" Foreground=""black"" xmlns=""http://schemas.microsoft.com/winfx/2006/xaml/presentation"" xmlns:x=""http://schemas.microsoft.com/winfx/2006/xaml""/>";

            //byte[] reqBodyBytes = Encoding.UTF8.GetBytes(reqBody);

            //Stream reqStream = req.GetRequestStream();

            //reqStream.Write(reqBodyBytes, 0, reqBodyBytes.Length);

            //reqStream.Close();

            //push.RegisterGcmService(new GcmPushChannelSettings("AIzaSyCRdVTZUqfHX7kCQWYAZWYoUXBEEwKZ-kA"));
            ////Fluent construction of an Android GCM Notification
            ////IMPORTANT: For Android you MUST use your own RegistrationId here that gets generated within your Android app itself!

            //push.QueueNotification(new GcmNotification().ForDeviceRegistrationId("APA91bF6vyksOdVplpwomrHG-oRW8Ha96XVUbekPBtyKwDZdF2eoEDxbGpUv0TeOOJMo9iN7eNFwlyV3vv0TOhEMMRJ0cUPinZVynVwIuWA9GnX4XyTuA1myLud5fGu5iv2llUmT71jXizIemqNU1HFFHmt_nc1Owwm7bc6gEI1uo_WS3ww1ZI0")
            //                     .WithJson("{\"message\":\"Titó Rabetão!\",\"badge\":7,\"sound\":\"sound.caf\"}"));
        }
    }
}
