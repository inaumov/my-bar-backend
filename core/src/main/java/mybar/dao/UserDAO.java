package mybar.dao;

import org.springframework.stereotype.Repository;
import mybar.entity.um.User;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserDao extends GenericDaoImpl<User> {

    public User findByEmail(String email) {
        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
        query.setParameter("email", email);
        try {
            return query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public List<User> findAll() {
        TypedQuery<User> query = em.createQuery("SELECT u FROM User u", User.class);
        return query.getResultList();
    }

    public User findByUsername(String username) {
        List<User> userList = new ArrayList<User>();
        TypedQuery<User> query = em.createQuery("SELECT u from User u where u.login = :login", User.class);
        query.setParameter("login", username);
        userList = query.getResultList();
        if (!userList.isEmpty())
            return userList.iterator().next();
        else
            return null;
    }
}