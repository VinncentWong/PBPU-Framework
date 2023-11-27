package pbpu.database;

import java.util.List;

public interface CoreDatabase<T>{
    void create(T data);
    T get(int id);
    List<T> getList();
    void delete(int id);
    void update(int id, T data);
}
