package br.com.api.rest.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import br.com.api.rest.model.Usuario;

public interface UsuarioRepository extends CrudRepository<Usuario, Long> {

	@Query("select u from Usuario u where u.login = ?1")
	Usuario findUserByLogin(String login);
	
	@Query("select u from Usuario u where u.nome like %?1%")
	List<Usuario> findByNome(String nome);
	
	@Query(value = "SELECT constraint_name from information_schema.constraint_column_usage \r\n" + 
			"where table_name = 'usuarios_role'\r\n" + 
			"and column_name = 'role_id' \r\n" + 
			"and constraint_name <> 'unique_role_user';", nativeQuery = true )
	String consultaConstraintRole();
	
	
	@Transactional
	@Modifying
	@Query(nativeQuery = true, value = "insert into usuarios_role (usuario_id, role_id) \r\n" + 
			"values (?1, (select id from role where nome_role = 'ROLE USER'));")
	void  insereAcessoRolePadrao(Long idUser);
}
