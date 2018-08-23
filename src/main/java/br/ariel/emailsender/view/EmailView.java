package br.ariel.emailsender.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

import br.ariel.emailsender.controller.EmailController;
import br.ariel.emailsender.exception.MultipleException;

public class EmailView extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	private JTextField txtServidor;
	private JTextField txtUsuario;
	private JTextField txtSenha;
	private JTextField txtPorta;
	private JTextField txtTitulo;
	private JTextField txtCorpo;
	private JTextField txtPlanilha;
	private JTextPane txtNomes;

	private EmailController controller = new EmailController();
	private Map<String, String> nomesEmails;

	public EmailView() {
		setTitle("E-mail sender");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 440, 539);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);
		panel.setLayout(new GridLayout(15, 1, 0, 0));
		panel.setSize(new Dimension(panel.getWidth(), 500));

		JLabel lblNewLabel = new JLabel("Servidor SMTP");
		panel.add(lblNewLabel);

		txtServidor = new JTextField();
		txtServidor.setText("smtp.gmail.com");
		panel.add(txtServidor);
		txtServidor.setColumns(10);

		JLabel lblNewLabel_3 = new JLabel("Porta");
		panel.add(lblNewLabel_3);

		txtPorta = new JTextField();
		txtPorta.setText("465");
		panel.add(txtPorta);
		txtPorta.setColumns(10);

		JLabel lblNewLabel_1 = new JLabel("Usuário");
		panel.add(lblNewLabel_1);

		txtUsuario = new JTextField();
		txtUsuario.addFocusListener(criarFocusAdapterSalvarConfig());
		panel.add(txtUsuario);
		txtUsuario.setColumns(10);

		JLabel lblNewLabel_2 = new JLabel("Senha");
		panel.add(lblNewLabel_2);

		txtSenha = new JPasswordField();
		txtSenha.addFocusListener(criarFocusAdapterSalvarConfig());
		panel.add(txtSenha);
		txtSenha.setColumns(10);

		JLabel lblNewLabel_4 = new JLabel("Título e-mail");
		panel.add(lblNewLabel_4);

		txtTitulo = new JTextField();
		txtTitulo.addFocusListener(criarFocusAdapterSalvarConfig());
		panel.add(txtTitulo);
		txtTitulo.setColumns(10);

		JLabel lblNewLabel_5 = new JLabel("Corpo e-mail");
		panel.add(lblNewLabel_5);

		txtCorpo = new JTextField();
		txtCorpo.addFocusListener(criarFocusAdapterSalvarConfig());
		panel.add(txtCorpo);
		txtCorpo.setColumns(10);

		JLabel lblNewLabel_6 = new JLabel("Planilha xlsx");
		panel.add(lblNewLabel_6);

		txtPlanilha = new JTextField();
		txtPlanilha.addFocusListener(criarFocusAdapterSalvarConfig());
		panel.add(txtPlanilha);
		txtPlanilha.setColumns(10);

		JButton btnNewButton_1 = new JButton("Carregar planilha");
		btnNewButton_1.addActionListener(criarEventoBotaoCarregarPlanilha());
		panel.add(btnNewButton_1);

		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.SOUTH);

		JButton btnNewButton = new JButton("Enviar");
		panel_1.add(btnNewButton);

		JPanel panel_2 = new JPanel();
		contentPane.add(panel_2, BorderLayout.CENTER);
		panel_2.setLayout(new GridLayout(1, 1, 0, 0));

		txtNomes = new JTextPane();
		txtNomes.setEditable(false);

		JScrollPane scrollPane = new JScrollPane(txtNomes);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		panel_2.add(scrollPane);
		btnNewButton.addActionListener(criarEventoBotaoEnviar());

		configurar();
	}

	private FocusAdapter criarFocusAdapterSalvarConfig() {
		return new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				try {
					salvarConfig();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		};
	}

	private void configurar() {
		try {
			lerConfig();
			salvarConfig();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void lerConfig() throws IOException {
		InputStream is = null;
		try {
			Properties properties = new Properties();

			is = new FileInputStream("config.properties");
			properties.load(is);

			txtUsuario.setText(properties.getProperty("usuario"));
			txtSenha.setText(properties.getProperty("senha"));
			txtTitulo.setText(properties.getProperty("titulo"));
			txtCorpo.setText(properties.getProperty("corpo"));
			txtPlanilha.setText(properties.getProperty("planilha"));
		} finally {
			if (null != is) {
				is.close();
			}
		}
	}

	private void salvarConfig() throws FileNotFoundException, IOException {
		OutputStream os = null;
		try {
			Properties properties = new Properties();
			properties.put("usuario", txtUsuario.getText());
			properties.put("senha", txtSenha.getText());
			properties.put("titulo", txtTitulo.getText());
			properties.put("corpo", txtCorpo.getText());
			properties.put("planilha", txtPlanilha.getText());

			os = new FileOutputStream("config.properties");
			properties.store(os, null);
		} finally {
			if (null != os) {
				os.close();
			}
		}
	}

	private ActionListener criarEventoBotaoCarregarPlanilha() {
		return (action) -> {
			String caminhoPlanilha = txtPlanilha.getText();
			try {
				Map<String, String> nomesEmails = controller.getNomesEmails(caminhoPlanilha);
				popularNomesEmails(nomesEmails);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "ERRO", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		};
	}

	private void popularNomesEmails(Map<String, String> nomesEmails) {
		this.nomesEmails = nomesEmails;

		StringBuilder sb = new StringBuilder();
		nomesEmails.forEach((key, value) -> sb.append(key).append(" - ").append(value).append("\n"));

		txtNomes.setText(sb.toString());
	}

	private ActionListener criarEventoBotaoEnviar() {
		return (action) -> {
			try {
				String servidor = txtServidor.getText();
				String porta = txtPorta.getText();
				String usuario = txtUsuario.getText();
				String senha = txtSenha.getText();
				String titulo = txtTitulo.getText();
				String corpo = txtCorpo.getText();

				controller.enviar(servidor, porta, usuario, senha, titulo, corpo, nomesEmails);
				JOptionPane.showMessageDialog(this, "Acho que foi...", "OK", JOptionPane.INFORMATION_MESSAGE);
			} catch (MultipleException e) {
				StringBuilder sb = new StringBuilder();
				Stream.of(e.getSuppressed()).forEach(ex -> sb.append(ex.getMessage()).append("\n"));
				JOptionPane.showMessageDialog(this, sb.toString(), "ERRO", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "ERRO", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		};
	}

}
