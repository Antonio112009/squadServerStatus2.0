package repository;

import config.HibernateUtil;
import entities.Guilds;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public class GuildRepository {


    private Transaction transaction = null;
    private Session session = null;
    private Guilds guilds = null;

    //Get Single Guild
    public Guilds getGuildByDiscordId(long discord_id){
        try {
            session = HibernateUtil.getSessionFactory().openSession();

            // start a transaction
            transaction = session.beginTransaction();

            Query query = session.createQuery("from Guilds l where l.discord_id = :discord_id");
            query.setParameter("discord_id", discord_id);
            guilds = (Guilds) query.uniqueResult();

            transaction.commit();
            session.clear();
            session.close();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }

        return guilds;
    }


    //Save Guild
    public void saveGuild(Guilds guilds) {
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            session.saveOrUpdate(guilds);

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



    //Delete Guild
    public void deleteGuildByDiscordId(long discord_id) {
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            Query query = session.createQuery("delete Guilds l where l.discord_id = :discord_id");
            query.setParameter("discord_id", discord_id);
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
