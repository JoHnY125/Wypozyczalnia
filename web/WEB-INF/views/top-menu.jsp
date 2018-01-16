<%@ page import="models.Uzytkownik" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
</head>
<body>
<div id="top-menu" style="background-color:red;height:50px;">
    <a href="stronaGlowna"><img style="width:150px;height:50px;"
                                src="https://upload.wikimedia.org/wikipedia/commons/thumb/9/9b/DVD_logo.svg/1200px-DVD_logo.svg.png"
                                alt="logo"></a>
    <div style="position:absolute;display:inline;margin:5px">
        <form method="post" action="panelUzytkownika" style="display:inline;margin-left:40px">
            <input type="submit" value="Panel Użytkownika"
                   style="width:140px;height:40px;background-color:lightgrey;border-color:lightgrey;"></input>
        </form>
        <%
            Uzytkownik uzytkownik = (Uzytkownik) request.getSession().getAttribute("uzytkownik");
            if (uzytkownik != null) {
        %>
        <form action="wyloguj" style="display:inline;margin-right:20px">
            <input type="submit" value="Wyloguj"
                   style="width:120px;height:40px;background-color:lightgrey;border-color:lightgrey;"></input>
        </form>
        <%
            if (uzytkownik.getRola().equals("klient")) {
                out.println("Klient: " + uzytkownik.getLogin());
            } else if (uzytkownik.getRola().equals("pracownik")) {
                out.println("Pracownik: " + uzytkownik.getLogin());
            } else if (uzytkownik.getRola().equals("administrator")) {
                out.println("Administrator: " + uzytkownik.getLogin());
            }
        } else {
        %>
        <form method="post" action="login" style="display:inline">
            <input type="submit" value="Logowanie"
                   style="width:120px;height:40px;background-color:lightgrey;border-color:lightgrey;"></input>
        </form>
        <form action="rejestruj" style="display:inline">
            <input type="submit" value="Zarejestruj się !"
                   style="width:120px;height:40px;background-color:lightgrey;border-color:lightgrey;"></input>
        </form>
        <%
            }
        %>

    </div>
</div>
<img src="http://bohmtheatre.org/wp-content/uploads/2015/09/film-banner.jpg"
     width="900px" height="378px">
</body>