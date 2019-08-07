package entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table
@Entity
public class Authorities {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "server_auth")
    @SequenceGenerator(name="server_auth", sequenceName = "seq_auth", allocationSize = 1)
    private Long id;

    private long discord_id;
}
