package com.grupo10.bff_vitacare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del BFF (Backend For Frontend) de VitaCare.
 *
 * <p>Este servicio actúa como fachada única para el frontend móvil: valida la
 * autenticación de Firebase, y orquesta/agrega las llamadas hacia los
 * microservicios de dominio (usuarios, pacientes, mediciones, medicamentos,
 * chatbot y alertas de IA) para exponer una API simplificada y coherente.</p>
 */
@SpringBootApplication
public class BffVitacareApplication {

	/**
	 * Arranca el contexto de Spring Boot de la aplicación.
	 *
	 * @param args argumentos de línea de comandos pasados a Spring Boot
	 */
	public static void main(String[] args) {
		SpringApplication.run(BffVitacareApplication.class, args);
	}

}
