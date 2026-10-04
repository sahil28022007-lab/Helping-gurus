<%-- Login form. Posts to AuthServlet (/login). The demo accounts shown here exist only for the demonstration. --%><%@ include file="header.jspf" %>
<div class="card pad narrow"><h2>Login</h2><c:if test="${not empty error}"><p class="err"><c:out value="${error}"/></p></c:if>
<form method="post" action="${pageContext.request.contextPath}/login"><label>Email</label><input type="email" name="email" required><label>Password</label><input type="password" name="password" required><button class="btn full">Login</button></form>
<p class="mut">Demo: admin@helpinggurus.org / admin123 &middot; priya@mail.com / priya123 &middot; donor@mail.com / donor123</p></div>
<%@ include file="footer.jspf" %>
