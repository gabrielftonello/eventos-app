package eventos.service;

import eventos.model.Evento;
import eventos.model.Participante;
import eventos.repository.EventoRepository;
import eventos.repository.ParticipanteRepository;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventoService {

    @Autowired
    private ParticipanteRepository participanteRepository;

    @Autowired
    private EventoRepository eventoRepository;


    public EventoService(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    public List<Evento> getAllEventos() {
        return eventoRepository.findAll();
    }

    public Optional<Participante> getParticipante(Long id) {
        return participanteRepository.findById(id);
    }

    public Optional<Evento> getEvento(Long id) {
        return eventoRepository.findById(id);
    }

    public List<Evento> filtrarEventos(String nome, Date dataInicio, Date dataFim) {
        List<Evento> todosEventos = eventoRepository.findAll();

        return todosEventos.stream()
                .filter(evento -> (nome == null || nome.isEmpty() || evento.getNome().toLowerCase().contains(nome.toLowerCase())))
                .filter(evento -> (dataInicio == null || !evento.getDataInicio().before(dataInicio) || evento.getDataInicio().equals(dataInicio)))
                .filter(evento -> (dataFim == null || !evento.getDataFim().after(dataFim) || evento.getDataFim().equals(dataFim)))
                .collect(Collectors.toList());
    }


    public void adicionarEvento(Evento evento) {
        eventoRepository.save(evento);
    }

    public void excluirEvento(Long eventoId) {
        eventoRepository.deleteById(eventoId);
    }



    public void adicionarParticipante(Long eventoId, Participante participante) {
        Evento evento = eventoRepository.findById(eventoId).orElseThrow(() -> new IllegalArgumentException("Evento não encontrado"));

        boolean cpfExiste = participanteRepository.findAll()
                .stream()
                .anyMatch(p -> p.getCpf().equals(participante.getCpf()) && !p.getId().equals(participante.getId()));

        if(cpfExiste) {
            throw new IllegalArgumentException("CPF duplicado! Já existe um participante com este CPF no evento.");
        }
        participante.setEvento(evento);
        participanteRepository.save(participante);

    }

    public void editarParticipante(Long eventoId, Participante participante) {

        if(eventoId != 0) {
            boolean cpfExiste = participanteRepository.findAll()
                    .stream()
                    .anyMatch(p -> p.getCpf().equals(participante.getCpf()) && !p.getId().equals(participante.getId()));

            if(cpfExiste) {
                throw new IllegalArgumentException("CPF duplicado! Já existe um participante com este CPF no evento.");
            }
        }

        participanteRepository.save(participante);

    }

    public void excluirParticipante(Long participanteId) {
        Optional<Participante> participanteOptional = participanteRepository.findById(participanteId);

        if (participanteOptional.isPresent()) {
            Participante participante = participanteOptional.get();
            participante.setEvento(null);
            editarParticipante(0L,participante);
            participanteRepository.delete(participante);
        } else {
            throw new EntityNotFoundException("Participante não encontrado com ID: " + participanteId);
        }
    }


    public void atualizarEvento(Evento evento) {
        eventoRepository.save(evento);
    }

}
