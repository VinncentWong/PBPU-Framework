package pbpu;

import java.util.List;

import pbpu.annotation.Crud;
import pbpu.annotation.Inject;
import pbpu.annotation.JsonDatabase;
import pbpu.database.CoreDatabase;
import pbpu.database.Operation;
import pbpu.entity.Animal;

@Crud(type = Animal.class)
public class AnimalOperation implements Operation<Animal>{

    @Inject(type = JsonDatabase.class)
    private CoreDatabase<Animal> database;

    @Override
    public void insert(Animal data) {
        this.database.create(data);
    }

    @Override
    public void update(int id, Animal data) {
        this.database.update(id, data);
    }

    @Override
    public Animal get(int id) {
        return this.database.get(id);
    }

    @Override
    public List<Animal> getList() {
        return this.database.getList();
    }

    @Override
    public void delete(int id) {
        this.database.delete(id);
    }

    
}
