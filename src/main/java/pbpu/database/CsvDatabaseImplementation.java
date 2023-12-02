package pbpu.database;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.lang.reflect.Type;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;

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
    }

    @SneakyThrows
    @Override
    public void create(T data) {
        try {
            if (!directory.exists()) {
                directory.mkdirs();
            }

            if (!dataFile.exists()) {
                dataFile.createNewFile();
                this.mapper.writeValue(dataFile, new ArrayList<>());
            }

            var dataList = this.mapper.readValue(dataFile, new TypeReference<List<T>>() {
                @Override
                public Type getType() {
                    return mapper.getTypeFactory().constructCollectionType(List.class, data.getClass());
                }
            });

            if (dataList.isEmpty()) {
                var idExist = false;
                var fields = data.getClass().getDeclaredFields();
                System.out.println("sebelum isi field");
                for (var field : fields) {
                    field.setAccessible(true);
                    if (field.isAnnotationPresent(Id.class)) {
                        idExist = true;
                        if (field.getType() == Long.class) {
                            field.set(data, 1L);
                        } else if (field.getType() == Integer.class) {
                            field.set(data, 1);
                        } else if (field.getType() == String.class) {
                            field.set(data, 1 + "");
                        }
                    }
                    field.setAccessible(false);
                }
                if (!idExist) {
                    throw new Exception("@Id tidak ditemukan");
                }
            } else {
                var idExist = false;
                var fields = data.getClass().getDeclaredFields();
                var latestData = dataList.get(dataList.size() - 1);
                for (var field : fields) {
                    try {
                        field.setAccessible(true);
                        if (field.isAnnotationPresent(Id.class)) {
                            idExist = true;
                            if (field.getType() == Long.class) {
                                field.set(data, (Long) getId(latestData) + 1L);
                            } else if (field.getType() == String.class) {
                                field.set(data, ((String) getId(latestData) + 1) + "");
                            } else if (field.getType() == Integer.class) {
                                field.set(data, (Integer) getId(latestData) + 1);
                            }
                        }
                        field.setAccessible(false);
                    } catch (Exception e) {
                        System.out.println("Exception occurred with message " + e.getMessage());
                    }
                }
                if (!idExist) {
                    throw new Exception("@Id tidak ditemukan");
                }
            }
            dataList.add(data);
            this.mapper.writeValue(dataFile, dataList);
            System.out.printf("Sukses menambahkan data %s: %s\n", this.entityName,
                    this.mapper.writeValueAsString(data));
        } catch (Exception e) {
            System.out.println("Error occurred with message " + e.getMessage());
        }
    }

    @SneakyThrows
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

    @SneakyThrows
    @Override
    public List<T> getList() {
        if (!directory.exists()) {
            directory.createNewFile();
        }

        if (!dataFile.exists()) {
            dataFile.createNewFile();
            this.mapper.writeValue(dataFile, new ArrayList<>());
        }

        return this.mapper.readValue(dataFile, new TypeReference<List<T>>() {
        });
    }

    @SneakyThrows
    @Override
    public void delete(int id) {
        boolean isExist = checkIfExist();
        if(!isExist)return;

        var dataList = this.mapper.readValue(dataFile, new TypeReference<List<T>>() {
        });

        if (dataList.isEmpty()) {
            System.out.println("data.csv berisi [] kosong");
            return;
        }

        boolean dataFound = false;
        for (int i = 0; i < dataList.size(); i++) {
            var dataId = getId(dataList.get(i));
            if (dataId.getClass() == Integer.class) {
                var intId = (Integer) dataId;
                if (intId == id) {
                    dataList.remove(i);
                    dataFound = true;
                }
            } else if (dataId.getClass() == Double.class) {
                var doubleId = (Double) dataId;
                if (doubleId == id) {
                    dataList.remove(i);
                    dataFound = true;
                }
            } else if (dataId.getClass() == String.class) {
                var strId = (String) dataId;
                if (strId.equalsIgnoreCase(id + "")) {
                    dataList.remove(i);
                    dataFound = true;
                }
            }
        }

        this.mapper.writeValue(dataFile, dataList);

        if (!dataFound) {
            System.out.printf("Tidak terdapat data dengan id %d dalam data.csv\n", id);
            return;
        } else {
            System.out.println("Sukses menghapus data");
        }
    }

    @SneakyThrows
    @Override
    public void update(int id, T data) {
        boolean isExist = checkIfExist();
        if(!isExist)return;

        var dataList = this.getList();

        int index = -1;
        T targetBook = null;
        for (int i = 0; i < dataList.size(); i++) {
            var dataId = getId(dataList.get(i));
            if (dataId.getClass() == Integer.class) {
                var intId = (Integer) dataId;
                if (intId == id) {
                    targetBook = dataList.get(i);
                    index = i;
                }
            } else if (dataId.getClass() == Double.class) {
                var doubleId = (Double) dataId;
                if (doubleId == id) {
                    targetBook = dataList.get(i);
                    index = i;
                }
            } else if (dataId.getClass() == String.class) {
                var strId = (String) dataId;
                if (strId.equalsIgnoreCase(id + "")) {
                    targetBook = dataList.get(i);
                    index = i;
                }
            }
        }

        if (targetBook == null) {
            System.out.println("Buku tidak ditemukan");
            return;
        }

        var field = getFieldAnnotateWithId(data);

        field.set(data, id);

        dataList.set(index, data);

        this.mapper.writeValue(dataFile, dataList);

        System.out.println("Sukses mengubah data buku " + this.mapper.writeValueAsString(data));
    }

    private String convertToSnakeCase(String str) {
        // Empty String
        String result = "";

        // Append first character(in lower case)
        // to result string
        char c = str.charAt(0);
        result = result + Character.toLowerCase(c);

        // Traverse the string from
        // ist index to last index
        for (int i = 1; i < str.length(); i++) {

            char ch = str.charAt(i);

            // Check if the character is upper case
            // then append '_' and such character
            // (in lower case) to result string
            if (Character.isUpperCase(ch)) {
                result = result + '_';
                result = result
                        + Character.toLowerCase(ch);
            }

            // If the character is lower case then
            // add such character into result string
            else {
                result = result + ch;
            }
        }

        // return the result
        return result;
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

    @SneakyThrows
    private Field getFieldAnnotateWithId(T data) {
        var obj = data.getClass();
        for (var field : obj.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                return field;
            }
        }
        throw new Exception("@Id tidak ditemukan on method getFieldAnnotateWithId(T data)");
    }

    @SneakyThrows
    private boolean checkIfExist(){
        if (!directory.exists()) {
            directory.createNewFile();
        }

        if (!dataFile.exists()) {
            System.out.printf("Tidak ada file %s. Silahkan masukkan data terlebih dahulu\n", this.entityName + ".csv");
            return false;
        }

        return true;
    }

}
