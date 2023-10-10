<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="ru">
<head>
    <title>Add Meal</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Add meal</h2>

<form method="post" action="meals">

    DateTime: <input type="datetime-local" name="dateTime"/><br>
    Description: <input type="text" name="description"/><br>
    Calories: <input type="number" name="calories"/><br>
    <input type="submit" value="Add"/>
    <button onclick="window.history.back()" type="button">Cancel</button>

</form>

</body>
</html>
