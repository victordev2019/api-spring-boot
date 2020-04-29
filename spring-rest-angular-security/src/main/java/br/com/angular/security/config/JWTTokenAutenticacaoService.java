package br.com.angular.security.config;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import br.com.angular.security.ApplicationContextLoad;
import br.com.api.rest.model.Usuario;
import br.com.api.rest.repository.UsuarioRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
@Component
public class JWTTokenAutenticacaoService {

	
	
	//Tem de validade do token 2 dias
	private static final long EXPIRATION_TIME = 172800000;
	
	//Uma senha unica para compor a autenticação e ajudar na segurança
	private static final String SECRET = "*SenhaExtremamenteSecreta";
	
	//Prefixo padrão de Token
	private static final String TOKEN_PREFIX = "Bearer";
	
	private static final String HEADER_STRING = "Authorization";
	
	//Gerando token de autenticação e adicionando ao cabeçalho e resposta Http
	public void addAuthentication(HttpServletResponse response, String username) throws IOException {
		//Montagem do Token
		String JWT =Jwts.builder()//Chama o gerador de Token
				.setSubject(username)//Adiciona o usuário
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) //Tempo de expiração
				.signWith(SignatureAlgorithm.HS512, SECRET).compact();//Compactação e algoritmos de geração de senha
		//Junta token com o prefixo
		String token = TOKEN_PREFIX + " " + JWT; //Bearer 87878we787w8e78w78w78e78w7w87w
		
		//Adiciona o cabeçalho http
		response.addHeader(HEADER_STRING, token);//Authorization: Bearer 87878we787w8e78w78w78e78w7w87w
		
		liberarCORS(response);
		//Escrevem token como resposta no corpo http
		response.getWriter().write("{\"Authorization\": \""+token+"\"}");
		
	}
	
	//Retorna o usuário validado com o token ou caso não seja validado retorna null
	public Authentication getAuthentication(HttpServletRequest request, HttpServletResponse response) {

		// Pega o token enviado no cabeçalho Http
		String token = request.getHeader(HEADER_STRING);
		
		if (token != null) {			
			// Faz validação do Token do usuário na requisição
			String user = Jwts.parser()
					.setSigningKey(SECRET)
					.parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
					.getBody()
					.getSubject();	
			
			if (user != null) {					
				Usuario usuario = ApplicationContextLoad
						.getApplicationContext()
						.getBean(UsuarioRepository.class)
						.findUserByLogin(user);
				
				if (usuario != null) {
					return new UsernamePasswordAuthenticationToken(
							usuario.getLogin(), 
							usuario.getSenha(), 
							usuario.getAuthorities());
				}
			}
		}	
		liberarCORS(response);
		return null;
	}

	// CORS policy
	private void liberarCORS(HttpServletResponse response) {
		if (response.getHeader("Access-Control-Allow-Origin") == null) {
			response.addHeader("Access-Control-Allow-Origin", "*");		
		}
		
		if (response.getHeader("Access-Control-Allow-Headers") == null) {
			response.addHeader("Access-Control-Allow-Headers", "*");		
		}
		
		if (response.getHeader("Access-Control-Request-Headers") == null) {
			response.addHeader("Access-Control-Request-Headers", "*");		
		}
		
		if (response.getHeader("Access-Control-Allow-Methods") == null) {
			response.addHeader("Access-Control-Allow-Methods", "*");
					
		}
	}

}