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
using MongoDB.Driver.Linq;
using MongoDB.Bson;
using System.Threading;
using System.Text.RegularExpressions;


namespace PushNotificationService
{
    public class PushNotificationService : IPushNotificationService
    {
        private static MongoClient client = new MongoClient("mongodb://10.4.0.133");
        private static MongoDatabase mongoDB = client.GetServer().GetDatabase("pushNotification");
        private static PushBroker push = new PushSharp.PushBroker();

        private MongoCollection<UserSettings> collectionUserSettings = null;
        private MongoCollection<UserSettings> CollectionUserSettings
        {
            get
            {
                if (collectionUserSettings == null)
                    collectionUserSettings = mongoDB.GetCollection<UserSettings>("UserSettings");
                return collectionUserSettings;
            }
        }

        private MongoCollection<Topic> collectionTopics = null;
        private MongoCollection<Topic> CollectionTopics
        {
            get
            {
                if (collectionTopics == null)
                    collectionTopics = mongoDB.GetCollection<Topic>("Topics");
                return collectionTopics;
            }
        }

        static PushNotificationService()
        {
            var thread = new Thread(() =>
            {
                while (true)
                {
                    var server = client.GetServer();

                    var database = server.GetDatabase("pushNotification");
                    var searchTermCollection = database.GetCollection("Topics");

                    var messagesCollection = database.GetCollection("Messages");


                    var searchTerms = searchTermCollection.FindAll();
                    foreach (var doc in searchTerms)
                    {
                        var searchTerm = doc["Name"].ToString();

                        var messages = messagesCollection.Find(
                            Query.And(
                                Query.Matches("text", BsonRegularExpression.Create(new Regex(searchTerm, RegexOptions.IgnoreCase))),
                                Query.EQ("notified", false)
                            )
                        );

                        if (messages.Count() > 0)
                        {

                            var regIds = doc["RegIds"];
                            foreach (var regId in regIds.AsBsonArray)
                            {
                                push.RegisterGcmService(new GcmPushChannelSettings("AIzaSyCRdVTZUqfHX7kCQWYAZWYoUXBEEwKZ-kA"));
                                push.QueueNotification(new GcmNotification().ForDeviceRegistrationId(regId.ToString())
                                                     .WithJson("{\"topic\":\"" + searchTerm + "\", \"newMessages\":\"" + messages.Count() + "\",\"badge\":7,\"sound\":\"sound.caf\"}"));

                            }
                        }

                    }

                    messagesCollection.Update(Query.EQ("notified", false), Update.Set("notified", true));

                    Thread.Sleep(30000);
                }
            });

            thread.IsBackground = true;
            thread.Start();
        }

        public int SubscribeTopic(string regId, string topic)
        {
            try
            {

                var result = CollectionTopics.AsQueryable<Topic>()
                    .Where(t => t.Name == topic && t.RegIds.Contains(regId));
                if (result.Count() == 0)
                {
                    IMongoQuery query = Query<Topic>.EQ(t => t.Name, topic);
                    IMongoUpdate update = Update<Topic>.AddToSet(t => t.RegIds, regId)
                        .Inc(t => t.NumberOfSubscribers, 1);
                    CollectionTopics.Update(query, update, UpdateFlags.Upsert);
                }
            }
            catch (Exception e)
            {
                return -1;
            }
            return 0;
        }

        public int UnsubscribeTopic(string regId, string topic)
        {
            try
            {
                var result = CollectionTopics.AsQueryable<Topic>()
                    .Where(t => t.Name == topic && t.RegIds.Contains(regId));
                if (result.Count() > 0)
                {
                    IMongoQuery query = Query<Topic>.EQ(t => t.Name, topic);
                    IMongoUpdate update = Update<Topic>.Pull(t => t.RegIds, regId)
                        .Inc(t => t.NumberOfSubscribers, -1);

                    CollectionTopics.Update(query, update);
                }
            }
            catch (Exception e)
            {
                return -1;
            }
            return 0;
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
                var modules = messagesCollection.Find(
                        Query.Matches("text", BsonRegularExpression.Create(new Regex(searchTerm, RegexOptions.IgnoreCase)))
                    ).SetSortOrder(SortBy.Descending("date"));

                foreach (var doc in modules)
                {
                    messages.Add(new Message
                    {
                        Id = doc["_id"].ToString(),
                        Text = doc["text"].ToString(),
                        Title = doc["title"].ToString(),
                        Url = doc["url"].IsBsonNull ? "" : doc["url"].ToString(),
                        Date = doc["date"].IsBsonNull ? DateTime.MinValue.ToString("yyyy-MM-dd'T'HH:mm:ssz") : DateTime.Parse(doc["date"].ToString()).ToString("yyyy-MM-dd'T'HH:mm:ssz")
                    });
                }

                return messages;
            }
            catch (Exception e)
            {
                return messages;
            }
        }

        public int AddMessage(string title, string text, string url)
        {
            var client = new MongoClient("mongodb://10.4.0.133");
            var server = client.GetServer();
            var database = server.GetDatabase("pushNotification");

            var messagesCollection = database.GetCollection("Messages");
            try
            {
                var a = messagesCollection.Insert(
                    new
                    {
                        title = title,
                        text = text,
                        notified = false,
                        url = url,
                        date = DateTime.UtcNow
                    }, WriteConcern.Acknowledged);

                return 0;
            }
            catch (Exception e)
            {
                return -1;
            }
        }


        public List<string> GetSubscribedTopics(string regId)
        {
            List<string> topics = new List<string>();

            try
            {
                var result = CollectionTopics.AsQueryable<Topic>()
                .Where(t => t.RegIds.Contains(regId))
                .OrderBy(t => t.Name);

                foreach (var doc in result)
                {
                    topics.Add(doc.Name);
                }

                return topics;
            }
            catch (Exception e)
            {
                return topics;
            }
        }


        public List<string> GetAllTopics()
        {
            List<string> topics = new List<string>();

            var client = new MongoClient("mongodb://10.4.0.133");
            var server = client.GetServer();

            var database = server.GetDatabase("pushNotification");
            var searchTermCollection = database.GetCollection("Topics");

            try
            {
                var result = searchTermCollection.FindAll();
                foreach (var doc in result)
                {
                    topics.Add(doc["Name"].ToString());
                }

                return topics;
            }
            catch (Exception e)
            {
                return topics;
            }
        }


        public List<Topic> GetPopularTopics(string regId)
        {
            List<Topic> topics = new List<Topic>();

            int topPopuparTopics = 10;
            IMongoQuery query = Query<UserSettings>.EQ(e => e.RegId, regId);
            UserSettings userSettings = CollectionUserSettings.FindOne(query);
            if (userSettings != null)
                topPopuparTopics = userSettings.TopPopuparTopics;

            try
            {
                var result = CollectionTopics.AsQueryable<Topic>()
                    .Where(t => !t.RegIds.Contains(regId))
                    .OrderByDescending(t => t.NumberOfSubscribers)
                    .Take(topPopuparTopics);
                foreach (var doc in result)
                {
                    topics.Add(new Topic()
                    {
                        Name = doc.Name,
                        NumberOfSubscribers = doc.NumberOfSubscribers,
                        Id = doc.ObjectId.ToString()
                    });
                }
            }
            catch (Exception e)
            {
                return new List<Topic>();
            }
            return topics;
        }


        public UserSettings GetUserSettings(string regId)
        {
            IMongoQuery query = Query<UserSettings>.EQ(e => e.RegId, regId);
            return CollectionUserSettings.FindOne(query);
        }

        public int SaveUserSetting(UserSettings settings)
        {
            try
            {
                IMongoQuery query = Query<UserSettings>.EQ(s => s.RegId, settings.RegId);
                IMongoUpdate update = Update<UserSettings>.Set(s => s.RegId, settings.RegId)
                    .Set(s => s.TopPopuparTopics, settings.TopPopuparTopics);
                CollectionUserSettings.Update(query, update, UpdateFlags.Upsert);
            }
            catch (Exception e)
            {
                return -1;
            }
            return 0;
        }
    }
}
