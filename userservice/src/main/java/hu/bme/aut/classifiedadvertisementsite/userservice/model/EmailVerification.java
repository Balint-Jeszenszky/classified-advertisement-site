package hu.bme.aut.classifiedadvertisementsite.userservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "EmailVerification")
@Table(name = "email_verification")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "email_verification_seq")
    @SequenceGenerator(name = "email_verification_seq", sequenceName = "email_verification_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    private Integer id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "key", nullable = false, unique = true)
    private String key;
}
