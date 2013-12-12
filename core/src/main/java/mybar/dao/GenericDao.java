package mybar.dao;

public interface GenericDao<T> {

    T create(T t);

    T read(Object id);

    T update(T t);

    void delete(Object id);

}