package pbpu.operation;

import java.util.List;

import pbpu.annotation.Delete;
import pbpu.annotation.Get;
import pbpu.annotation.GetList;
import pbpu.annotation.Insert;
import pbpu.annotation.Update;

/***
 * Class annotated with @Crud should implements this Interface
 */
public interface Operation<T> {
    
    @Insert
    void insert(T data);

    @Update
    void update(int id, T data);

    @Get
    T get(int id);

    @GetList
    List<T> getList();

    @Delete
    void delete(int id);
}
