package org.lessons.java;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {

	private final static String url = "jdbc:mysql://localhost:3306/db-nations";
	private final static String username = "root";
	private final static String password = "root";

	public static void main(String[] args) throws SQLException {

		Connection conn = null;

		Scanner in = new Scanner(System.in);
		System.out.println("Cerca una nazione o diginta parte del nome");

		String strNation = in.nextLine();

		// CREAZIONE CONNESSIONE CON IL DB
		try {
			conn = DriverManager.getConnection(url, username, password);

			final String SQL = "SELECT countries.country_id, countries.name AS country_name, regions.name AS region_name, continents.name AS continent_name "
					+ "FROM countries " + "JOIN regions ON countries.region_id = regions.region_id "
					+ "JOIN continents ON regions.continent_id = continents.continent_id "
					+ "WHERE countries.name LIKE CONCAT('%' ? '%') " + "ORDER BY countries.name";

			System.out.println();

			try (PreparedStatement ps = conn.prepareStatement(SQL)) {

				ps.setString(1, strNation);

				try (ResultSet rs = ps.executeQuery()) {

					while (rs.next()) {
						int country_id = rs.getInt(1);
						String country_name = rs.getString(2);
						String region_name = rs.getString(3);
						String continent_name = rs.getString(4);

						System.out.println("id: " + country_id + " - " + country_name + " - " + region_name + " - "
								+ continent_name);
					}
				}
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			if (conn != null) {
				conn.close();
			}
		}

		System.out.println("\n----------------------------------\n");

		System.out.println("Digita il id della nazione per ulteriori statistiche");
		String strCountryId = in.nextLine();
		int countryId = Integer.parseInt(strCountryId);

		// CRO CONNESSIONE CON IL DB
		try {
			conn = DriverManager.getConnection(url, username, password);

			final String SQL = "SELECT languages.language, country_stats.year, country_stats.population, country_stats.gdp "
					+ "FROM countries "
					+ "JOIN country_languages ON country_languages.country_id = countries.country_id "
					+ "JOIN languages ON country_languages.language_id = languages.language_id "
					+ "JOIN country_stats ON country_stats.country_id = countries.country_id "
					+ "WHERE countries.country_id = ? " + "AND country_stats.year > 2015";

			System.out.println();

			try (PreparedStatement ps = conn.prepareStatement(SQL)) {

				ps.setInt(1, countryId);

				try (ResultSet rs = ps.executeQuery()) {

					while (rs.next()) {
						String language = rs.getString(1);
						int year = rs.getInt(2);
						int population = rs.getInt(3);
						String gdp = rs.getString(4);

						System.out.println("Lingua: " + language);
						System.out.println("Statisctiche più recenti \n");
						System.out.println(" - Anno: " + year + " - Popolazione: " + population + " - GDP: " + gdp);
						System.out.println("\n----------------------------------\n");
					}
				}
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

}
