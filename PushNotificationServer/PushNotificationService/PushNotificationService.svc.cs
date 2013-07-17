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
using MongoDB.Driver.Builders;
using MongoDB.Bson;


namespace PushNotificationService
{
    public class PushNotificationService : IPushNotificationService
    {
        

        public int Register(string regId, string searchTerm)
        {
            var client = new MongoClient("mongodb://10.4.0.133");
            var server = client.GetServer();

            var database = server.GetDatabase("pushNotification");
            var searchTermCollection = database.GetCollection("SearchTerm");

            try
            {
                searchTermCollection.Update(Query.EQ("searchTerm", searchTerm ), Update.AddToSet("regIds", regId), UpdateFlags.Upsert);
                return 0;
            }
            catch (Exception e)
            {
                return -1;
            }
        }

        public List<Message> GetMessages(string searchTerm)
        {
            List<Message> messages = new List<Message>();

            var client = new MongoClient("mongodb://10.4.0.133");
            var server = client.GetServer();

            var database = server.GetDatabase("pushNotification");
            var messagesCollection = database.GetCollection("Messages");


            try
            {
                //var push = new PushSharp.PushBroker();
                //push.RegisterGcmService(new GcmPushChannelSettings("AIzaSyCRdVTZUqfHX7kCQWYAZWYoUXBEEwKZ-kA"));
                //push.QueueNotification(new GcmNotification().ForDeviceRegistrationId(regId)
                //                     .WithJson("{\"message\":\"" + message + " \",\"badge\":7,\"sound\":\"sound.caf\"}"));

                var modules = messagesCollection.Find(Query.Matches("text", searchTerm));
                foreach (var doc in modules)
                {
                    messages.Add(new Message
                    {
                        Id = doc["_id"].ToString(),
                        Text = doc["text"].ToString(),
                        Title = doc["title"].ToString()
                    });
                }

                return messages;
            }
            catch (Exception e)
            {
                return messages;
            }
        }

        public int AddMessage(string title, string text)
        {
            var push = new PushSharp.PushBroker();

            var client = new MongoClient("mongodb://10.4.0.133");
            var server = client.GetServer();
            var database = server.GetDatabase("pushNotification");

            var searchTermCollection = database.GetCollection("SearchTerm");

            try
            {
                //var array = new List<string>() { text };
                var searchTerms = searchTermCollection.FindAll();
                foreach (var doc in searchTerms)
                {
                    var searchTerm = doc["searchTerm"].ToString();
                    if (text.Contains(searchTerm))
                    {
                        
                        var regIds = doc["regIds"];
                        foreach (var regId in regIds.AsBsonArray)
                        {
                            push.RegisterGcmService(new GcmPushChannelSettings("AIzaSyCRdVTZUqfHX7kCQWYAZWYoUXBEEwKZ-kA"));
                            push.QueueNotification(new GcmNotification().ForDeviceRegistrationId(regId.ToString())
                                                 .WithJson("{\"message\":\"New message for " + searchTerm + " \",\"badge\":7,\"sound\":\"sound.caf\"}"));
                            
                        }
                    }

                }

            }
            catch (Exception e)
            {
                return -1;
            }

            var messagesCollection = database.GetCollection("Messages");
            try
            {
                var a = messagesCollection.Insert(
                    new
                    {
                        title = title,
                        text = text
                    }, WriteConcern.Acknowledged);

                return 0;
            }
            catch (Exception e)
            {
                return -1;
            }
        }
    }
}
