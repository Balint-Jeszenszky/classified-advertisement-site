package hu.bme.aut.classifiedadvertisementsite.userservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity(name = "PasswordReset")
@Table(name = "password_reset")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordReset {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "password_reset_seq")
    @SequenceGenerator(name = "password_reset_seq", sequenceName = "password_reset_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "expiration", updatable = false)
    private Date expiration;

    @Column(name = "key", updatable = false, unique = true)
    private String key;
}
