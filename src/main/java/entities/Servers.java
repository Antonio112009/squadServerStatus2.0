package entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "messageServers")
@Table
@Entity
public class Servers {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "server_generator")
    @SequenceGenerator(name="server_generator", sequenceName = "seq_server", allocationSize = 1)
    private Long id;

    private String server_name;

    private String server_ip;

    private int server_port;

    private String battlemetrics_id;

    @OneToMany(
            mappedBy = "server",
            cascade = CascadeType.ALL
    )
    @LazyCollection(LazyCollectionOption.FALSE)
    List<MessageServer> messageServers = new ArrayList<>();

    /*
    As you see we need to do something like "recursion" below
     */
    public void addMessage(MessageServer messageServer) {
        messageServers.add(messageServer);
        messageServer.setServer(this);
    }

    public void removeMessage(MessageServer messageServer) {
        messageServers.remove(messageServer);
        messageServer.setServer(null);
    }
}
