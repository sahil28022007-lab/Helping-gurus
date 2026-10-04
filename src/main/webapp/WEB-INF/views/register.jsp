<%-- Registration form. Posts to AuthServlet (/register). The role list offers Contributor or Creator only, never Admin. --%><%@ include file="header.jspf" %>
<div class="card pad narrow"><h2>Create account</h2><c:if test="${not empty error}"><p class="err"><c:out value="${error}"/></p></c:if>
<form method="post" action="${pageContext.request.contextPath}/register"><label>Name</label><input name="name" required><label>Email</label><input type="email" name="email" required>
<label>Password (min 6)</label><input type="password" name="password" minlength="6" required><label>I want to</label>
<select name="role"><option value="CONTRIBUTOR">Support campaigns</option><option value="CREATOR">Start campaigns</option></select><button class="btn full">Register</button></form></div>
<%@ include file="footer.jspf" %>
