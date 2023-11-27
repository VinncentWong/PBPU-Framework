package pbpu.entity;

import lombok.Data;
import pbpu.annotation.Entity;
import pbpu.annotation.Id;

@Data
@Entity(name = "BukaBukuMu")
public class Buku {

    @Id
    private Long id;
    
    private String name;
}
