package eventos.controllers;

import eventos.model.Presenca;
import eventos.model.Evento;
import eventos.model.Participante;
import eventos.service.EventoService;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Named("eventoController")
@Component
@ViewScoped
@NoArgsConstructor(force = true)
public class EventoController implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Evento> eventos;
    private List<Evento> eventosFiltrados;
    private String filtroNome;
    private Date filtroDataInicio;
    private Date filtroDataFim;

    private Evento novoEvento = new Evento();
    private Evento eventoSelecionado;
    private Participante participanteSelecionado;
    private Presenca novaPresenca = new Presenca();
    private Participante novoParticipante = new Participante();

    private final EventoService eventoService;

    @Autowired
    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
        carregarEventos();
    }

    public List<Evento> getEventos() {
        return eventos;
    }

    public void setEventos(List<Evento> eventos) {
        this.eventos = eventos;
    }

    public List<Evento> getEventosFiltrados() {
        return eventosFiltrados;
    }

    public void setEventosFiltrados(List<Evento> eventosFiltrados) {
        this.eventosFiltrados = eventosFiltrados;
    }

    public String getFiltroNome() {
        return filtroNome;
    }

    public void setFiltroNome(String filtroNome) {
        this.filtroNome = filtroNome;
    }

    public Date getFiltroDataInicio() {
        return filtroDataInicio;
    }

    public void setFiltroDataInicio(Date filtroDataInicio) {
        this.filtroDataInicio = filtroDataInicio;
    }

    public Date getFiltroDataFim() {
        return filtroDataFim;
    }

    public void setFiltroDataFim(Date filtroDataFim) {
        this.filtroDataFim = filtroDataFim;
    }

    public Evento getNovoEvento() {
        return novoEvento;
    }

    public void setNovoEvento(Evento novoEvento) {
        this.novoEvento = novoEvento;
    }

    public Evento getEventoSelecionado() {
        return eventoSelecionado;
    }

    public void setEventoSelecionado(Evento eventoSelecionado) {
        this.eventoSelecionado = eventoSelecionado;
    }

    public Participante getNovoParticipante() {
        return novoParticipante;
    }

    public void setNovoParticipante(Participante novoParticipante) {
        this.novoParticipante = novoParticipante;
    }

    public Participante getParticipanteSelecionado() {
        return participanteSelecionado;
    }

    public void setParticipanteSelecionado(Participante participanteSelecionado) {
        this.participanteSelecionado = participanteSelecionado;
    }

    public Presenca getNovaPresenca() {
        return novaPresenca;
    }

    public void setNovaPresenca(Presenca novaPresenca) {
        this.novaPresenca = novaPresenca;
    }

    public void aplicarFiltro() {
        eventosFiltrados = eventoService.filtrarEventos(filtroNome.trim(), filtroDataInicio, filtroDataFim);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Filtro Aplicado", "Os eventos foram filtrados com sucesso."));
    }

    public void adicionarEvento() {
        FacesContext context = FacesContext.getCurrentInstance();
        String nomeTrim = (novoEvento.getNome() != null) ? novoEvento.getNome().trim() : "";

        if (nomeTrim.isEmpty()) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Nome Inválido", "O nome do evento não pode estar em branco."));
            context.validationFailed();
            return;
        }

        if (novoEvento.getDataFim().before(novoEvento.getDataInicio())) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Data Inválida", "A data de fim não pode ser anterior à data de início."));
            context.validationFailed();
            return;
        }

        try {
            eventoService.adicionarEvento(novoEvento);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Evento adicionado com sucesso!"));
            novoEvento = new Evento();
            carregarEventos();
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao adicionar o evento."));
            context.validationFailed();
            e.printStackTrace();
        }
    }

    public void carregarEventos() {
        this.eventos = eventoService.getAllEventos();
        this.eventosFiltrados = eventos;
    }

    public void limparFiltroDataInicio(){
        this.filtroDataInicio = null;
    }

    public void limparFiltroDataFim(){
        this.filtroDataInicio = null;
    }

    public void excluirEvento() {
        try {
            eventoService.excluirEvento(eventoSelecionado.getId());
            carregarEventos();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Evento excluído com sucesso!"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao excluir o evento."));
            e.printStackTrace();
        }
    }

    public void adicionarParticipante() {
        FacesContext context = FacesContext.getCurrentInstance();
        String nomeTrim = novoParticipante.getNome() != null ? novoParticipante.getNome().trim() : "";
        String cpfTrim = novoParticipante.getCpf() != null ? novoParticipante.getCpf().trim() : "";
        String emailTrim = novoParticipante.getEmail() != null ? novoParticipante.getEmail().trim() : "";

        if (nomeTrim.isEmpty() || cpfTrim.isEmpty() || emailTrim.isEmpty()) {
            if (nomeTrim.isEmpty()) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Nome Inválido", "O nome do participante não pode estar em branco."));
            }
            if (cpfTrim.isEmpty()) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "CPF Inválido", "O CPF do participante não pode estar em branco."));
            }
            if (emailTrim.isEmpty()) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Email Inválido", "O email do participante não pode estar em branco."));
            }
            context.validationFailed();
            return;
        }

        novoParticipante.setNome(nomeTrim);
        novoParticipante.setCpf(cpfTrim);
        novoParticipante.setEmail(emailTrim);

        try {
            eventoService.adicionarParticipante(eventoSelecionado.getId(), novoParticipante);
            eventoSelecionado.getParticipantes().add(novoParticipante);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Participante adicionado com sucesso!"));
            novoParticipante = new Participante();
        } catch (IllegalArgumentException e) {
            context.validationFailed();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", e.getMessage()));
        } catch (Exception e) {
            context.validationFailed();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao adicionar o participante."));
            e.printStackTrace();
        }
    }

    public void excluirParticipante() {
        try {
            eventoSelecionado.getParticipantes().removeIf(p -> p.getId().equals(participanteSelecionado.getId()));
            eventoService.excluirParticipante(participanteSelecionado.getId());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Participante excluído com sucesso!"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao excluir o participante."));
            e.printStackTrace();
        }
    }

    public void atualizarEvento() {
        FacesContext context = FacesContext.getCurrentInstance();
        String nomeTrim = eventoSelecionado.getNome() != null ? eventoSelecionado.getNome().trim() : "";

        if (eventoSelecionado.getDataFim().before(eventoSelecionado.getDataInicio())) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Data Inválida", "A data de fim não pode ser anterior à data de início."));
            context.validationFailed();

            Evento evento = (eventoService.getEvento(eventoSelecionado.getId())).get();

            eventoSelecionado.setNome(evento.getNome());
            eventoSelecionado.setDataInicio(evento.getDataInicio());
            eventoSelecionado.setDataFim(evento.getDataFim());

            return;
        }

        if (nomeTrim.isEmpty()) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Nome Inválido", "O nome não pode ser em branco."));
            context.validationFailed();

            Evento evento = (eventoService.getEvento(eventoSelecionado.getId())).get();

            eventoSelecionado.setNome(evento.getNome());
            eventoSelecionado.setDataInicio(evento.getDataInicio());
            eventoSelecionado.setDataFim(evento.getDataFim());

            return;
        }

        try {
            eventoService.atualizarEvento(eventoSelecionado);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Evento atualizado com sucesso."));
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao atualizar o evento."));
        }
    }

    public void atualizarParticipante() {
        FacesContext context = FacesContext.getCurrentInstance();
        String nomeTrim = participanteSelecionado.getNome() != null ? participanteSelecionado.getNome().trim() : "";
        String cpfTrim = participanteSelecionado.getCpf() != null ? participanteSelecionado.getCpf().trim() : "";
        String emailTrim = participanteSelecionado.getEmail() != null ? participanteSelecionado.getEmail().trim() : "";

        if (nomeTrim.isEmpty() || cpfTrim.isEmpty() || emailTrim.isEmpty()) {
            if (nomeTrim.isEmpty()) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Nome Inválido", "O nome do participante não pode estar em branco."));
            }
            if (cpfTrim.isEmpty()) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "CPF Inválido", "O CPF do participante não pode estar em branco."));
            }
            if (emailTrim.isEmpty()) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Email Inválido", "O email do participante não pode estar em branco."));
            }
            context.validationFailed();

            Participante participante = (eventoService.getParticipante(participanteSelecionado.getId())).get();

            participanteSelecionado.setNome(participante.getNome());
            participanteSelecionado.setCpf(participante.getCpf());
            participanteSelecionado.setEmail(participante.getEmail());

            return;
        }

        participanteSelecionado.setNome(nomeTrim);
        participanteSelecionado.setCpf(cpfTrim);
        participanteSelecionado.setEmail(emailTrim);

        try {
            eventoService.editarParticipante(eventoSelecionado.getId(),participanteSelecionado);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Participante atualizado com sucesso!"));
        } catch (IllegalArgumentException e) {
            // Captura de CPF duplicado
            context.validationFailed();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", e.getMessage()));
        } catch (Exception e) {
            context.validationFailed();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao atualizar o participante."));
            e.printStackTrace();
        }
    }


    public void confirmarPresenca() {

        FacesContext context = FacesContext.getCurrentInstance();

        if (novaPresenca.getData().before(eventoSelecionado.getDataInicio()) || novaPresenca.getData().after(eventoSelecionado.getDataFim())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Data inválida", "A data deve estar no intervalo do evento."));
            FacesContext.getCurrentInstance().validationFailed();
            return;
        }

        boolean existe = participanteSelecionado.getPresencas()
                .stream()
                .anyMatch(p -> p.getData().equals(novaPresenca.getData()));

        if (existe) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Data inválida", "Já existe uma presença registrada com esta data."));
            FacesContext.getCurrentInstance().validationFailed();
            return;
        }

        List<Presenca> presencas = participanteSelecionado.getPresencas();
        if (presencas == null) {
            presencas = new ArrayList<>();
        }
        presencas.add(novaPresenca);
        participanteSelecionado.setPresencas(presencas);
        eventoService.editarParticipante(eventoSelecionado.getId(),participanteSelecionado);

        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Presenca confirmada!"));
    }
}
