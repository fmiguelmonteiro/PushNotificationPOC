using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.ServiceModel;
using System.ServiceModel.Web;
using System.Text;
using PushSharp;
using PushSharp.Android;
using PushSharp.Apple;
using PushSharp.Core;
using MongoDB.Driver;


namespace PushNotificationService
{
    public class PushNotificationService : IPushNotificationService
    {
        public int Register(string regId, string searchTerm)
        {
            var client = new MongoClient("mongodb://10.4.0.133");
            var server = client.GetServer();

            var database = server.GetDatabase("pushNotification");
            var commentCollection = database.GetCollection("Registers");

            try
            {
                var a = commentCollection.Insert(
                    new { 
                        regId = regId,  
                        searchTerm = searchTerm
                    }, WriteConcern.Acknowledged);

                return 0;
            }
            catch (Exception e)
            {
                return -1;
            }
        }


        //public void PushNotification(string apiKey, string regId, string message)
        //{
        //    var push = new PushSharp.PushBroker();
        //    push.RegisterGcmService(new GcmPushChannelSettings("AIzaSyCRdVTZUqfHX7kCQWYAZWYoUXBEEwKZ-kA"));
        //    push.QueueNotification(new GcmNotification().ForDeviceRegistrationId(regId)
        //                         .WithJson("{\"message\":\"" + message + " \",\"badge\":7,\"sound\":\"sound.caf\"}"));
        //}


        public List<Message> GetMessages(string searchTerm)
        {
            return new List<Message>(){
                new Message(){
                    Id = 0,
                    Title = "Batatas",
                    Text = "Batatas 0"
                },
                new Message(){
                    Id = 1,
                    Title = "Batatas",
                    Text = "Batatas 1"
                },
                new Message(){
                    Id = 2,
                    Title = "Batatas",
                    Text = "Batatas 2"
                },
            };
        }
    }
}
