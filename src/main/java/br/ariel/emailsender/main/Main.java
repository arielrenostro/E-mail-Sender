package br.ariel.emailsender.main;

import java.awt.EventQueue;

import br.ariel.emailsender.view.EmailView;

public class Main {

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				EmailView frame = new EmailView();
				frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}
