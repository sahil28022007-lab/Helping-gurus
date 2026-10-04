<%-- Friendly error page used for 403, 404 and unexpected exceptions (see web.xml). --%><%@ page isErrorPage="true" %>
<%@ include file="header.jspf" %>
<div class="card pad narrow"><h2>Something went wrong</h2><p class="mut">${pageContext.errorData.statusCode == 403 ? 'You do not have access to this page.' : pageContext.errorData.statusCode == 404 ? 'We could not find that page.' : 'An unexpected error occurred. Please try again.'}</p><a class="btn" href="${pageContext.request.contextPath}/">Back to home</a></div>
<%@ include file="footer.jspf" %>
