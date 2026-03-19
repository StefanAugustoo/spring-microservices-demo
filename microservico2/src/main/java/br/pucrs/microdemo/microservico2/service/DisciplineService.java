package br.pucrs.microdemo.microservico2.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.stereotype.Service;

import br.pucrs.microdemo.microservico2.domain.DisciplineSchedule;
import br.pucrs.microdemo.microservico2.repository.DisciplineScheduleRepository;

@Service
public class DisciplineService {

    public record DisciplineSummary(String codigo, String nome) {
    }

    public record DisciplineWithHorarios(String codigo, String nome, List<String> horarios) {
    }

    public static class DisciplineConflictException extends RuntimeException {
        public DisciplineConflictException(String message) {
            super(message);
        }
    }

    private final DisciplineScheduleRepository repository;

    public DisciplineService(DisciplineScheduleRepository repository) {
        this.repository = repository;
    }

    public DisciplineWithHorarios register(String codigo, String nome, List<String> horarios) {
        String codigoNorm = normalizeCodigo(codigo);
        String nomeNorm = normalizeNome(nome);

        if (horarios == null || horarios.isEmpty()) {
            throw new IllegalArgumentException("A lista 'horarios' não pode estar vazia.");
        }

        List<String> horariosNorm = normalizeHorarios(horarios);

        // Se o mesmo código for cadastrado com nomes diferentes, o enunciado não define.
        // Para evitar ambiguidades nas consultas /enrollment, bloqueamos essa situação.
        List<DisciplineSchedule> existing = repository.findAllByCodigo(codigoNorm);
        Set<String> nomesExistentes = new LinkedHashSet<>();
        for (DisciplineSchedule d : existing) {
            nomesExistentes.add(d.getNome());
        }
        if (!nomesExistentes.isEmpty() && !(nomesExistentes.size() == 1 && nomesExistentes.contains(nomeNorm))) {
            throw new DisciplineConflictException("O código '" + codigoNorm + "' já foi cadastrado com outro nome.");
        }

        for (String horarioNorm : horariosNorm) {
            // Como já validamos o nome para aquele código, podemos checar apenas por (codigo, horario).
            if (!repository.existsByCodigoAndHorario(codigoNorm, horarioNorm)) {
                repository.save(new DisciplineSchedule(codigoNorm, nomeNorm, horarioNorm));
            }
        }

        List<String> horariosPersistidos = listDistinctHorarios(codigoNorm);
        return new DisciplineWithHorarios(codigoNorm, nomeNorm, horariosPersistidos);
    }

    public List<DisciplineSummary> listDistinct() {
        List<DisciplineSchedule> all = repository.findAll();

        // Distinct por (codigo, nome). Mantém ordem de inserção aproximada.
        Set<String> seen = new LinkedHashSet<>();
        List<DisciplineSummary> out = new ArrayList<>();
        for (DisciplineSchedule d : all) {
            String key = d.getCodigo() + "|" + d.getNome();
            if (seen.add(key)) {
                out.add(new DisciplineSummary(d.getCodigo(), d.getNome()));
            }
        }
        return out;
    }

    public DisciplineSummary getByCodigo(String codigo) {
        String codigoNorm = normalizeCodigo(codigo);
        List<DisciplineSchedule> all = repository.findAllByCodigo(codigoNorm);
        if (all.isEmpty()) {
            return null;
        }

        // Esperamos que exista apenas um nome para o código (bloqueado no cadastro).
        DisciplineSchedule first = all.get(0);
        return new DisciplineSummary(first.getCodigo(), first.getNome());
    }

    public List<String> listHorarios(String codigo) {
        String codigoNorm = normalizeCodigo(codigo);
        return listDistinctHorarios(codigoNorm);
    }

    private List<String> listDistinctHorarios(String codigoNorm) {
        List<DisciplineSchedule> all = repository.findAllByCodigo(codigoNorm);
        Set<String> horarios = new LinkedHashSet<>();
        for (DisciplineSchedule d : all) {
            horarios.add(d.getHorario());
        }
        return horarios.stream()
                .sorted(Comparator.comparingInt(h -> h.charAt(0)))
                .toList();
    }

    private String normalizeCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("O campo 'codigo' é obrigatório.");
        }
        return codigo.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O campo 'nome' é obrigatório.");
        }
        return nome.trim();
    }

    private List<String> normalizeHorarios(List<String> horarios) {
        List<String> normalized = new ArrayList<>();
        for (String h : horarios) {
            if (h == null || h.isBlank()) {
                throw new IllegalArgumentException("Horários não podem ser vazios.");
            }
            String hNorm = h.trim().toUpperCase(Locale.ROOT);
            if (!hNorm.matches("^[A-G]$")) {
                throw new IllegalArgumentException("Horário inválido '" + h + "'. Use apenas A-G.");
            }
            normalized.add(hNorm);
        }
        // remove duplicados mantendo ordem de entrada
        return normalized.stream().distinct().toList();
    }
}

