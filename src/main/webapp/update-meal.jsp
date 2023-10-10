<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="ru">
<head>
    <title>Update Meal</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Update meal</h2>

<form method="post" action="meals">

    <input hidden type="number" name="id" value="${mealId}"/>
    DateTime: <input type="datetime-local" value="${mealDateTime}" name="dateTime"/><br>
    Description: <input type="text" value="${mealDescription}" name="description"/><br>
    Calories: <input type="number" value="${mealCalories}" name="calories"/><br>
    <input type="submit" value="Update"/>
    <button onclick="window.history.back()" type="button">Cancel</button>

</form>

</body>
</html>
