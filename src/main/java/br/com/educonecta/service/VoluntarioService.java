package br.com.educonecta.service;

import br.com.educonecta.exception.ConflitoException;
import br.com.educonecta.exception.RecursoNaoEncontradoException;
import br.com.educonecta.model.Voluntario;
import br.com.educonecta.repository.VoluntarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Regras de negocio do cadastro de professores voluntarios.
 */
@Service
public class VoluntarioService {

    private final VoluntarioRepository repository;

    public VoluntarioService(VoluntarioRepository repository) {
        this.repository = repository;
    }

    public Voluntario cadastrar(Voluntario voluntario) {
        garantirEmailDisponivel(voluntario.getEmail());
        return repository.inserir(voluntario);
    }

    public Voluntario buscarPorId(String id) {
        Voluntario voluntario = repository.buscarPorId(id);
        if (voluntario == null) {
            throw new RecursoNaoEncontradoException("Voluntario", id);
        }
        return voluntario;
    }

    public List<Voluntario> listarTodos() {
        return repository.listarTodos();
    }

    public Voluntario atualizar(String id, Voluntario novosDados) {
        Voluntario existente = buscarPorId(id);
        if (!existente.getEmail().equals(novosDados.getEmail())) {
            garantirEmailDisponivel(novosDados.getEmail());
        }
        existente.atualizarDados(novosDados);
        repository.atualizar(existente);
        return existente;
    }

    public void remover(String id) {
        if (!repository.remover(id)) {
            throw new RecursoNaoEncontradoException("Voluntario", id);
        }
    }

    private void garantirEmailDisponivel(String email) {
        if (repository.existePorEmail(email)) {
            throw new ConflitoException("Ja existe voluntario cadastrado com o e-mail " + email + ".");
        }
    }
}
