package pbpu.database;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import lombok.SneakyThrows;
import pbpu.annotation.Id;

public class CsvDatabaseImplementation<T> implements CoreDatabase<T> {

    // by default, the file name that will be used as database
    // will be based on entity name
    private String entityName;
    private File dataFile;
    private File directory;
    private CsvMapper mapper;

    @SneakyThrows
    public CsvDatabaseImplementation(String entityName){
        this.entityName = entityName.toLowerCase();
        this.mapper = new CsvMapper();
        this.directory = new File("data");
        this.dataFile = new File("data", this.entityName + ".csv");
        DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
        prettyPrinter.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);
        this.mapper.setDefaultPrettyPrinter(prettyPrinter);
    }

    public void create(T data) {
        try {
            CsvSchema csvSchema = mapper.schemaFor(data.getClass()).withHeader();

            List<T> dataList;
            if (!dataFile.exists()) {
                dataFile.createNewFile();
                dataList = new ArrayList<>();
            } else {
                MappingIterator<T> dataMappingIterator = mapper.readerFor(data.getClass()).with(csvSchema).readValues(dataFile);
                dataList = dataMappingIterator.readAll();
            }

            if (dataList.isEmpty()) {
                initializeIdForData(data);
            } else {
                updateIdForNextData(data, dataList);
            }

            dataList.add(data);

            mapper.writer(csvSchema).writeValue(dataFile, dataList);

            System.out.printf("Sukses menambahkan data %s \n", this.entityName);
        } catch (Exception e) {
            System.out.println("Error occurred with message " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeIdForData(T data) throws IllegalAccessException {
        var fields = data.getClass().getDeclaredFields();
        for (var field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Id.class)) {
                setIdFieldValue(data, field, 1);
            }
            field.setAccessible(false);
        }
    }

    @Override
    public T get(int id) {
        T entity = null;
        var dataList = getList();
        for (var data : dataList) {
            var dataId = getId(data);
            if (dataId.getClass() == Integer.class) {
                var intId = (Integer) dataId;
                if (intId == id) {
                    entity = data;
                }
            } else if (dataId.getClass() == Double.class) {
                var doubleId = (Double) dataId;
                if (doubleId == id) {
                    entity = data;
                }
            } else if (dataId.getClass() == String.class) {
                var strId = (String) dataId;
                if (strId.equalsIgnoreCase(id + "")) {
                    entity = data;
                }
            }
        }
        return entity;
    }

    @Override
    @SneakyThrows
    public List<T> getList() {
        if(!directory.exists()){
            directory.mkdir();
        }

        if(!dataFile.exists()){
            dataFile.createNewFile();
        }
        CsvSchema csvSchema = mapper.schemaFor((Class<T>) Object.class).withHeader();

        // Read existing CSV file into List of T objects
        MappingIterator<T> dataMappingIterator = mapper.readerFor((Class<T>) Object.class).with(csvSchema).readValues(dataFile);
        return dataMappingIterator.readAll();
    }

    @SneakyThrows
    @Override
    public void delete(int id) {
        if(!directory.exists()){
            directory.mkdir();
        }
        if(!dataFile.exists()){
            System.out.printf("Tidak ada file %s. Silahkan masukkan data terlebih dahulu\n", this.entityName + ".csv");
            return;
        }

        var dataList = getList();
        boolean isSuccess = false;
        for(int i = 0; i<dataList.size(); i++){
            var data = dataList.get(i);
            var dataId = getId(data);
            if(dataId.getClass() == Integer.class){
                var intId = (Integer) dataId;
                if(intId == id){
                    dataList.remove(i);
                    isSuccess = true;
                }
            } else if(dataId.getClass() == Double.class){
                var doubleId = (Double) dataId;
                if(doubleId == id){
                    dataList.remove(i);
                    isSuccess = true;
                }
            } else if(dataId.getClass() == String.class){
                var strId = (String) dataId;
                if(strId.equalsIgnoreCase(id + "")){
                    dataList.remove(i);
                    isSuccess = true;
                }
            }
            if(isSuccess)break;
        }

        this.mapper.writeValue(dataFile, dataList);

        if (!isSuccess) {
            System.out.printf("Tidak terdapat data dengan id %d dalam data.csv\n", id);
            return;
        } else {
            System.out.println("Sukses menghapus data");
        }
    }
    @SneakyThrows
    @Override
    public void update(int id, T data) {
        if(!directory.exists()){
            directory.mkdir();
        }
        if(!dataFile.exists()){
            System.out.printf("Tidak ada file %s. Silahkan masukkan data terlebih dahulu\n", this.entityName + ".csv");
            return;
        }

        var dataList = getList();

        T target = null;

        for(int i = 0; i<dataList.size(); i++){
            var dataItem = dataList.get(i);
            var dataId = getId(dataItem);
            if(dataId.getClass() == Integer.class){
                var intId = (Integer) dataId;
                if(intId == id){
                    target = dataItem;
                }
            } else if(dataId.getClass() == Double.class){
                var doubleId = (Double) dataId;
                if(doubleId == id){
                    target = dataItem;
                }
            } else if(dataId.getClass() == String.class){
                var strId = (String) dataId;
                if(strId.equalsIgnoreCase(id + "")){
                    target = dataItem;
                }
            }
            if(target != null)break;
        }

        if(target == null){
            System.out.printf("Tidak terdapat data dengan id %d dalam data.csv\n", id);
            return;
        }

        var fields = data.getClass().getDeclaredFields();
        for(var field : fields){
            try{
                field.setAccessible(true);
                var value = field.get(data);
                if(value != null){
                    field.set(target, value);
                }
                field.setAccessible(false);
            } catch(Exception e){
                System.out.println("Exception occurred with message " + e.getMessage());
            }
        }

        this.mapper.writeValue(dataFile, dataList);

        System.out.println("Sukses mengupdate data");
    }
    

    private void updateIdForNextData(T data, List<T> dataList) throws IllegalAccessException {
        var latestData = dataList.get(dataList.size() - 1);
        var fields = data.getClass().getDeclaredFields();
        for (var field : fields) {
            try {
                field.setAccessible(true);
                if (field.isAnnotationPresent(Id.class)) {
                    setIdFieldValue(data, field, getIdFieldValue(latestData, field) + 1);
                }
                field.setAccessible(false);
            } catch (Exception e) {
                System.out.println("Exception occurred with message " + e.getMessage());
            }
        }
    }

    private void setIdFieldValue(T data, java.lang.reflect.Field field, int value) throws IllegalAccessException {
        if (field.getType() == Long.class) {
            field.set(data, (long) value);
        } else if (field.getType() == Integer.class) {
            field.set(data, value);
        } else if (field.getType() == String.class) {
            field.set(data, String.valueOf(value));
        }
    }

    private int getIdFieldValue(T data, java.lang.reflect.Field field) throws IllegalAccessException {
        Object value = field.get(data);
        return (value != null) ? Integer.parseInt(value.toString()) : 0;
    }


    @SneakyThrows
    private Object getId(T data) {
        var fields = data.getClass().getDeclaredFields();
        Object value = null;
        for (var field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Id.class)) {
                value = field.get(data);
            }
            field.setAccessible(false);
        }
        if(value != null){
            return value;
        } else {
            throw new Exception("@Id tidak ditemukan on method getId(T data)");
        }
    }
}
