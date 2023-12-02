package pbpu.database;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;

import lombok.SneakyThrows;

public class CsvDatabaseImplementation<T> implements CoreDatabase<T> {

    // by default, the file name that will be used as database
    // will be based on entity name
    private String entityName;
    private CsvMapper objectMapper;

    @SneakyThrows
    public CsvDatabaseImplementation(String entityName){
        this.entityName = entityName.toLowerCase();
        this.objectMapper = new CsvMapper();
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

    @SneakyThrows
    @Override
    public List<T> getList() {
        var directory = new File("data");
        if (!directory.exists()) {
            directory.createNewFile();
        }

        var dataFile = new File(directory, entityName + ".json");
        if (!dataFile.exists()) {
            dataFile.createNewFile();
            this.objectMapper.writeValue(dataFile, entityName);
        }
        return this.objectMapper.readValue(dataFile, new TypeReference<List<T>>(){});
    }

    @SneakyThrows
    @Override
    public void delete(int id) {
        var directory = new File("data");
        if (!directory.exists()) {
            directory.createNewFile();  
        }

        var dataFile = new File(directory, entityName + ".csv"); 
        if (!dataFile.exists()) {
            System.out.printf("Tidak ada file %s. Silahkan masukkan data terlebih dahulu\n", this.entityName + ".json");
            return;
        }

        var dataList = this.objectMapper.readValue(dataFile, new TypeReference<List<T>>() {});
    }

    @Override
    public void update(int id, T data) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }
    
}
