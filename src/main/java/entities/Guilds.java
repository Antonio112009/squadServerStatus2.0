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
@ToString
@Table
@Entity
public class Guilds {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "guild_generator")
    @SequenceGenerator(name="guild_generator", sequenceName = "seq_guild", allocationSize = 1)
    private Long id;

    private long discord_id;

    private long channel_id;


    // Language
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "language_id", unique = true)
    private Language language;



    @OneToMany(
            mappedBy = "guild",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @LazyCollection(LazyCollectionOption.FALSE)
    List<MessageServer> messageServers = new ArrayList<>();


    public void addMessage(MessageServer messageServer) {
        messageServers.add(messageServer);
        messageServer.setGuild(this);
    }

    public void removeMessage(MessageServer messageServer) {
        messageServers.remove(messageServer);
        messageServer.setGuild(null);
    }



    // Who can access bot?
    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Authorities> authorities = new ArrayList<>();

    public void addAuthority(Authorities authority) {
        authorities.add(authority);
    }

    public void removeAuthority(Authorities authority) {
        authorities.remove(authority);
    }
}
