package pbpu.entity;

import lombok.Data;
import pbpu.annotation.Entity;
import pbpu.annotation.Id;

@Data
@Entity
public class Human {
    
    @Id
    private Long id;

    private String namaManusia;

    private Integer umur;
}
