package pbpu;

import java.util.List;

import pbpu.annotation.Crud;
import pbpu.annotation.Inject;
import pbpu.annotation.JsonDatabase;
import pbpu.database.CoreDatabase;
import pbpu.database.Operation;
import pbpu.entity.Human;

@Crud(type = Human.class)
public class HumanOperation implements Operation<Human>{

    @Inject(type = JsonDatabase.class)
    private CoreDatabase<Human> database;

    @Override
    public void insert(Human data) {
        this.database.create(data);
    }

    @Override
    public void update(int id, Human data) {
        this.database.update(id, data);
    }

    @Override
    public Human get(int id) {
        return this.database.get(id);
    }

    @Override
    public List<Human> getList() {
        return this.database.getList();
    }

    @Override
    public void delete(int id) {
        this.database.delete(id);
    }
    
}
