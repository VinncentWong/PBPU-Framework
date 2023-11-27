package pbpu.entity;

import lombok.Data;
import pbpu.annotation.Entity;
import pbpu.annotation.Id;

@Data
@Entity(name = "lemari")
public class Lemari {

    @Id
    private Long id;

    private String ukuranLemari;
}
