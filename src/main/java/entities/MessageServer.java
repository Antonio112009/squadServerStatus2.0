package entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "guild")
@Table
@Entity
public class MessageServer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "messerv_generator")
    @SequenceGenerator(name="messerv_generator", sequenceName = "seq_messerv", allocationSize = 1)
    private Long id;

    private long message_id;

    @ManyToOne()
    @JoinColumn(name="guild_id")
    @LazyCollection(LazyCollectionOption.FALSE)
    private Guilds guild;


    // message - server
    @ManyToOne(cascade = CascadeType.ALL
    )
    @JoinColumn(name="server_id")
    @LazyCollection(LazyCollectionOption.FALSE)
    private Servers server;
}
