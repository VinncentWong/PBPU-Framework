package pbpu.database;

import java.util.List;

public class CsvDatabaseImplementation<T> implements CoreDatabase<T> {

    // by default, the file name that will be used as database
    // will be based on entity name
    private String entityName;

    public CsvDatabaseImplementation(String entityName){
        this.entityName = entityName.toLowerCase();
    }

    @Override
    public void create(T data) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public T get(int id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'get'");
    }

    @Override
    public List<T> getList() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getList'");
    }

    @Override
    public void delete(int id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public void update(int id, T data) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }
    
}
