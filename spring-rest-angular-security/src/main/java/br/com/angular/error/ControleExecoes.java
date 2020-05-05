package br.com.angular.error;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@ControllerAdvice
public class ControleExecoes extends ResponseEntityExceptionHandler {

	//Tratamento da maioria dos erros
	@Override
	@ExceptionHandler({Exception.class, RuntimeException.class, Throwable.class})
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		String msg = "";
		
		if(ex instanceof MethodArgumentNotValidException) {
		List<ObjectError> list = ((MethodArgumentNotValidException) ex).getBindingResult().getAllErrors();
		for (ObjectError objectError : list) {
			msg += objectError.getDefaultMessage() + "\n";
			
		}
		}
		// TODO Auto-generated method stub
		ObjetoError objetoError = new ObjetoError();
		objetoError.setError(msg);
		objetoError.setCode(status.value() + " ==> " + status.getReasonPhrase());
		return new ResponseEntity<>(objetoError,headers,status);
	}
	
	//Tratamento dos erros do banco de dados
	@ExceptionHandler({DataIntegrityViolationException.class, ConstraintViolationException.class, SQLException.class})
	protected ResponseEntity<Object> handleExceptionDataIntegry(Exception ex) {
		
		String msg = ex.getMessage();
		
		if(ex instanceof DataIntegrityViolationException ) {
			msg = ((DataIntegrityViolationException) ex).getCause().getCause().getMessage();
			setErrorMessage(msg);
			
		}
		if(ex instanceof ConstraintViolationException ) {
			msg = ((ConstraintViolationException) ex).getCause().getCause().getMessage();
			setErrorMessage(msg);
		}
		
		if(ex instanceof SQLException ) {
			msg = ((SQLException) ex).getCause().getCause().getMessage();
			setErrorMessage(msg);
		}
		
		return setErrorMessage(msg);
		}
		
		// TODO Auto-generated method stub
		private ResponseEntity<Object> setErrorMessage(String msg){
		ObjetoError objetoError = new ObjetoError();
		objetoError.setError(msg);
		objetoError.setCode(HttpStatus.INTERNAL_SERVER_ERROR + " ==> " + HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
		return new ResponseEntity<>(objetoError,HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
}
