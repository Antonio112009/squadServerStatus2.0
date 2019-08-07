package repository;

import config.HibernateUtil;
import entities.Guilds;
import entities.MessageServer;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public class MessageServerRepository {

    private Transaction transaction = null;
    private Session session = null;
    private MessageServer messageServer;

    //Get Message
    public MessageServer getMessageByServerId(long discord_id, String server_id){
        try {
            session = HibernateUtil.getSessionFactory().openSession();

            // start a transaction
            transaction = session.beginTransaction();

            Query query = session.createQuery("select ms from Guilds as guild join guild.messageServers as ms join ms.server as server where guild.discord_id = :discord_id and server.battlemetrics_id = :server_id");
            query.setParameter("server_id", server_id);
            query.setParameter("discord_id", discord_id);
            messageServer = (MessageServer) query.uniqueResult();

            transaction.commit();
            session.clear();
            session.close();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }

        return messageServer;
    }

    //Save Guild
    public void saveMessage(MessageServer messageServer) {
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            session.saveOrUpdate(messageServer);

            transaction.commit();
            session.clear();
            session.close();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }


    //Save Guild
    public void deleteMessage(MessageServer messageServer) {
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            session.remove(messageServer);

            transaction.commit();
            session.clear();
            session.close();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }


    //Delete MessageServer
    public void deleteMessageServerByMessageId(long message_id, long id) {
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            Query query = session.createQuery("delete MessageServer mes where mes.message_id = :message_id and mes.server.id = :id");
            query.setParameter("message_id", message_id);
            query.setParameter("id", id);
            query.executeUpdate();

            transaction.commit();
            session.clear();
            session.close();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

}
