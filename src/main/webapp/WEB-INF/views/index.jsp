<%-- Home page: hero banner with live totals, category chips, and a card for every LIVE campaign. Data comes from HomeServlet. --%><%@ include file="header.jspf" %>
<section class="hero"><h1>Fund hope. Verified.</h1><p>Every campaign and photo is checked by our admin team before it goes live.</p>
<p class="stats"><b>${hg:inr(totalRaised)}</b> raised &middot; <b>${donationCount}</b> recent contributions</p></section>
<div class="chips"><a class="${empty cat ? 'on' : ''}" href="${pageContext.request.contextPath}/">All</a>
<c:forEach var="e" items="${categories}"><a class="${cat == e.key ? 'on' : ''}" href="${pageContext.request.contextPath}/?cat=${fn:escapeXml(e.key)}"><c:out value="${e.key}"/> (${e.value})</a></c:forEach></div>
<div class="grid">
<c:forEach var="c" items="${campaigns}">
<a class="card" href="${pageContext.request.contextPath}/campaign?id=${c.id}">
<div class="cover ${fn:contains(c.category,'SMA') ? 'a' : 'b'}"><span class="badge">&#10004; Verified</span></div>
<div class="pad"><span class="pill"><c:out value="${c.category}"/></span><h3><c:out value="${c.title}"/></h3><div class="mut">by <c:out value="${c.ownerName}"/></div>
<div class="bar"><i style="width:${c.percent}%"></i></div><div class="mut"><b>${hg:inr(c.raised)}</b> raised &middot; ${c.percent}% of ${hg:inr(c.goal)}</div></div></a>
</c:forEach>
<c:if test="${empty campaigns}"><p class="mut">No campaigns found.</p></c:if></div>
<%@ include file="footer.jspf" %>
