<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<link href="meals_table.css" rel="stylesheet">
<html lang="ru">
<head>
    <title>Meals</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Meals</h2>
    <table>
        <tr>
            <th>Date</th>
            <th>Description</th>
            <th>Calories</th>
        </tr>
        <br>

        <c:forEach var="meal" items="${meals}">
            <c:set var="excessColor" value="${meal.excess ? 'red' : 'green'}"/>
            <tr style="color: ${excessColor}">
                <td>${meal.dateTime.format(formatter)}</td>
                <td>${meal.description}</td>
                <td>${meal.calories}</td>
            </tr>
        </c:forEach>

    </table>

</body>
</html>
