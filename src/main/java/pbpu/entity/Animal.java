package pbpu.entity;

import lombok.Data;
import pbpu.annotation.Entity;
import pbpu.annotation.Id;

@Entity
@Data
public class Animal {
    
    @Id
    private Long id;

    private String namaHewan;
}
