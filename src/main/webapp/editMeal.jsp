<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="ru">
<head>
  <title>Edit meal</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>

<c:set var="title" value="${meal.id == null ? 'Add meal' : 'Edit meal'}"/>

<h2>${title}</h2>

<form method="post" action="meals">

  <input hidden type="number" name="id" value="${meal.id}"/>
  DateTime: <input type="datetime-local" value="${meal.dateTime}" name="dateTime"/><br>
  Description: <input type="text" value="${meal.description}" name="description"/><br>
  Calories: <input type="number" value="${meal.calories}" name="calories"/><br>
  <input type="submit" value="Save"/>
  <button onclick="window.history.back()" type="button">Cancel</button>

</form>

</body>
</html>
