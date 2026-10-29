package com.gerenciador.erro;

public class PersistenciaException extends Exception {

    /**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public PersistenciaException(String mensagem, Throwable causa) { super(mensagem, causa); }
}
