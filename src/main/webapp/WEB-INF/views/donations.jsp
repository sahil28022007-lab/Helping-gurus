<%-- My giving: table of the logged-in user's contributions and their total. Data comes from MyDonationsServlet. --%><%@ include file="header.jspf" %>
<div class="card pad"><h2>My contributions</h2><div class="tw"><table><tr><th>Date</th><th>Campaign</th><th>Amount</th></tr>
<c:forEach var="d" items="${donations}"><tr><td>${d.createdAt}</td><td><a href="${pageContext.request.contextPath}/campaign?id=${d.campaignId}"><c:out value="${d.campaignTitle}"/></a></td><td>${hg:inr(d.amount)}</td></tr></c:forEach></table></div>
<c:if test="${empty donations}"><p class="mut">Nothing yet. Support a campaign!</p></c:if><p><b>Total given: ${hg:inr(total)}</b></p></div>
<%@ include file="footer.jspf" %>
