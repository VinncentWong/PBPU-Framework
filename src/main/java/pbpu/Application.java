package pbpu;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import lombok.SneakyThrows;
import pbpu.annotation.Entity;
import pbpu.annotation.Id;
import pbpu.annotation.Inject;
import pbpu.annotation.JsonDatabase;
import pbpu.context.ApplicationContext;
import pbpu.database.Operation;
import pbpu.injector.DatabaseInjector;
import pbpu.jackson.JacksonMapper;
import pbpu.util.LineUtil;

public class Application {

    private LineUtil lineUtil = LineUtil.getInstance();

    private List<Class<?>> entities;

    private List<Class<?>> jsonDatabases;

    private List<Class<?>> injects;

    private ObjectMapper mapper;

    private Application() {
        this.mapper = JacksonMapper.getInstance();
    }

    public static Application getInstance() {
        return new Application();
    }

    private void init(ScanResult result) {
        this.entities = scanEntity(result);
        this.jsonDatabases = scanJsonRepository(result);
        this.injects = scanInject(result);
    }

    /***
     * run() will start the terminal to print
     */
    @SuppressWarnings("unchecked")
    @SneakyThrows
    public void run() {
        try (
                var result = new ClassGraph()
                        // .verbose()
                        .enableAllInfo()
                        .scan();

                var input = new Scanner(System.in);) {

            // make sure to always call init first!
            init(result);

            // inject proxies into @Inject
            inject(result);

            boolean continueLoop = true;
            lineUtil.printLine();
            do {
                System.out.println("SELAMAT DATANG DI SISTEM MANIPULASI ENTITAS");
                System.out.println("Silahkan masukkan operasi yang Anda ingin lakukan");
                System.out.print("""
                                1. Insert
                                2. Get
                                3. GetList
                                4. Delete
                                5. Update
                                6. Exit
                        """);
                System.out.print("Pilihan Anda: ");
                int pilihan = input.nextInt();
                switch (pilihan) {
                    case 1 -> {
                        int choice = getEntityChoice(input);
                        switch (getDatabaseType(input)) {
                            case 1 -> {
                                var targetDatabase = JsonDatabase.class;
                                var targetType = this.entities.get(choice);
                                System.out.println("targetDatabase = " + targetDatabase + " target type " + targetType);
                                var instance = ApplicationContext
                                        .getInstance()
                                        .getComponent(targetType)
                                        .get(targetDatabase);
                                if (instance != null) {
                                    var operation = (Operation<Object>) instance;
                                    var targetInstance = targetType.getDeclaredConstructor().newInstance();
                                    var fields = targetInstance.getClass().getDeclaredFields();
                                    this.showEntityInsertOrUpdate(input, targetInstance, fields);
                                    operation.insert(targetInstance);
                                } else {
                                    System.out.println("Instance not found");
                                }
                            }
                            case 2 -> {

                            }
                        }
                    }
                    case 2 -> {
                        int choice = getEntityChoice(input);
                        switch (getDatabaseType(input)) {
                            case 1 -> {
                                var instance = ApplicationContext.getInstance()
                                        .getComponent(this.entities.get(choice))
                                        .get(JsonDatabase.class);

                                if (instance != null) {
                                    var operation = (Operation<Object>) instance;
                                    var searchId = getIdChoice(input);
                                    var data = operation.get(searchId);
                                    if (data != null) {
                                        System.out.printf("Sukses mendapatkan data %s\n",
                                                this.mapper.writeValueAsString(data));
                                    } else {
                                        System.out.printf("Data tidak ditemukan dengan id %d\n", searchId);
                                    }
                                } else {
                                    System.out.println("Instance not found");
                                }
                            }
                            case 2 -> {

                            }
                        }
                    }
                    case 3 -> {
                        int choice = getEntityChoice(input);
                        switch (getDatabaseType(input)) {
                            case 1 -> {
                                var instance = ApplicationContext
                                        .getInstance()
                                        .getComponent(this.entities.get(choice))
                                        .get(JsonDatabase.class);

                                if (instance != null) {
                                    var operation = (Operation<Object>) instance;
                                    var data = operation.getList();
                                    if (data != null) {
                                        System.out.printf("Sukses mendapatkan data %s\n",
                                                this.mapper.writeValueAsString(data));
                                    } else if (data == null || data.size() == 0) {
                                        System.out.println("Data tidak ditemukan, data berisi [] kosong");
                                    }
                                } else {
                                    System.out.println("Instance not found");
                                }
                            }
                            case 2 -> {

                            }
                        }
                    }
                    case 4 -> {
                        int choice = getEntityChoice(input);
                        switch (getDatabaseType(input)) {
                            case 1 -> {
                                var instance = ApplicationContext
                                        .getInstance()
                                        .getComponent(this.entities.get(choice))
                                        .get(JsonDatabase.class);

                                if (instance != null) {
                                    var operation = (Operation<Object>) instance;
                                    var deleteId = this.getIdChoice(input);
                                    operation.delete(deleteId);
                                } else {
                                    System.out.println("Instance not found");
                                }
                            }
                            case 2 -> {

                            }
                        }
                    }
                    case 5 -> {
                        int choice = getEntityChoice(input);
                        switch (getDatabaseType(input)) {
                            case 1 -> {

                                var searchId = this.getIdChoice(input);

                                var targetDatabase = JsonDatabase.class;
                                var targetType = this.entities.get(choice);
                                System.out.println("targetDatabase = " + targetDatabase + " target type " + targetType);
                                var instance = ApplicationContext
                                        .getInstance()
                                        .getComponent(targetType)
                                        .get(targetDatabase);
                                if (instance != null) {
                                    var operation = (Operation<Object>) instance;
                                    var targetInstance = targetType.getDeclaredConstructor().newInstance();
                                    var fields = targetInstance.getClass().getDeclaredFields();
                                    this.showEntityInsertOrUpdate(input, targetInstance, fields);
                                    operation.update(searchId, targetInstance);
                                } else {
                                    System.out.println("Instance not found");
                                }
                            }
                            case 2 -> {

                            }
                        }
                    }
                    case 6 -> {
                        continueLoop = false;
                    }
                }

            } while (continueLoop);
            this.lineUtil.printLine();
        }

    }

    private int getEntityChoice(Scanner input) {
        this.lineUtil.printLine();
        if (this.entities.size() > 0) {
            System.out.println("Silahkan pilih entitas yang ingin Anda operasikan: ");
            for (int i = 0; i < this.entities.size(); i++) {
                var entity = this.entities.get(i);
                var entityName = entity.getSimpleName();
                var additionalName = entity.getAnnotation(Entity.class);
                if (!(additionalName.name().isBlank() || additionalName.name().isEmpty())) {
                    entityName = additionalName.name();
                }
                System.out.printf("%d. %s\n", (i + 1), entityName);
            }
            this.lineUtil.printLine();
            System.out.print("Pilihan Anda: ");
            int choice = input.nextInt();
            return choice - 1;
        } else {
            System.out.println("Tidak ada entitas ditemukan, pastikan Anda menandai kelas Anda dengan @Entity");
            this.lineUtil.printLine();
        }
        return -1;
    }

    private int getDatabaseType(Scanner input) {
        this.lineUtil.printLine();
        System.out.println("Silahkan pilih jenis database yang akan digunakan: ");
        System.out.print("""
                1. JSON
                2. CSV
                3. Text
                """);
        this.lineUtil.printLine();
        System.out.print("Pilihan Anda: ");
        int choice = input.nextInt();
        return choice;
    }

    private int getIdChoice(Scanner input) {
        this.lineUtil.printLine();
        System.out.print("Masukkan id yang Anda ingin cari: ");
        int choice = input.nextInt();
        this.lineUtil.printLine();
        return choice;
    }

    @SneakyThrows
    private <T> void showEntityInsertOrUpdate(Scanner input, Object targetInstance, Field[] fields) {
        for (var field : fields) {
            if (!field.isAnnotationPresent(Id.class)) {
                var fieldType = field.getType();
                System.out.printf("Masukkan nilai attribute %s: ", field.getName());
                Object value;
                if (fieldType == Integer.class) {
                    value = input.nextInt();
                } else if (fieldType == Double.class) {
                    value = input.nextDouble();
                } else if (fieldType == Boolean.class) {
                    value = input.nextBoolean();
                } else {
                    value = input.next();
                }
                field.setAccessible(true);
                field.set(targetInstance, value);
                field.setAccessible(false);
            }
        }
    }

    /***
     * Scan @Entity on entire application
     */
    private List<Class<?>> scanEntity(ScanResult result) {
        var classes = result.getClassesWithAnnotation(Entity.class);
        var list = new ArrayList<Class<?>>();
        for (var clazz : classes) {
            var currentClass = clazz.loadClass();
            list.add(currentClass);

        }
        return list;
    }

    /***
     * Scan @JsonRepository on entire application
     */
    private List<Class<?>> scanJsonRepository(ScanResult result) {
        var classes = result.getClassesWithAnnotation(JsonDatabase.class);
        var list = new ArrayList<Class<?>>();
        for (var clazz : classes) {
            var currentClass = clazz.loadClass();
            list.add(currentClass);

        }
        return list;
    }

    /***
     * Scan @Inject on entire application
     */
    private List<Class<?>> scanInject(ScanResult result) {
        var classes = result.getClassesWithFieldAnnotation(Inject.class);
        var list = new ArrayList<Class<?>>();
        for (var clazz : classes) {
            var currentClass = clazz.loadClass();
            list.add(currentClass);
        }

        System.out.println("injects = " + classes);
        return list;
    }

    private void inject(ScanResult result) {
        var dbInjector = DatabaseInjector
                .builder()
                .databases(this.injects)
                .build();

        dbInjector.injectProxy();
    }
}
