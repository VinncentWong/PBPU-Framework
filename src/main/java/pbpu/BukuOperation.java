package pbpu;

import java.util.List;

import pbpu.annotation.Crud;
import pbpu.annotation.Inject;
import pbpu.annotation.JsonDatabase;
import pbpu.database.CoreDatabase;
import pbpu.database.Operation;
import pbpu.entity.Buku;

@Crud(type = Buku.class)
public class BukuOperation implements Operation<Buku> {

    @Inject(type = JsonDatabase.class)
    private CoreDatabase<Buku> database;

    @Override
    public void insert(Buku data) {
        this.database.create(data);
    }

    @Override
    public void update(int id, Buku data) {
        this.database.update(id, data);
    }

    @Override
    public Buku get(int id) {
        return this.database.get(id);
    }

    @Override
    public List<Buku> getList() {
        return this.database.getList();
    }

    @Override
    public void delete(int id) {
        this.database.delete(id);
    }
}
