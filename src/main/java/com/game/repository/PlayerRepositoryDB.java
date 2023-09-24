package com.game.repository;

import com.game.entity.Player;
import jakarta.persistence.NamedQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@Repository(value = "db")
public class PlayerRepositoryDB implements IPlayerRepository {
private final SessionFactory sessionFactory;
    public PlayerRepositoryDB() {
        Properties properties = new Properties();
        properties.put(Environment.DIALECT,"org.hibernate.dialect.MySQL8Dialect");
        properties.put(Environment.DRIVER, "com.p6spy.engine.spy.P6SpyDriver");
        properties.put(Environment.URL, "jdbc:p6spy:mysql://localhost:3306/rpg");
        properties.put(Environment.USER,"root");
        properties.put(Environment.PASS,"rf,ehvzr76");
        properties.put(Environment.HBM2DDL_AUTO,"update");

        sessionFactory =new Configuration()
                .addAnnotatedClass(Player.class)
                .addProperties(properties).buildSessionFactory();
    }

    @Override
    public List<Player> getAll(int pageNumber, int pageSize) {
        try(Session session = sessionFactory.openSession()){
           NativeQuery<Player> query = session.createNativeQuery("select  * from rpg.player",Player.class);
           query.setFirstResult(pageNumber*pageSize);//сколько нужно пропустить записей
            query.setMaxResults(pageSize);// кол-во на странице
            return query.list();
        }
    }

    @Override
    public int getAllCount() {
        try(Session session = sessionFactory.openSession()){
           Query<Long> query = session.createNamedQuery("player.getAllCount",Long.class);
           return Math.toIntExact(query.uniqueResult());// возвращаем один результат, приводим его к инту
        }
    }

    @Override
    public Player save(Player player) {
        try(Session session = sessionFactory.openSession()){
           Transaction transaction = session.beginTransaction();
            session.save(player);
            transaction.commit();
            return player;
        }
    }

    @Override
    public Player update(Player player) {
        try(Session session = sessionFactory.openSession()){
            Transaction transaction = session.beginTransaction();
            session.update(player);
            transaction.commit();
            return player;
        }
    }

    @Override
    public Optional<Player> findById(long id) { //ищет данные по id  и если находи возвращает , если не т возвращает null
        try(Session session = sessionFactory.openSession()){
            Transaction transaction = session.beginTransaction();
           Player player = session.find(Player.class,id);
            transaction.commit();
            return Optional.of(player);
        }
    }

    @Override
    public void delete(Player player) {
        try(Session session = sessionFactory.openSession()){
            Transaction transaction = session.beginTransaction();
            session.remove(player);
            transaction.commit();

        }
    }

    @PreDestroy
    public void beforeStop() {
     sessionFactory.close();// очищает ссесию

    }
}