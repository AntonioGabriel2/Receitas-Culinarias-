package br.edu.iff.ccc.webdev.controller.service;

import br.edu.iff.ccc.webdev.dto.UsuarioDTO;
import br.edu.iff.ccc.webdev.entities.Usuario;
import br.edu.iff.ccc.webdev.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.edu.iff.ccc.webdev.entities.Perfil;

@Service
public class UsuarioService {

    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;

    public UsuarioService(UsuarioRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    /* CREATE */
    @Transactional
    public Usuario cadastrar(UsuarioDTO dto) {
        if (dto.getSenha() == null || dto.getSenha().isBlank()) {
            throw new IllegalArgumentException("Senha obrigatória no cadastro.");
        }
        String cpf   = dto.getCpf().replaceAll("\\D", "");
        String email = dto.getEmail().toLowerCase();

        if (repo.existsByCpf(cpf))                     throw new IllegalArgumentException("CPF já cadastrado.");
        if (repo.existsByEmailIgnoreCase(email))       throw new IllegalArgumentException("E-mail já cadastrado.");

        Usuario u = new Usuario(dto.getNome(), cpf, email, encoder.encode(dto.getSenha()));
        return repo.save(u);
    }

    /* UPDATE */
    @Transactional
    public Usuario atualizar(Long id, UsuarioDTO dto) {
        Usuario u = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        String emailNovo = dto.getEmail().toLowerCase();
        // garante unicidade do email no update
        if (!u.getEmail().equalsIgnoreCase(emailNovo)
                && repo.existsByEmailIgnoreCase(emailNovo)) {
            throw new IllegalArgumentException("E-mail já cadastrado.");
        }

        u.setNome(dto.getNome());
        u.setEmail(emailNovo);
        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            u.setSenhaHash(encoder.encode(dto.getSenha()));
        }
        return repo.save(u);
    }

    /* READ - list */
    @Transactional(readOnly = true)
    public java.util.List<Usuario> findAll() {
        return repo.findAll();
    }

    /* READ - one */
    @Transactional(readOnly = true)
    public java.util.Optional<Usuario> findById(Long id) {
        return repo.findById(id);
    }

    /* DELETE */
    @Transactional
    public void excluir(Long id) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }
        repo.deleteById(id);
    }

    /* (Opcional) buscar por e-mail - útil para login */
    @Transactional(readOnly = true)
    public java.util.Optional<Usuario> findByEmail(String email) {
        return repo.findByEmailIgnoreCase(email.toLowerCase());
    }

    @Transactional
    public Usuario tornarCozinheiro(Long id) {
        Usuario u = repo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
        u.setPerfil(Perfil.COZINHEIRO);
        return repo.save(u);
    }
}

