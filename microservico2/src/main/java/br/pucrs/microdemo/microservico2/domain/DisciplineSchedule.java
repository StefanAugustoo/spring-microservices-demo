package br.pucrs.microdemo.microservico2.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(
        name = "discipline_schedules",
        uniqueConstraints = @UniqueConstraint(columnNames = {"codigo", "nome", "horario"})
)
@Getter
@Setter
@NoArgsConstructor
@ToString
public class DisciplineSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String codigo;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String horario; // A-G

    public DisciplineSchedule(String codigo, String nome, String horario) {
        this.codigo = codigo;
        this.nome = nome;
        this.horario = horario;
    }
}

