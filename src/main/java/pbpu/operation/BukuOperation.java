package pbpu.operation;

import java.util.List;

import pbpu.annotation.Crud;
import pbpu.annotation.CsvDatabase;
import pbpu.annotation.Inject;
import pbpu.database.CoreDatabase;
import pbpu.database.Operation;
import pbpu.entity.Buku;

@Crud(type = Buku.class)
public class BukuOperation implements Operation<Buku> {
    @Inject(type =CsvDatabase.class)
    private CoreDatabase<Buku> db;

    @Override
    public void insert(Buku data) {
        db.create(data);
    }

    @Override
    public void update(int id, Buku data) {
        db.update(id, data);
    }

    @Override
    public Buku get(int id) {
        return db.get(id);
    }

    @Override
    public List<Buku> getList() {
        return db.getList();
    }

    @Override
    public void delete(int id) {
         db.delete(id);
    }
    
}
