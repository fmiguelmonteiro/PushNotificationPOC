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
            var push = new PushSharp.PushBroker();


            push.RegisterGcmService(new GcmPushChannelSettings("AIzaSyCRdVTZUqfHX7kCQWYAZWYoUXBEEwKZ-kA"));
            //Fluent construction of an Android GCM Notification
            //IMPORTANT: For Android you MUST use your own RegistrationId here that gets generated within your Android app itself!

            push.QueueNotification(new GcmNotification().ForDeviceRegistrationId("APA91bF6vyksOdVplpwomrHG-oRW8Ha96XVUbekPBtyKwDZdF2eoEDxbGpUv0TeOOJMo9iN7eNFwlyV3vv0TOhEMMRJ0cUPinZVynVwIuWA9GnX4XyTuA1myLud5fGu5iv2llUmT71jXizIemqNU1HFFHmt_nc1Owwm7bc6gEI1uo_WS3ww1ZI0")
                                 .WithJson("{\"message\":\"Titó Rabetão!\",\"badge\":7,\"sound\":\"sound.caf\"}"));
        }
    }
}
