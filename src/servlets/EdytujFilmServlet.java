package servlets;

import dao.FilmDAO;
import dao.GatunekDAO;
import dao.GatunekFilmDAO;
import dao.RecenzjaDAO;
import models.Film;
import models.Gatunek;
import models.GatunekFilm;

import javax.persistence.NoResultException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

@WebServlet("/edytujFilm")
public class EdytujFilmServlet extends HttpServlet {

    /**
     * Metoda pobiera informacje o filmie i przekazuje je do wyświetlenia na stronie formularza edytujFilm.jsp.
     * Jeżeli dane formularza zostaną zmienione to metoda zastąpi stare dane nowowprowadzonymi i zapisze je w bazie
     * Metoda umożliwia edycje filmu, a także dodawanie, edycje i usuwanie gatunków do filmu.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Pobieramy informacje na temet filmu i jego gatunków
        int idFilmu = Integer.parseInt(request.getParameter("idFilmu"));
        String tytul = request.getParameter("tytul");
        String opis = request.getParameter("opis");
        String rokProdukcji = request.getParameter("rokProdukcji");
        // Nazwy gatunków wpisanych przez pracownika
        String[] newgatunek = request.getParameterValues("gatunek");

        // Tworzenie Regex-ów
        String patternPusty = ".+";
        String patternRokProdukcji = "19[0-9]{2}|20[0-9]{2}";

        FilmDAO filmDAO = new FilmDAO();
        GatunekDAO gatunekDAO = new GatunekDAO();
        GatunekFilmDAO gatunekFilmDAO = new GatunekFilmDAO();

        // Pobieramy film z bazy
        Film film = filmDAO.getWybranyFilm(idFilmu);
        List<GatunekFilm> gatunekFilm;

        // Pominięcie edycji jeśli wchodzimy w edycje pierwszy raz
        if (tytul != null) {

            // Sprawdzenie czy dane zostały poprawnie wstawione
            if (Pattern.matches(patternPusty, tytul) && Pattern.matches(patternPusty, opis)
                    && Pattern.matches(patternRokProdukcji, rokProdukcji) && Pattern.matches(patternPusty, newgatunek[0])) {

                // Pobranie listy gatunków danego filmu
                gatunekFilm = gatunekFilmDAO.getGatunekFilmPoFilmieList(idFilmu);

                // Edycja danych o filmie
                film.setTytul(tytul);
                film.setOpis(opis);
                film.setRokProdukcji(Integer.parseInt(rokProdukcji));

                // Update danych o filmie
                filmDAO.updateFilm(film);

                int oldGatunek;
                Gatunek gatunek;
                GatunekFilm newGatunekFilm = new GatunekFilm();

                // Iteracja po gatunkach wybranego filmu
                for (int i = 0; i < gatunekFilm.size(); i++) {

                    // Sprawdzanie czy zmieniono dany gatunek
                    if (!gatunekDAO.getWybranyGatunekPoId(gatunekFilm.get(i).getIdGatunku()).getNazwa().equals(newgatunek[i])) {
                        // Sprawdzanie czy nowy gatunek nie jest pusty (usunięcie gatunku)
                        if (Pattern.matches(patternPusty, newgatunek[i])) {
                            try {
                                // Szukanie gatunku w bazie
                                gatunek = gatunekDAO.getWybranyGatunekPoNazwie(newgatunek[i]);
                            } catch (NoResultException e) {

                                gatunek = new Gatunek();
                                gatunek.setNazwa(newgatunek[i]);
                                gatunekDAO.addGatunek(gatunek);
                            }
                            // Pobranie starego połączenia gatunekFilm
                            GatunekFilm oldGatunekFilm = gatunekFilmDAO.getWybranyGatunek(idFilmu, gatunekFilm.get(i).getIdGatunku());
                            oldGatunek = oldGatunekFilm.getIdGatunku();

                            // Usunięcie starego połączenia gatunekFilm
                            gatunekFilmDAO.deleteGatunekFilm(oldGatunekFilm);

                            // Dodanie nowego połączenia gatunekFilm
                            newGatunekFilm.setIdGatunku(gatunek.getIdGatunku());
                            newGatunekFilm.setIdFilmu(idFilmu);
                            gatunekFilmDAO.addGatunekFilm(newGatunekFilm);
                        } else {
                            // Usuwanie połączenia gatunekFilm
                            GatunekFilm oldGatunekFilm = gatunekFilmDAO.getWybranyGatunek(idFilmu, gatunekFilm.get(i).getIdGatunku());
                            oldGatunek = oldGatunekFilm.getIdGatunku();
                            gatunekFilmDAO.deleteGatunekFilm(oldGatunekFilm);
                        }

                        // Jeżeli żaden film nie ma starego gatunku to jest on usuwany
                        try {
                            GatunekFilm gatunkuList = gatunekFilmDAO.getGatunekFilmPoGatunku(oldGatunek);
                        } catch (NoResultException | IndexOutOfBoundsException e) {
                            //Usuwanie gatunku
                            gatunek = gatunekDAO.getWybranyGatunekPoId(oldGatunek);
                            gatunekDAO.deleteGatunek(gatunek);
                        }
                    }
                }

                //Sprawdzanie czy jest dodatkowy gatunek
                if (gatunekFilm.size() < newgatunek.length && Pattern.matches(patternPusty, newgatunek[newgatunek.length - 1])) {
                    try {
                        // Szukanie gatunku w bazie
                        gatunek = gatunekDAO.getWybranyGatunekPoNazwie(newgatunek[newgatunek.length - 1]);
                    } catch (NoResultException e) {
                        // Tworzenie nowego gatunku
                        gatunek = new Gatunek();
                        gatunek.setNazwa(newgatunek[newgatunek.length - 1]);
                        gatunekDAO.addGatunek(gatunek);
                    }
                    // Dodanie nowego połączenia gatunekFilm
                    newGatunekFilm.setIdGatunku(gatunekDAO.getWybranyGatunekPoNazwie(newgatunek[newgatunek.length - 1]).getIdGatunku());
                    newGatunekFilm.setIdFilmu(idFilmu);
                    gatunekFilmDAO.addGatunekFilm(newGatunekFilm);
                }
                request.setAttribute("wynik", "Film został edytowany!");
            } else request.setAttribute("blad", "Podałeś błędne dane!");
        }

        // Przekazanie danych do wyświetlenia
        request.setAttribute("film", film);

        List<Gatunek> gatunek = gatunekDAO.getAllGatunkiList();
        request.setAttribute("gatunek", gatunek);

        gatunekFilm = gatunekFilmDAO.getGatunekFilmPoFilmieList(idFilmu);
        request.setAttribute("gatunekFilm", gatunekFilm);

        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/edytujFilm.jsp").forward(request, response);
    }


}
