package br.com.api.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.api.rest.model.Usuario;
import br.com.api.rest.repository.UsuarioRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// Consulta no banco o usuario
		Usuario usuario = usuarioRepository.findUserByLogin(username);
		if(usuario == null) {
			throw new UsernameNotFoundException("Usuário não foi encontrado");
		}
		return new User(usuario.getLogin(), usuario.getPassword(), usuario.getAuthorities());
	}
	public void insereAcessoPadrao(Long id) {
		//Descobre qual a constraint de restrição
		String constraint = usuarioRepository.consultaConstraintRole();
		//Remove a constraint
		if(constraint != null) {
		jdbcTemplate.execute(" alter table usuarios_role DROP INDEX " + constraint);
		}
		//Insere os acessos padrão
		usuarioRepository.insereAcessoRolePadrao(id);
		
	}

}
