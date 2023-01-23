package io.github.pollanz74.gs.kafka.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Builder
@ToString
@Setter(AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "LH_UTENTE_BIS")
public class UtenteEntity {

    @Id
    @GeneratedValue(generator = "utente_seq")
    @SequenceGenerator(name = "utente_seq", sequenceName = "SQ_LH_UTENTE_BIS", allocationSize = 1)
    @Column(name = "ID_UTENTE", unique = true, nullable = false)
    @EqualsAndHashCode.Include
    private Long idUtente;

    @EqualsAndHashCode.Include
    @Column(name = "CF", unique = true, length = 16)
    private String cf;

    @Column(name = "COGNOME", length = 64)
    private String cognome;

    @Column(name = "NOME", length = 64)
    private String nome;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATA_NASCITA")
    private Date dataNascita;

    @Setter(AccessLevel.PUBLIC)
    @Column(name = "F_ATTIVO")
    private boolean attivo = false;

    @Column(name = "DATA_AGGIORNAMENTO", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataAggiornamento;

}
