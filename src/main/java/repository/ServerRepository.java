package repository;

import config.HibernateUtil;
import entities.Guilds;
import entities.MessageServer;
import entities.Servers;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class ServerRepository {

    private Transaction transaction = null;
    private Session session = null;
    private Servers servers = null;


    //Get Single Guild
    public Servers getServerByServerId(String server_id) {
        try {
            session = HibernateUtil.getSessionFactory().openSession();

            // start a transaction
            transaction = session.beginTransaction();

            Query query = session.createQuery("from Servers l where l.battlemetrics_id = :battlemetrics_id");
            query.setParameter("battlemetrics_id", server_id);
            servers = (Servers) query.uniqueResult();

            transaction.commit();
            session.clear();
            session.close();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return servers;
    }


    //Get Single Server
    public Servers getServerByGuildAndServerId(long discord_id, String server_id){
        try {
            session = HibernateUtil.getSessionFactory().openSession();

            // start a transaction
            transaction = session.beginTransaction();

            Query query = session.createQuery("select serv from Servers as serv join serv.messageServers as ms join ms.guild as guild where guild.discord_id = :discord_id and serv.battlemetrics_id = :server_id");
            query.setParameter("server_id", server_id);
            query.setParameter("discord_id", discord_id);
            servers = (Servers) query.uniqueResult();

            transaction.commit();
            session.clear();
            session.close();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }

        return servers;
    }

    //Get MessageID
    public long getMessageIdByGuildAndServerId(long discord_id, String server_id){
        long id = 0;
        try {
            session = HibernateUtil.getSessionFactory().openSession();

            // start a transaction
            transaction = session.beginTransaction();

            Query query = session.createQuery("select ms from Servers as serv join serv.messageServers as ms join ms.guild as guild where guild.discord_id = :discord_id and serv.battlemetrics_id = :server_id");
            query.setParameter("server_id", server_id);
            query.setParameter("discord_id", discord_id);
            MessageServer messageServer = (MessageServer) query.uniqueResult();
            id = messageServer.getMessage_id();

            transaction.commit();
            session.clear();
            session.close();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }

        return id;
    }



    //Get all servers
    public List<Servers> getAllServers(){
        List<Servers> servers = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            String hql = "from Servers";
            Query<Servers> query = session.createQuery(hql, Servers.class);
            servers = query.list();

            transaction.commit();
            session.clear();
            session.close();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
        }
        return servers;
    }



    //Save Server
    public void saveServer(Servers servers) {
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            session.saveOrUpdate(servers);

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



    //checker
    public boolean isServerExistByServerId(String serverId) {
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            // start a transaction
            transaction = session.beginTransaction();

            Query query = session.getSession().
                    createQuery("from Servers t where t.battlemetrics_id = :battlemetrics_id");
            query.setParameter("battlemetrics_id", serverId);
            query.setMaxResults(1);
            Servers result = (Servers) query.uniqueResult();

            transaction.commit();
            session.clear();
            session.close();

            return (result != null);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }
}
