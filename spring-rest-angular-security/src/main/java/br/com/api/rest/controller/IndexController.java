package br.com.api.rest.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.api.rest.model.Usuario;
import br.com.api.rest.repository.UsuarioRepository;
import br.com.api.rest.service.UserDetailsServiceImpl;

@RestController
@RequestMapping(value = "/usuario")
public class IndexController {

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private UserDetailsServiceImpl userDetailsServiceImpl;

	@GetMapping(value = "/{id}/codigovenda/{venda}", produces = "application/json")
	public ResponseEntity<Usuario> relatorio(@PathVariable(value = "id") Long id,
			@PathVariable(value = "venda") Long venda) {

		Optional<Usuario> usuario = usuarioRepository.findById(id);
		return new ResponseEntity(usuario.get(), HttpStatus.OK);
	}

	// Serviço RestFul
	@GetMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<Usuario>initV1(@PathVariable(value = "id") String id) throws InterruptedException {

		Optional<Usuario> usuario = usuarioRepository.findById(Long.parseLong(id));
		
		return new ResponseEntity(usuario.get(), HttpStatus.OK);
	}

	@GetMapping(value = "/{id}", produces = "application/json", headers = "X-API-VERSION=v2")
	public ResponseEntity<Usuario> initV2(@PathVariable(value = "id") Long id) {

		Optional<Usuario> usuario = usuarioRepository.findById(id);
		
		return new ResponseEntity(usuario.get(), HttpStatus.OK);
	}

	/*
	 * Supondo que o carregamento de usuário seja um processo lento e que controle
	 * ele com cache para agilizar o processo
	 */
	@GetMapping(value = "/", produces = "application/json")

	public ResponseEntity<List<Usuario>> usuario() throws InterruptedException {
		List<Usuario> list = (List<Usuario>) usuarioRepository.findAll();

		return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);
	}
	
	//Consulta de usuário por Nome
	@GetMapping(value = "/usuarioPorNome/{nome}", produces = "application/json")

	public ResponseEntity<List<Usuario>> usuarioPorNome(@PathVariable("nome") String nome) throws InterruptedException {
		List<Usuario> list = (List<Usuario>) usuarioRepository.findByNome(nome);

		return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);
	}

	@PostMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> cadastrar(@Valid @RequestBody Usuario usuario) {

		usuario.getTelefones().forEach(t -> t.setUsuario(usuario));

		String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
		usuario.setSenha(senhaCriptografada);
		Usuario usuarioSalvo = usuarioRepository.save(usuario);
		
		userDetailsServiceImpl.insereAcessoPadrao(usuario.getId());
		
		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
	}

	@PostMapping(value = "/{iduser}/idvenda/{idvenda}", produces = "application/json")
	public ResponseEntity<Usuario> cadastrarvenda(@PathVariable Long iduser, @PathVariable Long idvenda) {
		// Processo de venda
		// Usuario usurioSalvo = usuarioRepository.save(usuario);
		
		return new ResponseEntity("id user: " + iduser + "idvenda :" + iduser, HttpStatus.OK);
	}

	@PutMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> atualizar(@RequestBody Usuario usuario) {
		usuario.getTelefones().forEach(t -> t.setUsuario(usuario));

		Usuario usarioTemporario = usuarioRepository.findById(usuario.getId()).get();

		if (!usarioTemporario.getSenha().equals(usuario.getSenha())) {// Se for diferente
			String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
			usuario.setSenha(senhaCriptografada);
		}

		Usuario usurioSalvo = usuarioRepository.save(usuario);
		return new ResponseEntity<Usuario>(usurioSalvo, HttpStatus.OK);
	}

	@DeleteMapping(value = "/{id}", produces = "application/text")
	public String delete(@PathVariable("id") Long id) {

		usuarioRepository.deleteById(id);

		return "Usuário deletado com sucesso";
	}

	@DeleteMapping(value = "/{id}/venda", produces = "application/text")
	public String deletevenda(@PathVariable("id") Long id) {

		usuarioRepository.deleteById(id);

		return "Vendas deletado com sucesso";
	}
}