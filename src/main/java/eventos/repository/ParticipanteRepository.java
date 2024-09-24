package eventos.repository;

import eventos.model.Participante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParticipanteRepository extends JpaRepository<Participante, Long> {
    Optional<Participante> findByCpfAndEventoIdAndIdNot(String cpf, Long eventoId, Long id);
}
