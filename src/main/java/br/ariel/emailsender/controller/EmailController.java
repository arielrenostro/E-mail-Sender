package br.ariel.emailsender.controller;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import br.ariel.emailsender.exception.MultipleException;

public class EmailController {

	public Map<String, String> getNomesEmails(String caminhoPlanilha) throws Exception {
		PlanilhaController planilhaController = new PlanilhaController();
		return planilhaController.getNomesEmails(caminhoPlanilha);
	}

	public void enviar(String servidor, String porta, String usuario, String senha, String titulo, String corpo, Map<String, String> nomesEmails) throws Exception {
		validarEnviar(servidor, porta, usuario, senha, titulo, corpo, nomesEmails);

		MultipleException exception = new MultipleException();

		Session session = getSession(servidor, porta, usuario, senha);

		for (Entry<String, String> entry : nomesEmails.entrySet()) {
			String nome = entry.getKey();
			String email = entry.getValue();

			try {
				String tituloFormatado = getMensagemNomeFormatado(nome, titulo);
				String corpoFormatado = getMensagemNomeFormatado(nome, corpo);

				Message message = new MimeMessage(session);
				message.setFrom(new InternetAddress(usuario));
				message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
				message.setSubject(tituloFormatado);
				message.setContent(corpoFormatado, "text/html; charset=utf-8");

				Transport.send(message);
			} catch (Exception e) {
				Exception ex = new Exception("ERRO: E-mail [" + email + "]");
				ex.addSuppressed(e);
				exception.addSuppressed(ex);
			}
		}

		if (exception.getSuppressed().length > 0) {
			throw exception;
		}
	}

	private void validarEnviar(String servidor, String porta, String usuario, String senha, String titulo, String corpo, Map<String, String> nomesEmails) throws Exception {
		if (null == nomesEmails || nomesEmails.isEmpty()) {
			throw new Exception("Nomes e e-mails não carregados");
		}

		if (null == servidor || servidor.isEmpty()) {
			throw new Exception("Informe o servidor");
		}

		if (null == porta || porta.isEmpty()) {
			throw new Exception("Informe a porta");
		}

		if (null == usuario || usuario.isEmpty()) {
			throw new Exception("Informe o usuário");
		}

		if (null == senha || senha.isEmpty()) {
			throw new Exception("Informe a senha");
		}

		if (null == titulo || titulo.isEmpty()) {
			throw new Exception("Informe o título");
		}

		if (null == corpo || corpo.isEmpty()) {
			throw new Exception("Informe o corpo");
		}
	}

	private String getMensagemNomeFormatado(String nome, String msg) {
		return msg.replaceAll("\\[NOME\\]", nome);
	}

	private Session getSession(String servidor, String porta, String usuario, String senha) {
		Properties props = new Properties();
		props.put("mail.smtp.host", servidor);
		props.put("mail.smtp.socketFactory.port", porta);
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", porta);

		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			@Override
			protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
				return new javax.mail.PasswordAuthentication(usuario, senha);
			}
		});
		return session;
	}

}
