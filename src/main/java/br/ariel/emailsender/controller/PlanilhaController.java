package br.ariel.emailsender.controller;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class PlanilhaController {

	@SuppressWarnings("resource")
	public Map<String, String> getNomesEmails(String caminhoPlanilha) throws Exception {
		Path path = Paths.get(caminhoPlanilha);
		if (!Files.exists(path)) {
			throw new Exception("Caminho planilha inválido");
		}

		File planilha = path.toFile();
		FileInputStream fis = new FileInputStream(planilha);

		XSSFWorkbook myWorkBook = new XSSFWorkbook(fis);
		XSSFSheet sheet = myWorkBook.getSheetAt(0);

		Map<String, String> nomesEmails = new HashMap<>();

		Iterator<Row> linhas = sheet.iterator();
		while (linhas.hasNext()) {
			Row linha = linhas.next();

			Cell cellNome = linha.getCell(1);
			Cell cellEmail = linha.getCell(8);

			if (null == cellNome || null == cellEmail) {
				continue;
			}

			String nome = cellNome.getStringCellValue();
			String email = cellEmail.getStringCellValue();

			if (null == email || !email.contains("@")) {
				continue;
			}

			nomesEmails.put(nome, email);
		}
		return nomesEmails;
	}

}
