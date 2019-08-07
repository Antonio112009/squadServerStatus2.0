package repository;

import config.HibernateUtil;
import entities.Authorities;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class AuthorityRepository {

    private Transaction transaction = null;
    private Session session = null;

    public boolean isAuthorityExistByDiscordId(Long discordId) {
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            // start a transaction
            transaction = session.beginTransaction();

            Query query = session.getSession().
                    createQuery("from Authorities t where t.discord_id = :discordId");
            query.setParameter("discordId", discordId);
            query.setMaxResults(1);
            Authorities result = (Authorities) query.uniqueResult();

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


    public List<Authorities> getAllAccessByGuildId(long discord_id){
        List<Authorities> servers = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            String hql = "select auth from Guilds guild join guild.authorities as auth where guild.discord_id = :discord";
            Query<Authorities> query = session.createQuery(hql, Authorities.class);
            query.setParameter("discord", discord_id);
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
}
