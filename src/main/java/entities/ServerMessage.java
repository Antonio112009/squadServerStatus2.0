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
public class ServerMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "servmes_generator")
    @SequenceGenerator(name="servmes_generator", sequenceName = "seq_servmes", allocationSize = 1)
    private Long id;

    private String server_status;

    private String server_id;

    private String players;

    private String map;

    private String join_server;



}
