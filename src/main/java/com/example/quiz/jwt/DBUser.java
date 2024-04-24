package com.example.quiz.jwt;

import com.example.quiz.model.Player;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.MoreObjects;
import org.springframework.data.domain.Persistable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "users")
public class DBUser implements Serializable, Persistable<Long> {

    private static final long serialVersionUID = -5554304839188669754L;

    protected Long id;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    public Long getId() {
        return id;
    }

    protected void setId(final Long id) {
        this.id = id;
    }

    @Override
    @Transient
    public boolean isNew() {
        return null == getId();
    }

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JsonIgnore
    private Player player;


    @Column(nullable = false, unique = true, length = 60)
    private String name;

    @JsonIgnore
    @Column(nullable = false, length = 255)
    private String password;

    protected DBUser() {
    }

    @Transient
    public static String hashPassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public String toString() {
        return "DBUser{" +
                "id=" + id +
                ", player=" + player +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getName() {
        return name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Implement PrePersist lifecycle callback
    @PrePersist
    public void prePersist() {
        if (player == null) {
            player = new Player();
            player.setUser(this);
        }
    }

    // Implement PreRemove lifecycle callback
    @PreRemove
    public void preRemove() {
        if (player != null) {
            player.setUser(null);
        }
    }


    public static final class UserBuilder {
        private Long id;
        private Player player;
        private String name;
        private String password;

        private UserBuilder() {
        }

        public static UserBuilder anUser() {
            return new UserBuilder();
        }

        public UserBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UserBuilder player(Player player) {
            this.player = player;
            return this;
        }

        public UserBuilder name(String name) {
            this.name = name;
            return this;
        }

        public UserBuilder password(String password) {
            this.password = password;
            return this;
        }

        public DBUser build() {
            DBUser dBUser = new DBUser();
            dBUser.setId(id);
            dBUser.setPlayer(player);
            dBUser.setName(name);
            dBUser.setPassword(password);
            return dBUser;
        }
    }
}
